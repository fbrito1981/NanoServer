package nano.server.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

import nano.server.db.access.DBException;
import nano.server.db.entities.Device;
import nano.server.db.entities.User;
import nano.server.db.services.def.IDeviceService;
import nano.server.dtos.DataTableRequestDto;
import nano.server.dtos.DataTableResponseDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.DeviceDto;
import nano.server.utils.ExceptionUtils;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.KeyUtils;

@Controller
public class DevicesController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(DevicesController.class);

	@Autowired
	private IDeviceService deviceService;
	
	@RequestMapping("/devices")
	public ModelAndView devices(ModelMap model, HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				if (userHasAccess(user)) {
					localizeModel(model, request);
					
					localizeModel(model, request, "data_table");
					
					loadMenu(model, request, user);
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
		
		return new ModelAndView("/devices/devices", model);
	}

	@RequestMapping(value = "/devices/list", method = { RequestMethod.POST })
	public @ResponseBody DataTableResponseDto<DeviceDto> list(HttpServletRequest request) {
		try {
			DataTableRequestDto dataTableRequest = new DataTableRequestDto(request);
			String search = KeyUtils.group(new String[] {
					getLoggedUserFromCookie(request).getEmail()
					});;
			String order = null;
			
			if (dataTableRequest.getSearch().getValue() != null && dataTableRequest.getSearch().getValue().length() > 0) {
				search = String.format(Locale.getDefault(), "%s%s",
						search, dataTableRequest.getSearch().getValue());
			}
			
			if (dataTableRequest.getOrder() != null && dataTableRequest.getOrder().size() > 0) {
				int column = dataTableRequest.getOrder().get(0).getColumn();
				order = String.format(Locale.getDefault(), "%s %s",
						dataTableRequest.getColumns().get(column).getData(),
						dataTableRequest.getOrder().get(0).getDir());
			}
			
			int devicesCounter = deviceService.countDevices(search);
			
			List<Device> devices = deviceService.allDevices(search, order,
					dataTableRequest.getLength(), dataTableRequest.getStart());
			
			List<DeviceDto> deviceDtos = new ArrayList<>();
			for (Device device : devices) {
				String activeLabel = device.isActive() ? "db_status_active" : "db_status_inactive";
				
				DeviceDto deviceDto = new DeviceDto(device);
				deviceDto.setActiveLabel(LocalizerUtils.getLocalizedText(request, activeLabel));
				
				deviceDtos.add(deviceDto);
			}
			
			return new DataTableResponseDto<DeviceDto>(dataTableRequest.getDraw(), devicesCounter, deviceDtos);
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new DataTableResponseDto<DeviceDto>(request, e);
		}
	}

	@RequestMapping(value = "/devices/create", method = { RequestMethod.POST })
	public @ResponseBody ResultDto create(HttpServletRequest request) {
		try {
			DeviceDto deviceDto = getObjectFromRequest(request, DeviceDto.class);
			
			Device existingDevice = deviceService.getDevice(deviceDto.getUuid());

			if (existingDevice != null) {
				return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "db_error_msg_duplicate_key"));
			} else {
				Device device = new Device(deviceDto.getUuid(), getLoggedUserFromCookie(request).getEmail(),
						deviceDto.getName(), deviceDto.getModel(), deviceDto.getOs(), deviceDto.getVersion());
				
				deviceService.setDevice(device);
				
				return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_create"));
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/devices/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request) {
		try {
			DeviceDto deviceDto = getObjectFromRequest(request, DeviceDto.class);
			
			Device device = deviceService.getDevice(deviceDto.getUuid());
			device.setUserEmail(getLoggedUserFromCookie(request).getEmail());
			device.setName(deviceDto.getName());
			device.setModel(deviceDto.getModel());
			device.setOs(deviceDto.getOs());
			device.setVersion(deviceDto.getVersion());
			device.setActive(deviceDto.getActive() != null ? deviceDto.getActive() : false);

			deviceService.setDevice(device);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_update"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/devices/remove", method = { RequestMethod.POST })
	public @ResponseBody ResultDto remove(HttpServletRequest request) {
		try {
			DeviceDto deviceDto = getObjectFromRequest(request, DeviceDto.class);
			
			try {
				deviceService.delDevice(deviceDto.getUuid());
				
				return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_remove"));
			} catch (DBException e) {
				if (ExceptionUtils.hasExceptionType(e, "MySQLIntegrityConstraintViolationException")) {
					return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "db_error_msg_foreign_key"));
				}
				
				throw e;
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/devices/randomUuid", method = { RequestMethod.GET })
	public @ResponseBody ResultDto randomUuid(HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		
		return new ResultDto(true, uuid.toString(), "");
	}

	@RequestMapping(value = "/devices/validateName", method = { RequestMethod.GET })
	public @ResponseBody ResultDto validateName(HttpServletRequest request, String name) {
		try {
			String userEmail = getLoggedUserFromCookie(request).getEmail();
			
			Device device = deviceService.getDeviceByUserEmailAndName(userEmail, name);
			
			return new ResultDto(true, device, "");
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}
}
