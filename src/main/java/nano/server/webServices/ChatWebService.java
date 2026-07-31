package nano.server.webServices;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nano.server.controllers.BaseController;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnergyLog;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.ChatRequestDto;
import nano.server.dtos.ChatResponseDto;
import nano.server.dtos.ChatTurnDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.EnergyViewType;
import nano.server.enums.EnvironmentViewType;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.KeyUtils;
import nano.server.utils.MapperUtils;
import nano.server.utils.WSSecurityUtils;

/**
 * Chat/LLM proxy for the Fuerz4 Assistant Android app. Holds the Grok (xAI) API key server-side
 * (never shipped in the public app/repo) and executes the same 5 "tools" the standalone
 * `mcp-server/server.py` MCP bridge exposes — but in-process against the already-autowired
 * device/log service beans, instead of calling out to that separate Python service. See
 * CLAUDE.md in the Fuerz4Assistant repo for the full rationale and the exact Grok request/
 * response shape this expects to be validated against.
 */
@Controller
public class ChatWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(ChatWebService.class);

	private static final int MAX_TOOL_ITERATIONS = 5;
	private static final String DEFAULT_MODEL = "grok-4-fast";
	private static final String GROK_URL = "https://api.x.ai/v1/chat/completions";
	private static final String[] DATE_PATTERNS = {
			"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd"
	};

	@Autowired
	private ISecurityLogService securityLogService;

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnergyLogService energyLogService;

	@Autowired
	private IEnvironmentLogService environmentLogService;

	@RequestMapping(value = "/api/services/chat/converse", method = { RequestMethod.POST })
	public @ResponseBody ResultDto converse(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					ChatRequestDto chatRequest = MapperUtils.getObject(data, ChatRequestDto.class);

					if (chatRequest.getMessage() != null && !chatRequest.getMessage().isEmpty()) {
						String reply = converseWithGrok(user, chatRequest);

						return new ResultDto(true, new ChatResponseDto(reply));
					} else {
						responseType = ResponseType.SC_INVALID_PARAMS;
					}
				} else {
					responseType = ResponseType.SC_INVALID_USER;
				}
			} else {
				responseType = ResponseType.SC_UNAUTHORIZED;
			}
		} catch (Exception e) {
			logException(LOGGER, e);

			responseType = ResponseType.SC_INTERNAL_SERVER_ERROR;
		}

		response.setStatus(responseType.intValue());

		return new ResultDto(false, null, responseType.strValue());
	}

	private boolean isAuthorized(HttpServletRequest request) throws Exception {
		return WSSecurityUtils.isHeaderValid(securityLogService, request,
				ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue());
	}

	// ---- Grok conversation + tool-calling loop -------------------------------------------------

	private String converseWithGrok(User user, ChatRequestDto chatRequest) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode messages = mapper.createArrayNode();

		if (chatRequest.getHistory() != null) {
			for (ChatTurnDto turn : chatRequest.getHistory()) {
				String role = "user".equalsIgnoreCase(turn.getRole()) ? "user" : "assistant";
				messages.add(buildMessage(mapper, role, turn.getText()));
			}
		}
		messages.add(buildMessage(mapper, "user", chatRequest.getMessage()));

		for (int iteration = 0; iteration < MAX_TOOL_ITERATIONS; iteration++) {
			ObjectNode requestBody = mapper.createObjectNode();
			requestBody.set("messages", messages);
			requestBody.set("tools", buildToolsDeclaration(mapper));

			JsonNode message = callGrok(mapper, requestBody).path("choices").path(0).path("message");
			JsonNode toolCalls = message.path("tool_calls");

			if (!toolCalls.isArray() || toolCalls.isEmpty()) {
				return message.path("content").asText("");
			}

			messages.add(message);

			for (JsonNode toolCall : toolCalls) {
				String toolCallId = toolCall.path("id").asText();
				String toolName = toolCall.path("function").path("name").asText();
				JsonNode args = mapper.readTree(toolCall.path("function").path("arguments").asText("{}"));
				ObjectNode toolResult = executeTool(mapper, user, toolName, args);

				ObjectNode toolMessage = mapper.createObjectNode();
				toolMessage.put("role", "tool");
				toolMessage.put("tool_call_id", toolCallId);
				toolMessage.put("content", mapper.writeValueAsString(toolResult));

				messages.add(toolMessage);
			}
		}

		throw new IllegalStateException("El asistente no devolvió una respuesta final tras varias llamadas a herramientas.");
	}

	private ObjectNode buildMessage(ObjectMapper mapper, String role, String text) {
		ObjectNode node = mapper.createObjectNode();
		node.put("role", role);
		node.put("content", text);
		return node;
	}

	private JsonNode callGrok(ObjectMapper mapper, ObjectNode requestBody) throws Exception {
		String apiKey = ServerProperties.GROK_API_KEY.getValue();
		String model = ServerProperties.GROK_MODEL.getValue();
		if (model == null || model.isEmpty()) {
			model = DEFAULT_MODEL;
		}
		requestBody.put("model", model);

		HttpPost post = new HttpPost(GROK_URL);
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Authorization", "Bearer " + apiKey);
		post.setEntity(new StringEntity(mapper.writeValueAsString(requestBody), StandardCharsets.UTF_8));

		try (CloseableHttpClient client = HttpClients.createDefault();
				CloseableHttpResponse httpResponse = client.execute(post)) {
			String body = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

			if (httpResponse.getStatusLine().getStatusCode() >= 300) {
				throw new IllegalStateException("Grok respondió con error: " + body);
			}

			return mapper.readTree(body);
		}
	}

	// ---- Tool declarations -----------------------------------------------------------------

	private ArrayNode buildToolsDeclaration(ObjectMapper mapper) {
		ArrayNode tools = mapper.createArrayNode();

		tools.add(wrapAsFunctionTool(mapper, declareListDevices(mapper)));
		tools.add(wrapAsFunctionTool(mapper, declareLatest(mapper, "get_home_temperature_humidity",
				"Devuelve la última lectura de temperatura y humedad de un dispositivo de ambiente.")));
		tools.add(wrapAsFunctionTool(mapper, declareHistory(mapper, "get_temperature_humidity_history",
				"Devuelve el historial de temperatura y humedad de un dispositivo de ambiente.")));
		tools.add(wrapAsFunctionTool(mapper, declareLatest(mapper, "get_home_energy_consumption",
				"Devuelve la última lectura de consumo eléctrico de un dispositivo de energía.")));
		tools.add(wrapAsFunctionTool(mapper, declareHistory(mapper, "get_energy_consumption_history",
				"Devuelve el historial de consumo eléctrico de un dispositivo de energía.")));

		return tools;
	}

	private ObjectNode wrapAsFunctionTool(ObjectMapper mapper, ObjectNode functionDeclaration) {
		ObjectNode tool = mapper.createObjectNode();
		tool.put("type", "function");
		tool.set("function", functionDeclaration);
		return tool;
	}

	private ObjectNode declareListDevices(ObjectMapper mapper) {
		ObjectNode declaration = mapper.createObjectNode();
		declaration.put("name", "list_devices");
		declaration.put("description", "Lista los dispositivos del usuario, opcionalmente filtrados por tipo.");

		ObjectNode parameters = declaration.putObject("parameters");
		parameters.put("type", "object");
		ObjectNode properties = parameters.putObject("properties");
		ObjectNode deviceType = properties.putObject("device_type");
		deviceType.put("type", "string");
		deviceType.putArray("enum").add("energy").add("environment");
		deviceType.put("description", "Tipo de dispositivo a filtrar (opcional).");

		return declaration;
	}

	private ObjectNode declareLatest(ObjectMapper mapper, String name, String description) {
		ObjectNode declaration = mapper.createObjectNode();
		declaration.put("name", name);
		declaration.put("description", description);

		ObjectNode parameters = declaration.putObject("parameters");
		parameters.put("type", "object");
		ObjectNode properties = parameters.putObject("properties");
		ObjectNode deviceUuid = properties.putObject("device_uuid");
		deviceUuid.put("type", "string");
		deviceUuid.put("description", "UUID del dispositivo. Si se omite, se usa el primer dispositivo del tipo correspondiente.");

		return declaration;
	}

	private ObjectNode declareHistory(ObjectMapper mapper, String name, String description) {
		ObjectNode declaration = mapper.createObjectNode();
		declaration.put("name", name);
		declaration.put("description", description);

		ObjectNode parameters = declaration.putObject("parameters");
		parameters.put("type", "object");
		ObjectNode properties = parameters.putObject("properties");

		ObjectNode fromDate = properties.putObject("from_date");
		fromDate.put("type", "string");
		fromDate.put("description", "Fecha/hora de inicio, formato ISO-8601 (ej: 2026-07-01).");

		ObjectNode untilDate = properties.putObject("until_date");
		untilDate.put("type", "string");
		untilDate.put("description", "Fecha/hora de fin, formato ISO-8601 (opcional, por defecto ahora).");

		ObjectNode viewType = properties.putObject("view_type");
		viewType.put("type", "string");
		viewType.putArray("enum").add("byMinute").add("byHour").add("byDay").add("byMonth").add("byYear");
		viewType.put("description", "Granularidad del historial (opcional, por defecto byDay).");

		ObjectNode deviceUuid = properties.putObject("device_uuid");
		deviceUuid.put("type", "string");
		deviceUuid.put("description", "UUID del dispositivo. Si se omite, se usa el primer dispositivo del tipo correspondiente.");

		parameters.putArray("required").add("from_date");

		return declaration;
	}

	// ---- Tool execution -----------------------------------------------------------------------

	private ObjectNode executeTool(ObjectMapper mapper, User user, String toolName, JsonNode args) {
		try {
			switch (toolName) {
			case "list_devices":
				return toolListDevices(mapper, user, textOrNull(args, "device_type"));
			case "get_home_temperature_humidity":
				return toolLatestEnvironment(mapper, user, textOrNull(args, "device_uuid"));
			case "get_temperature_humidity_history":
				return toolEnvironmentHistory(mapper, user, args);
			case "get_home_energy_consumption":
				return toolLatestEnergy(mapper, user, textOrNull(args, "device_uuid"));
			case "get_energy_consumption_history":
				return toolEnergyHistory(mapper, user, args);
			default:
				ObjectNode error = mapper.createObjectNode();
				error.put("error", "Herramienta desconocida: " + toolName);
				return error;
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			ObjectNode error = mapper.createObjectNode();
			error.put("error", "No se pudo ejecutar la herramienta: " + e.getMessage());
			return error;
		}
	}

	private ObjectNode toolListDevices(ObjectMapper mapper, User user, String deviceType) throws Exception {
		List<Device> devices = allDevicesForUser(user);
		ArrayNode array = mapper.createArrayNode();

		for (Device device : devices) {
			if (deviceType != null && !deviceType.equals(device.getType())) {
				continue;
			}
			ObjectNode node = array.addObject();
			node.put("uuid", device.getUuid());
			node.put("name", device.getName());
			node.put("type", device.getType());
			node.put("active", device.isActive());
		}

		ObjectNode result = mapper.createObjectNode();
		result.set("devices", array);
		return result;
	}

	private ObjectNode toolLatestEnvironment(ObjectMapper mapper, User user, String deviceUuid) throws Exception {
		String uuid = deviceUuid != null ? deviceUuid : firstDeviceUuid(user, "environment");
		if (uuid == null) {
			return errorNode(mapper, "El usuario no tiene dispositivos de ambiente.");
		}

		EnvironmentLog log = environmentLogService.getEnvironmentLog(uuid);
		if (log == null) {
			return errorNode(mapper, "No hay lecturas registradas para el dispositivo indicado.");
		}

		ObjectNode result = mapper.createObjectNode();
		result.put("device", uuid);
		result.put("temp", log.getTemp());
		result.put("hum", log.getHum());
		return result;
	}

	private ObjectNode toolLatestEnergy(ObjectMapper mapper, User user, String deviceUuid) throws Exception {
		String uuid = deviceUuid != null ? deviceUuid : firstDeviceUuid(user, "energy");
		if (uuid == null) {
			return errorNode(mapper, "El usuario no tiene dispositivos de energía.");
		}

		EnergyLog log = energyLogService.getEnergyLog(uuid);
		if (log == null) {
			return errorNode(mapper, "No hay lecturas registradas para el dispositivo indicado.");
		}

		ObjectNode result = mapper.createObjectNode();
		result.put("device", uuid);
		result.put("volts", log.getVolts());
		result.put("amps", log.getAmps());
		return result;
	}

	private ObjectNode toolEnvironmentHistory(ObjectMapper mapper, User user, JsonNode args) throws Exception {
		String deviceUuid = textOrNull(args, "device_uuid");
		String uuid = deviceUuid != null ? deviceUuid : firstDeviceUuid(user, "environment");
		if (uuid == null) {
			return errorNode(mapper, "El usuario no tiene dispositivos de ambiente.");
		}

		Date from = parseDate(textOrNull(args, "from_date"));
		Date until = parseDateOrNow(textOrNull(args, "until_date"));
		EnvironmentViewType viewType = EnvironmentViewType.fromValue(textOrDefault(args, "view_type", "byDay"));

		List<EnvironmentLog> logs;
		switch (viewType) {
		case BY_HOUR:
			logs = environmentLogService.allEnvironmentLogsByHours(uuid, from, until);
			break;
		case BY_DAY:
			logs = environmentLogService.allEnvironmentLogsByDays(uuid, from, until);
			break;
		case BY_MONTH:
			logs = environmentLogService.allEnvironmentLogsByMonths(uuid, from, until);
			break;
		case BY_YEAR:
			logs = environmentLogService.allEnvironmentLogsByYears(uuid, from, until);
			break;
		default:
			logs = environmentLogService.allEnvironmentLogsByMinutes(uuid, from, until);
			break;
		}

		ArrayNode array = mapper.createArrayNode();
		for (EnvironmentLog log : logs) {
			ObjectNode node = array.addObject();
			node.put("created", log.getCreated() != null ? log.getCreated().getTime() : null);
			node.put("temp", log.getTemp());
			node.put("hum", log.getHum());
		}

		ObjectNode result = mapper.createObjectNode();
		result.put("device", uuid);
		result.set("logs", array);
		return result;
	}

	private ObjectNode toolEnergyHistory(ObjectMapper mapper, User user, JsonNode args) throws Exception {
		String deviceUuid = textOrNull(args, "device_uuid");
		String uuid = deviceUuid != null ? deviceUuid : firstDeviceUuid(user, "energy");
		if (uuid == null) {
			return errorNode(mapper, "El usuario no tiene dispositivos de energía.");
		}

		Date from = parseDate(textOrNull(args, "from_date"));
		Date until = parseDateOrNow(textOrNull(args, "until_date"));
		EnergyViewType viewType = EnergyViewType.fromValue(textOrDefault(args, "view_type", "byDay"));

		List<EnergyLog> logs;
		switch (viewType) {
		case BY_HOUR:
			logs = energyLogService.allEnergyLogsByHours(uuid, from, until);
			break;
		case BY_DAY:
			logs = energyLogService.allEnergyLogsByDays(uuid, from, until);
			break;
		case BY_MONTH:
			logs = energyLogService.allEnergyLogsByMonths(uuid, from, until);
			break;
		case BY_YEAR:
			logs = energyLogService.allEnergyLogsByYears(uuid, from, until);
			break;
		default:
			logs = energyLogService.allEnergyLogsByMinutes(uuid, from, until);
			break;
		}

		ArrayNode array = mapper.createArrayNode();
		for (EnergyLog log : logs) {
			ObjectNode node = array.addObject();
			node.put("created", log.getCreated() != null ? log.getCreated().getTime() : null);
			node.put("volts", log.getVolts());
			node.put("amps", log.getAmps());
		}

		ObjectNode result = mapper.createObjectNode();
		result.put("device", uuid);
		result.set("logs", array);
		return result;
	}

	// ---- Helpers ------------------------------------------------------------------------------

	private List<Device> allDevicesForUser(User user) throws Exception {
		String search = KeyUtils.group(new String[] { user.getEmail() });
		int count = deviceService.countDevices(search);
		return count > 0 ? deviceService.allDevices(search, null, count, 0) : new ArrayList<>();
	}

	private String firstDeviceUuid(User user, String type) throws Exception {
		for (Device device : allDevicesForUser(user)) {
			if (type.equals(device.getType())) {
				return device.getUuid();
			}
		}
		return null;
	}

	private String textOrNull(JsonNode args, String field) {
		if (args == null || !args.hasNonNull(field)) {
			return null;
		}
		return args.get(field).asText();
	}

	private String textOrDefault(JsonNode args, String field, String defaultValue) {
		String value = textOrNull(args, field);
		return value != null ? value : defaultValue;
	}

	private Date parseDateOrNow(String value) {
		if (value == null) {
			return new Date();
		}
		Date parsed = parseDate(value);
		return parsed != null ? parsed : new Date();
	}

	private Date parseDate(String value) {
		if (value == null) {
			return null;
		}
		for (String pattern : DATE_PATTERNS) {
			try {
				return new SimpleDateFormat(pattern, Locale.US).parse(value);
			} catch (ParseException ignored) {
				// try the next pattern
			}
		}
		return null;
	}

	private ObjectNode errorNode(ObjectMapper mapper, String message) {
		ObjectNode node = mapper.createObjectNode();
		node.put("error", message);
		return node;
	}
}
