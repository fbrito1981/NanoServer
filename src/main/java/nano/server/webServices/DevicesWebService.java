package nano.server.webServices;

import nano.server.controllers.BaseController;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnergyLog;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;
import nano.server.db.services.def.IEnvironmentLogService;
import nano.server.dtos.EnvironmentDto;
import nano.server.dtos.EnergyDto;
import nano.server.dtos.ResultDto;
import nano.server.enums.ResponseType;
import nano.server.utils.MapperUtils;

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
