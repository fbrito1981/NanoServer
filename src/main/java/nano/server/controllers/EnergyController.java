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
import nano.server.db.entities.EnergyConsumed;
import nano.server.db.entities.EnergyLog;
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyConsumedService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.dtos.EnergyConsumedListDto;
import nano.server.dtos.EnergyFormDto;
import nano.server.dtos.EnergyLogDto;
import nano.server.dtos.EnergyLogsDto;
import nano.server.dtos.EnergyViewTypeDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.EnergyViewType;
import nano.server.utils.KeyUtils;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.SecureUtils;

@Controller
public class EnergyController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(EnergyController.class);

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnergyLogService energyLogService;

	@Autowired
	private IEnergyConsumedService energyConsumedService;
	
	@RequestMapping("/energy")
	public ModelAndView energy(ModelMap model, HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				if (userHasAccess(user)) {
					localizeModel(model, request);
					
					loadMenu(model, request, user);
					
					List<EnergyViewTypeDto> energyViewTypes = new ArrayList<>();
					for (EnergyViewType energyViewType : EnergyViewType.values()) {
						EnergyViewTypeDto energyViewTypeDto = new EnergyViewTypeDto(energyViewType, request);
						energyViewTypes.add(energyViewTypeDto);
					}
					
					model.put("energyViewTypes", energyViewTypes);
					model.put("energyViewTypeDefault", new EnergyViewTypeDto(EnergyViewType.BY_DAY, request));
					
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
							if ("energy".equals(device.getType())) {
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
		
		return new ModelAndView("/energy/energy", model);
	}

	@RequestMapping(value = "/energy/energy", method = { RequestMethod.POST })
	public @ResponseBody ResultDto energy(HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				EnergyFormDto energyFormDto = getObjectFromRequest(request, EnergyFormDto.class);
				
				if (energyFormDto != null) {
					List<EnergyConsumed> energyLogs = null;
					
					EnergyViewType energyViewType = energyFormDto.getEnergyViewType();
					String deviceUuid = energyFormDto.getDevice();
					Date from = energyFormDto.getFrom();
					Date until = energyFormDto.getUntil();
					TimeZone timeZone = TimeZone.getTimeZone(energyFormDto.getTimeZone());
					
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
					
					switch (energyViewType) {
					case BY_MINUTE:
						energyLogs = energyConsumedService.allEnergyLogsByMinutes(deviceUuid, from, until);
						break;
					case BY_HOUR:
						energyLogs = energyConsumedService.allEnergyLogsByHours(deviceUuid, from, until);
						break;
					case BY_DAY:
						energyLogs = energyConsumedService.allEnergyLogsByDays(deviceUuid, from, until);
						break;
					case BY_MONTH:
						energyLogs = energyConsumedService.allEnergyLogsByMonths(deviceUuid, from, until);
						break;
					case BY_YEAR:
						energyLogs = energyConsumedService.allEnergyLogsByYears(deviceUuid, from, until);
						break;
					}
					
					EnergyConsumedListDto energyConsumedListDto = new EnergyConsumedListDto(energyLogs);
					
					return new ResultDto(true, SecureUtils.encrypt(energyConsumedListDto));
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

	@RequestMapping(value = "/energy/data", method = { RequestMethod.POST })
	public @ResponseBody ResultDto data(HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				EnergyFormDto energyFormDto = getObjectFromRequest(request, EnergyFormDto.class);
				
				if (energyFormDto != null) {
					List<EnergyLog> energyLogs = null;
					
					EnergyViewType energyViewType = energyFormDto.getEnergyViewType();
					String deviceUuid = energyFormDto.getDevice();
					Date from = energyFormDto.getFrom();
					Date until = energyFormDto.getUntil();
					TimeZone timeZone = TimeZone.getTimeZone(energyFormDto.getTimeZone());
					
					if (until == null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(from);
						calendar.setTimeZone(timeZone);
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						
						from = calendar.getTime();
						
						calendar.add(Calendar.DATE, 1);
						
						until = calendar.getTime();
					}
					
					switch (energyViewType) {
					case BY_MINUTE:
						energyLogs = energyLogService.allEnergyLogsByMinutes(deviceUuid, from, until);
						break;
					case BY_HOUR:
						energyLogs = energyLogService.allEnergyLogsByHours(deviceUuid, from, until);
						break;
					case BY_DAY:
						energyLogs = energyLogService.allEnergyLogsByDays(deviceUuid, from, until);
						break;
					case BY_MONTH:
						energyLogs = energyLogService.allEnergyLogsByMonths(deviceUuid, from, until);
						break;
					case BY_YEAR:
						energyLogs = energyLogService.allEnergyLogsByYears(deviceUuid, from, until);
						break;
					}
					
					EnergyLogsDto energyLogsDto = new EnergyLogsDto(energyLogs);
					
					return new ResultDto(true, SecureUtils.encrypt(energyLogsDto));
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

	@RequestMapping(value = "/energy/instant", method = { RequestMethod.POST })
	public @ResponseBody ResultDto instant(HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				EnergyFormDto energyFormDto = getObjectFromRequest(request, EnergyFormDto.class);
				
				if (energyFormDto != null) {
					EnergyLog energyLog = null;
					
					String deviceUuid = energyFormDto.getDevice();
					
					energyLog = energyLogService.getEnergyLog(deviceUuid);
					
					EnergyLogDto log = new EnergyLogDto(energyLog);
					
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
