package nano.server.webServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.DeviceHistoryRequestDto;
import nano.server.dtos.DeviceLatestRequestDto;
import nano.server.dtos.EnergyLogDto;
import nano.server.dtos.EnergyLogsDto;
import nano.server.dtos.EnvironmentLogDto;
import nano.server.dtos.EnvironmentLogsDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.EnergyViewType;
import nano.server.enums.EnvironmentViewType;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.MapperUtils;
import nano.server.utils.WSSecurityUtils;

/**
 * Mobile-facing device history/latest-value reads for the Fuerz4 Assistant app's device detail
 * screen. WSSE + LoginToken authenticated like {@link DevicesWebService}, with the same
 * ownership check (loaded device's userEmail must match the resolved user) rather than trusting
 * a client-supplied uuid. Dispatches to {@link IEnergyLogService}/{@link IEnvironmentLogService}
 * exactly like {@link ChatWebService}'s toolEnergyHistory/toolEnvironmentHistory — that date
 * parsing/switch shape is intentionally duplicated here rather than extracted, matching this
 * codebase's existing precedent of not refactoring ChatWebService/McpWebService's overlapping
 * dispatch logic without a test harness.
 */
@Controller
public class DeviceHistoryWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(DeviceHistoryWebService.class);

	private static final String[] DATE_PATTERNS = {
			"yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ssXXX", "yyyy-MM-dd"
	};

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnergyLogService energyLogService;

	@Autowired
	private IEnvironmentLogService environmentLogService;

	@Autowired
	private ISecurityLogService securityLogService;

	@RequestMapping(value = "/api/services/devices/latestValue", method = { RequestMethod.POST })
	public @ResponseBody ResultDto latestValue(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceLatestRequestDto requestDto = MapperUtils.getObject(data, DeviceLatestRequestDto.class);
					Device device = requestDto.getDeviceUuid() != null
							? deviceService.getDevice(requestDto.getDeviceUuid())
							: null;

					if (device != null && device.getUserEmail().equals(user.getEmail())) {
						if ("energy".equals(device.getType())) {
							EnergyLog log = energyLogService.getEnergyLog(device.getUuid());
							return new ResultDto(true, log != null ? new EnergyLogDto(log) : null);
						} else {
							EnvironmentLog log = environmentLogService.getEnvironmentLog(device.getUuid());
							return new ResultDto(true, log != null ? new EnvironmentLogDto(log) : null);
						}
					} else {
						responseType = ResponseType.SC_INVALID_DEVICE;
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

	@RequestMapping(value = "/api/services/devices/history", method = { RequestMethod.POST })
	public @ResponseBody ResultDto history(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceHistoryRequestDto requestDto = MapperUtils.getObject(data, DeviceHistoryRequestDto.class);
					Device device = requestDto.getDeviceUuid() != null
							? deviceService.getDevice(requestDto.getDeviceUuid())
							: null;

					if (device != null && device.getUserEmail().equals(user.getEmail())) {
						// A missing fromDate means "unbounded" to the Android client (the "Todo" range) — the
						// log DAOs don't null-check their date params, so substitute the epoch rather than pass null.
						Date from = parseDate(requestDto.getFromDate());
						if (from == null) {
							from = new Date(0L);
						}
						Date until = parseDateOrNow(requestDto.getUntilDate());

						if ("energy".equals(device.getType())) {
							EnergyViewType viewType = EnergyViewType.fromValue(
									requestDto.getViewType() != null ? requestDto.getViewType() : "byDay");
							List<EnergyLog> logs;
							switch (viewType) {
							case BY_HOUR:
								logs = energyLogService.allEnergyLogsByHours(device.getUuid(), from, until);
								break;
							case BY_DAY:
								logs = energyLogService.allEnergyLogsByDays(device.getUuid(), from, until);
								break;
							case BY_MONTH:
								logs = energyLogService.allEnergyLogsByMonths(device.getUuid(), from, until);
								break;
							case BY_YEAR:
								logs = energyLogService.allEnergyLogsByYears(device.getUuid(), from, until);
								break;
							default:
								logs = energyLogService.allEnergyLogsByMinutes(device.getUuid(), from, until);
								break;
							}
							return new ResultDto(true, new EnergyLogsDto(logs));
						} else {
							EnvironmentViewType viewType = EnvironmentViewType.fromValue(
									requestDto.getViewType() != null ? requestDto.getViewType() : "byDay");
							List<EnvironmentLog> logs;
							switch (viewType) {
							case BY_HOUR:
								logs = environmentLogService.allEnvironmentLogsByHours(device.getUuid(), from, until);
								break;
							case BY_DAY:
								logs = environmentLogService.allEnvironmentLogsByDays(device.getUuid(), from, until);
								break;
							case BY_MONTH:
								logs = environmentLogService.allEnvironmentLogsByMonths(device.getUuid(), from, until);
								break;
							case BY_YEAR:
								logs = environmentLogService.allEnvironmentLogsByYears(device.getUuid(), from, until);
								break;
							default:
								logs = environmentLogService.allEnvironmentLogsByMinutes(device.getUuid(), from, until);
								break;
							}
							return new ResultDto(true, new EnvironmentLogsDto(logs));
						}
					} else {
						responseType = ResponseType.SC_INVALID_DEVICE;
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
}
