package nano.server.webServices;

import java.util.Date;

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
import nano.server.db.entities.User;
import nano.server.db.services.def.ISecurityLogService;
import nano.server.dtos.LoginDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.TokenDto;
import nano.server.dtos.UserDto;
import nano.server.dtos.ValidateCodeDto;
import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.enums.ResponseType;
import nano.server.enums.ServerProperties;
import nano.server.utils.EmailUtils;
import nano.server.utils.SecureUtils;
import nano.server.utils.UrlUtils;
import nano.server.utils.WSSecurityUtils;

@Controller
public class LoginWebService extends BaseController {
	private static final Logger LOGGER = LogManager.getLogger(LoginWebService.class);
	private static final int MAX_REQUEST_CODE_TIME = 2 * 60 * 60 * 1000; // 2 hours

	@Autowired
	private ISecurityLogService securityLogService;
	
	@RequestMapping(value = "/api/services/login/login", method = { RequestMethod.POST })
	public @ResponseBody ResultDto login(HttpServletRequest request, HttpServletResponse response, String encryptedData) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				LoginDto loginDto = SecureUtils.decrypt(encryptedData, LoginDto.class);
				
				User user = userService.getUser(loginDto.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getPass().equals(loginDto.getPass())) {
							String token = SecureUtils.getToken(user.getEmail());
							
							user.setToken(token);
							user.setResetCode(null);
							
							userService.setUser(user, SecureUtils.getDbKey());
							
							addTokenToResponse(token, response);
							
							String picture = null;
							if (user.getPicture() != null) {
								picture = UrlUtils.getFilePath(FileType.IMAGES, EntityType.USERS, user.getPicture());
							}
							
							UserDto userDto = new UserDto();
							userDto.setEmail(user.getEmail());
							userDto.setName(user.getName());
							userDto.setPicture(picture);

							return new ResultDto(true, SecureUtils.encrypt(userDto), null);
						} else {
							responseType = ResponseType.SC_INVALID_CREDENTIALS;
						}
					} else {
						responseType = ResponseType.SC_INACTIVE_USER;
					}
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
	
	@RequestMapping(value = "/api/services/login/tokenLogin", method = { RequestMethod.POST })
	public @ResponseBody ResultDto tokenLogin(HttpServletRequest request, HttpServletResponse response, String encryptedData) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				TokenDto tokenDto = SecureUtils.decrypt(encryptedData, TokenDto.class);
				
				String email = SecureUtils.getEmail(tokenDto.getToken());
				
				User user = userService.getUser(email, SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getToken().equals(tokenDto.getToken())) {
							addTokenToResponse(tokenDto.getToken(), response);
							
							String picture = null;
							if (user.getPicture() != null) {
								picture = UrlUtils.getFilePath(FileType.IMAGES, EntityType.USERS, user.getPicture());
							}
							
							UserDto userDto = new UserDto();
							userDto.setEmail(user.getEmail());
							userDto.setName(user.getName());
							userDto.setPicture(picture);

							return new ResultDto(true, SecureUtils.encrypt(userDto), null);
						} else {
							responseType = ResponseType.SC_INVALID_CREDENTIALS;
						}
					} else {
						responseType = ResponseType.SC_INACTIVE_USER;
					}
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
	
	@RequestMapping(value = "/api/services/login/requestCode", method = { RequestMethod.POST })
	public @ResponseBody ResultDto requestCode(HttpServletRequest request, HttpServletResponse response, String encryptedData) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				LoginDto loginDto = SecureUtils.decrypt(encryptedData, LoginDto.class);
				
				User user = userService.getUser(loginDto.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						int resetCode = SecureUtils.getCode();
						
						user.setResetCode(resetCode);
						
						userService.setUser(user, SecureUtils.getDbKey());
						
						LOGGER.info("Reset code is: {}.", resetCode);

						EmailUtils.sendResetCodeEmail(this, request, user.getEmail(), resetCode);

						return new ResultDto(true, null, null);
					} else {
						responseType = ResponseType.SC_INACTIVE_USER;
					}
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
	
	@RequestMapping(value = "/api/services/login/validateCode", method = { RequestMethod.POST })
	public @ResponseBody ResultDto validateCode(HttpServletRequest request, HttpServletResponse response, String encryptedData) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				ValidateCodeDto validateCodeDto = SecureUtils.decrypt(encryptedData, ValidateCodeDto.class);
				
				User user = userService.getUser(validateCodeDto.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getResetCode() != null && user.getResetCode().equals(validateCodeDto.getCode())) {
							long timestamp = new Date().getTime();
							long difference = timestamp - user.getUpdated().getTime();
							
							if (difference < MAX_REQUEST_CODE_TIME) {
								user.setResetCode(0);
								
								userService.setUser(user, SecureUtils.getDbKey());
								
								return new ResultDto(true, null, null);
							} else {
								responseType = ResponseType.SC_EXPIRED_RECOVERY_CODE;
							}
						} else {
							responseType = ResponseType.SC_INVALID_RECOVERY_CODE;
						}
					} else {
						responseType = ResponseType.SC_INACTIVE_USER;
					}
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
	
	@RequestMapping(value = "/api/services/login/resetPassword", method = { RequestMethod.POST })
	public @ResponseBody ResultDto resetPassword(HttpServletRequest request, HttpServletResponse response, String encryptedData) {
		ResponseType responseType;
		
		try {
			if (WSSecurityUtils.isHeaderValid(securityLogService, request,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue())) {
				LoginDto loginDto = SecureUtils.decrypt(encryptedData, LoginDto.class);
				
				User user = userService.getUser(loginDto.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getResetCode() != null && user.getResetCode().equals(0)) {
							user.setPass(loginDto.getPass());
							user.setResetCode(null);
							user.setToken(null);
							
							userService.setUser(user, SecureUtils.getDbKey());
							
							return new ResultDto(true, null, null);
						} else {
							responseType = ResponseType.SC_INVALID_RECOVERY_CODE;
						}
					} else {
						responseType = ResponseType.SC_INACTIVE_USER;
					}
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
}
