package nano.server.webServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nano.server.controllers.BaseController;
import nano.server.db.entities.Role;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.RegistrationDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.UserDto;
import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.EmailUtils;
import nano.server.utils.FileUtils;
import nano.server.utils.MapperUtils;
import nano.server.utils.SecureUtils;
import nano.server.utils.WSSecurityUtils;

@Controller
public class UsersWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(UsersWebService.class);
	private static final String USER_ROLE_NAME = "User";

	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private ISecurityLogService securityLogService;
	
	@RequestMapping(value = "/api/services/users/registration", method = { RequestMethod.POST })
	public @ResponseBody ResultDto registration(HttpServletRequest request, HttpServletResponse response, String encryptedData, String images) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				RegistrationDto registrationDto = SecureUtils.decrypt(encryptedData, RegistrationDto.class);
				UserDto imagesDto = MapperUtils.getObject(images, UserDto.class);

				UserDto userDto = registrationDto.getUser();
				
				User user = userService.getUser(userDto.getEmail(), SecureUtils.getDbKey());
				
				if (user == null) {
					String randomPass = SecureUtils.getRanbomPass();
					Role role = roleService.getRole(USER_ROLE_NAME);
					
					String picture = null;
					if (imagesDto.getPicture() != null && !imagesDto.getPicture().isEmpty()) {
						picture = FileUtils.saveImage(EntityType.USERS, userDto.getName(), imagesDto.getPicture());
					}
					
					user = new User(userDto.getEmail(), randomPass, userDto.getName(), role, picture);
					
					userService.setUser(user, SecureUtils.getDbKey());
					
					EmailUtils.sendNewAccountEmail(this, request, user.getEmail(), randomPass);
					
					return new ResultDto(true, null, null);
				} else {
					responseType = ResponseType.SC_INVALID_CREDENTIALS;
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
	
	@RequestMapping(value = "/api/services/users/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request, HttpServletResponse response, String encryptedData, String images) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				User user = getLoggedUserFromHeader(request);
				
				if (user != null && user.isActive()) {
					UserDto userDto = SecureUtils.decrypt(encryptedData, UserDto.class);
					UserDto imagesDto = MapperUtils.getObject(images, UserDto.class);
					
					user.setName(userDto.getName());

					String picture = user.getPicture();
					if (userDto.getPictureRemoved() != null && userDto.getPictureRemoved()) {
						FileUtils.deleteFile(FileType.IMAGES, EntityType.USERS, user.getPicture());
						picture = null;
					}
					if (imagesDto.getPicture() != null && !imagesDto.getPicture().isEmpty()) {
						picture = FileUtils.saveImage(EntityType.USERS, userDto.getName(), imagesDto.getPicture());
						
						if (user.getPicture() != null) {
							FileUtils.deleteFile(FileType.IMAGES, EntityType.USERS, user.getPicture());
						}
					}
					
					user.setPicture(picture);
					
					userService.setUser(user, SecureUtils.getDbKey());
					
					return new ResultDto(true, null, null);
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
}
