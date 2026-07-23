package nano.server.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import nano.server.db.entities.Device;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.dtos.EnvironmentFormDto;
import nano.server.dtos.EnvironmentLogDto;
import nano.server.dtos.EnvironmentLogsDto;
import nano.server.dtos.EnvironmentViewTypeDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.EnvironmentViewType;
import nano.server.utils.KeyUtils;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.SecureUtils;

@Controller
public class EnvironmentController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(EnvironmentController.class);

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnvironmentLogService environmentLogService;

	@RequestMapping("/environment")
	public ModelAndView environment(ModelMap model, HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);

			if (user != null) {
				if (userHasAccess(user)) {
					localizeModel(model, request);

					loadMenu(model, request, user);

					List<EnvironmentViewTypeDto> environmentViewTypes = new ArrayList<>();
					for (EnvironmentViewType environmentViewType : EnvironmentViewType.values()) {
						EnvironmentViewTypeDto environmentViewTypeDto = new EnvironmentViewTypeDto(environmentViewType, request);
						environmentViewTypes.add(environmentViewTypeDto);
					}

					model.put("environmentViewTypes", environmentViewTypes);
					model.put("environmentViewTypeDefault", new EnvironmentViewTypeDto(EnvironmentViewType.BY_DAY, request));

					Calendar calendar = Calendar.getInstance();

					model.put("toDate", calendar.getTime());

					calendar.add(Calendar.MONTH, -1);

					model.put("fromDate", calendar.getTime());

					String search = KeyUtils.group(new String[] { user.getEmail() });

					int count = deviceService.countDevices(search);

					if (count > 0) {
						List<Device> allDevices = deviceService.allDevices(search, null, count, 0);
						List<Device> devices = new ArrayList<>();
						for (Device device : allDevices) {
							if ("environment".equals(device.getType())) {
								devices.add(device);
							}
						}
						model.put("devices", devices);
					}
				} else {
					return new ModelAndView("redirect:/dashboard?irs=" + getControllerPath(), model);
				}
			} else {
				return new ModelAndView("redirect:/logout?ws=1", model);
			}
		} catch (Exception e) {
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_general"));

			logException(LOGGER, e);
		}

		return new ModelAndView("/environment/environment", model);
	}

	@RequestMapping(value = "/environment/data", method = { RequestMethod.POST })
	public @ResponseBody ResultDto data(HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);

			if (user != null) {
				EnvironmentFormDto environmentFormDto = getObjectFromRequest(request, EnvironmentFormDto.class);

				if (environmentFormDto != null) {
					List<EnvironmentLog> environmentLogs = null;

					EnvironmentViewType environmentViewType = environmentFormDto.getEnvironmentViewType();
					String deviceUuid = environmentFormDto.getDevice();
					Date from = environmentFormDto.getFrom();
					Date until = environmentFormDto.getUntil();
					TimeZone timeZone = TimeZone.getTimeZone(environmentFormDto.getTimeZone());

					Calendar calendar = Calendar.getInstance();
					calendar.setTime(from);
					calendar.setTimeZone(timeZone);
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);

					from = calendar.getTime();

					if (until == null) {
						calendar.add(Calendar.DATE, 1);

						until = calendar.getTime();
					} else {
						calendar.setTime(until);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);

						until = calendar.getTime();
					}

					switch (environmentViewType) {
					case BY_MINUTE:
						environmentLogs = environmentLogService.allEnvironmentLogsByMinutes(deviceUuid, from, until);
						break;
					case BY_HOUR:
						environmentLogs = environmentLogService.allEnvironmentLogsByHours(deviceUuid, from, until);
						break;
					case BY_DAY:
						environmentLogs = environmentLogService.allEnvironmentLogsByDays(deviceUuid, from, until);
						break;
					case BY_MONTH:
						environmentLogs = environmentLogService.allEnvironmentLogsByMonths(deviceUuid, from, until);
						break;
					case BY_YEAR:
						environmentLogs = environmentLogService.allEnvironmentLogsByYears(deviceUuid, from, until);
						break;
					}

					EnvironmentLogsDto environmentLogsDto = new EnvironmentLogsDto(environmentLogs);

					return new ResultDto(true, SecureUtils.encrypt(environmentLogsDto));
				} else {
					return new ResultDto(false, "", "Invalid form submit.");
				}
			} else {
				return new ResultDto(false, "", "User no longer logged.");
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			return new ResultDto(false, "", "Exception occurred.");
		}
	}

	@RequestMapping(value = "/environment/instant", method = { RequestMethod.POST })
	public @ResponseBody ResultDto instant(HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);

			if (user != null) {
				EnvironmentFormDto environmentFormDto = getObjectFromRequest(request, EnvironmentFormDto.class);

				if (environmentFormDto != null) {
					String deviceUuid = environmentFormDto.getDevice();

					EnvironmentLog environmentLog = environmentLogService.getEnvironmentLog(deviceUuid);

					EnvironmentLogDto log = new EnvironmentLogDto(environmentLog);

					return new ResultDto(true, SecureUtils.encrypt(log));
				} else {
					return new ResultDto(false, "", "Invalid form submit.");
				}
			} else {
				return new ResultDto(false, "", "User no longer logged.");
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			return new ResultDto(false, "", "Exception occurred.");
		}
	}
}
