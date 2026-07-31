package nano.server.webServices;

import java.util.ArrayList;
import java.util.List;

import nano.server.controllers.BaseController;
import nano.server.db.access.DBException;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnergyLog;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.DeviceDto;
import nano.server.dtos.DeviceListDto;
import nano.server.dtos.DeviceListRequestDto;
import nano.server.dtos.DeviceRemoveRequestDto;
import nano.server.dtos.EnvironmentDto;
import nano.server.dtos.EnergyDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.ExceptionUtils;
import nano.server.utils.KeyUtils;
import nano.server.utils.MapperUtils;
import nano.server.utils.WSSecurityUtils;

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

@Controller
public class DevicesWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(DevicesWebService.class);

	@Autowired
	private IDeviceService deviceService;

	@Autowired
	private IEnergyLogService energyLogService;

	@Autowired
	private IEnvironmentLogService environmentLogService;

	@Autowired
	private ISecurityLogService securityLogService;

	/**
	 * Mobile-facing CRUD for devices (Fuerz4 Assistant app). WSSE-gated like Login/Users/Mcp,
	 * plus a LoginToken header to resolve the owning user server-side — never trust a
	 * client-supplied email for these mutating endpoints.
	 */
	@RequestMapping(value = "/api/services/devices/list", method = { RequestMethod.POST })
	public @ResponseBody ResultDto list(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceListRequestDto requestDto = MapperUtils.getObject(data, DeviceListRequestDto.class);
					String search = KeyUtils.group(new String[] { user.getEmail() });

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

	@RequestMapping(value = "/api/services/devices/create", method = { RequestMethod.POST })
	public @ResponseBody ResultDto create(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceDto deviceDto = MapperUtils.getObject(data, DeviceDto.class);

					if (deviceDto.getUuid() != null && deviceDto.getName() != null) {
						Device existingDevice = deviceService.getDevice(deviceDto.getUuid());

						if (existingDevice != null) {
							responseType = ResponseType.SC_DUPLICATE_DEVICE;
						} else {
							Device device = new Device(deviceDto.getUuid(), user.getEmail(), deviceDto.getName(),
									deviceDto.getModel(), deviceDto.getOs(), deviceDto.getVersion());

							if (deviceDto.getSettings() != null) {
								device.setSettings(MapperUtils.getString(deviceDto.getSettings()));
							}

							deviceService.setDevice(device);

							Device created = deviceService.getDevice(deviceDto.getUuid());

							return new ResultDto(true, new DeviceDto(created));
						}
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

	@RequestMapping(value = "/api/services/devices/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceDto deviceDto = MapperUtils.getObject(data, DeviceDto.class);
					Device device = deviceDto.getUuid() != null ? deviceService.getDevice(deviceDto.getUuid()) : null;

					if (device != null && device.getUserEmail().equals(user.getEmail())) {
						device.setName(deviceDto.getName());
						if (deviceDto.getModel() != null) {
							device.setModel(deviceDto.getModel());
						}
						if (deviceDto.getOs() != null) {
							device.setOs(deviceDto.getOs());
						}
						if (deviceDto.getVersion() != null) {
							device.setVersion(deviceDto.getVersion());
						}
						if (deviceDto.getActive() != null) {
							device.setActive(deviceDto.getActive());
						}
						if (deviceDto.getSettings() != null) {
							device.setSettings(MapperUtils.getString(deviceDto.getSettings()));
						}

						deviceService.setDevice(device);

						return new ResultDto(true, new DeviceDto(deviceService.getDevice(device.getUuid())));
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

	@RequestMapping(value = "/api/services/devices/remove", method = { RequestMethod.POST })
	public @ResponseBody ResultDto remove(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			if (isAuthorized(request)) {
				User user = getLoggedUserFromHeader(request);

				if (user != null) {
					DeviceRemoveRequestDto requestDto = MapperUtils.getObject(data, DeviceRemoveRequestDto.class);
					Device device = requestDto.getUuid() != null ? deviceService.getDevice(requestDto.getUuid()) : null;

					if (device != null && device.getUserEmail().equals(user.getEmail())) {
						try {
							deviceService.delDevice(device.getUuid());

							return new ResultDto(true, null);
						} catch (DBException e) {
							if (ExceptionUtils.hasExceptionType(e, "MySQLIntegrityConstraintViolationException")) {
								responseType = ResponseType.SC_INVALID_PARAMS;
							} else {
								throw e;
							}
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

	@RequestMapping(value = "/api/services/devices/energyRecord", method = { RequestMethod.POST })
	public @ResponseBody ResultDto energyRecord(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;
		
		try {
			EnergyDto energyDto = MapperUtils.getObject(data, EnergyDto.class);			
			
			if (energyDto.getId() != null) {
				Device device = deviceService.getDevice(energyDto.getId());
				
				if (device != null) {
					EnergyLog energyLog = new EnergyLog(device, energyDto);
					
					energyLogService.setEnergyLog(energyLog);
					
					return new ResultDto(true, "", null);
				} else {
					responseType = ResponseType.SC_INVALID_DEVICE;
				}
			} else {
				responseType = ResponseType.SC_INVALID_PARAMS;
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			
			responseType = ResponseType.SC_INTERNAL_SERVER_ERROR;
		}

		response.setStatus(responseType.intValue());

		return new ResultDto(false, null, responseType.strValue());
	}

	@RequestMapping(value = "/api/services/devices/tempRecord", method = { RequestMethod.POST })
	public @ResponseBody ResultDto tempRecord(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String data) {
		ResponseType responseType;

		try {
			EnvironmentDto environmentDto = MapperUtils.getObject(data, EnvironmentDto.class);

			if (environmentDto.getId() != null) {
				Device device = deviceService.getDevice(environmentDto.getId());

				if (device != null) {
					EnvironmentLog environmentLog = new EnvironmentLog(device, environmentDto);

					environmentLogService.setEnvironmentLog(environmentLog);

					return new ResultDto(true, "", null);
				} else {
					responseType = ResponseType.SC_INVALID_DEVICE;
				}
			} else {
				responseType = ResponseType.SC_INVALID_PARAMS;
			}
		} catch (Exception e) {
			logException(LOGGER, e);

			responseType = ResponseType.SC_INTERNAL_SERVER_ERROR;
		}

		response.setStatus(responseType.intValue());

		return new ResultDto(false, null, responseType.strValue());
	}
}
