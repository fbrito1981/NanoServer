package nano.server.webServices;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nano.server.controllers.BaseController;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnergyLog;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.DeviceListDto;
import nano.server.dtos.EnergyFormDto;
import nano.server.dtos.EnergyLogDto;
import nano.server.dtos.EnergyLogsDto;
import nano.server.dtos.EnvironmentFormDto;
import nano.server.dtos.EnvironmentLogDto;
import nano.server.dtos.EnvironmentLogsDto;
import nano.server.dtos.McpDeviceListRequestDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.EnergyViewType;
import nano.server.enums.EnvironmentViewType;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.KeyUtils;
import nano.server.utils.MapperUtils;
import nano.server.utils.WSSecurityUtils;

@Controller
public class McpWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(McpWebService.class);

	@Autowired
	private ISecurityLogService securityLogService;

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnergyLogService energyLogService;

	@Autowired
	private IEnvironmentLogService environmentLogService;

	@RequestMapping(value = "/api/services/mcp/devices", method = { RequestMethod.POST })
	public @ResponseBody ResultDto devices(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				McpDeviceListRequestDto requestDto = MapperUtils.getObject(data, McpDeviceListRequestDto.class);

				if (requestDto.getUserEmail() != null) {
					String search = KeyUtils.group(new String[] { requestDto.getUserEmail() });

					int count = deviceService.countDevices(search);
					List<Device> allDevices = count > 0
							? deviceService.allDevices(search, null, count, 0)
							: new ArrayList<>();

					List<Device> devices = new ArrayList<>();
					for (Device device : allDevices) {
						if (requestDto.getType() == null || requestDto.getType().equals(device.getType())) {
							devices.add(device);
						}
					}

					return new ResultDto(true, new DeviceListDto(devices));
				} else {
					responseType = ResponseType.SC_INVALID_PARAMS;
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

	@RequestMapping(value = "/api/services/mcp/environment/latest", method = { RequestMethod.POST })
	public @ResponseBody ResultDto environmentLatest(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				EnvironmentFormDto formDto = MapperUtils.getObject(data, EnvironmentFormDto.class);

				if (formDto.getDevice() != null) {
					EnvironmentLog environmentLog = environmentLogService.getEnvironmentLog(formDto.getDevice());

					return new ResultDto(true, environmentLog != null ? new EnvironmentLogDto(environmentLog) : null);
				} else {
					responseType = ResponseType.SC_INVALID_PARAMS;
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

	@RequestMapping(value = "/api/services/mcp/environment/history", method = { RequestMethod.POST })
	public @ResponseBody ResultDto environmentHistory(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				EnvironmentFormDto formDto = MapperUtils.getObject(data, EnvironmentFormDto.class);

				if (formDto.getDevice() != null && formDto.getFrom() != null) {
					List<EnvironmentLog> logs = getEnvironmentLogs(formDto);

					return new ResultDto(true, new EnvironmentLogsDto(logs));
				} else {
					responseType = ResponseType.SC_INVALID_PARAMS;
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

	@RequestMapping(value = "/api/services/mcp/energy/latest", method = { RequestMethod.POST })
	public @ResponseBody ResultDto energyLatest(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				EnergyFormDto formDto = MapperUtils.getObject(data, EnergyFormDto.class);

				if (formDto.getDevice() != null) {
					EnergyLog energyLog = energyLogService.getEnergyLog(formDto.getDevice());

					return new ResultDto(true, energyLog != null ? new EnergyLogDto(energyLog) : null);
				} else {
					responseType = ResponseType.SC_INVALID_PARAMS;
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

	@RequestMapping(value = "/api/services/mcp/energy/history", method = { RequestMethod.POST })
	public @ResponseBody ResultDto energyHistory(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				EnergyFormDto formDto = MapperUtils.getObject(data, EnergyFormDto.class);

				if (formDto.getDevice() != null && formDto.getFrom() != null) {
					List<EnergyLog> logs = getEnergyLogs(formDto);

					return new ResultDto(true, new EnergyLogsDto(logs));
				} else {
					responseType = ResponseType.SC_INVALID_PARAMS;
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

	private List<EnvironmentLog> getEnvironmentLogs(EnvironmentFormDto formDto) throws Exception {
		EnvironmentViewType viewType = formDto.getEnvironmentViewType();

		switch (viewType) {
		case BY_HOUR:
			return environmentLogService.allEnvironmentLogsByHours(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_DAY:
			return environmentLogService.allEnvironmentLogsByDays(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_MONTH:
			return environmentLogService.allEnvironmentLogsByMonths(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_YEAR:
			return environmentLogService.allEnvironmentLogsByYears(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		default:
			return environmentLogService.allEnvironmentLogsByMinutes(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		}
	}

	private List<EnergyLog> getEnergyLogs(EnergyFormDto formDto) throws Exception {
		EnergyViewType viewType = formDto.getEnergyViewType();

		switch (viewType) {
		case BY_HOUR:
			return energyLogService.allEnergyLogsByHours(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_DAY:
			return energyLogService.allEnergyLogsByDays(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_MONTH:
			return energyLogService.allEnergyLogsByMonths(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		case BY_YEAR:
			return energyLogService.allEnergyLogsByYears(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		default:
			return energyLogService.allEnergyLogsByMinutes(formDto.getDevice(), formDto.getFrom(), formDto.getUntil());
		}
	}
}
