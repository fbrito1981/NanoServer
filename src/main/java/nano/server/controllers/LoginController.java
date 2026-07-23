package nano.server.controllers;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import nano.server.db.entities.User;
import nano.server.dtos.LoginDto;
import nano.server.dtos.RedirectDto;
import nano.server.dtos.ResponseDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.ValidateCodeDto;
import nano.server.utils.EmailUtils;
import nano.server.utils.ExceptionUtils;
import nano.server.utils.IpAddressUtils;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.SecureUtils;

@Controller
public class LoginController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(LoginController.class);
	private static int MAX_REQUEST_CODE_TIME = 2 * 60 * 60 * 1000; // 2 hours
	
	@RequestMapping("/welcome")
	public ModelAndView welcome(ModelMap model, HttpServletRequest request) {
		try {
			localizeModel(model, request);
		} catch (Exception e) {
			logException(LOGGER, e);
		}
		
		model.put("availableLocales", LocalizerUtils.getAvailableLocales());
		model.put("currentLocale", LocalizerUtils.getCurrentLocale(request));
		
		return new ModelAndView("/login/login", model);
	}
	
	@RequestMapping("/logout")
	public ModelAndView logout(ModelMap model, HttpServletRequest request, Integer ws) {
		try {
			localizeModel(model, request);
			
			User user = getLoggedUserFromCookie(request);
			if (user != null) {
				user.setToken(null);
				
				userService.setUser(user, SecureUtils.getDbKey());
			}
		} catch (Exception e) {
			logException(LOGGER, e);
		}
		
		model.put("availableLocales", LocalizerUtils.getAvailableLocales());
		model.put("currentLocale", LocalizerUtils.getCurrentLocale(request));
		model.put("logout", true);
		
		if (ws != null && ws == 1) {
			model.put("sessionMessage", LocalizerUtils.getLocalizedText(request, "error_msg_session_ended"));
			
			LOGGER.info("Invalid user session, redirecting to Logout.");
		}
		
		return new ModelAndView("/login/login", model);
	}

	@RequestMapping(value = "/localize", method = { RequestMethod.POST })
	public @ResponseBody ResultDto localize(ModelMap model, HttpServletRequest request) {
		try {
			String selectedLocale = getObjectFromRequest(request, String.class);
			
			LocalizerUtils.setLocale(request, selectedLocale);
			
			Map<String, String> dictionary = LocalizerUtils.getLocalizedDictionary(request, getControllerPath());
			
			return new ResultDto(true, SecureUtils.encrypt(dictionary), null);
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, null, ExceptionUtils.getErrorMessage(e));
		}
	}
	
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public @ResponseBody ResponseDto login(ModelMap model, HttpServletRequest request) {
		ResponseDto response = null;
		
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				LoginDto login = getObjectFromRequest(request, LoginDto.class);
				
				User user = userService.getUser(login.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getPass().equals(login.getPass())) {
							String token = SecureUtils.getToken(user.getEmail());
							
							user.setToken(token);
							user.setResetCode(null);
							
							userService.setUser(user, SecureUtils.getDbKey());
							
							response = new RedirectDto(true, "/dashboard", SecureUtils.encrypt(token), LocalizerUtils.getLocalizedText(request, "msg_login_success"));
						} else {
							response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_1"));
						}
					} else {
						response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_disabled"));
					}
				} else {
					response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_1"));
				}
			} catch (Exception e) {
				logException(LOGGER, e);
				
				response = new RedirectDto(false, "/internalError", ExceptionUtils.getErrorMessage(e));
			}
		} else {
			response = new RedirectDto(false, "/accessDenied", null);
		}
		
		return response;
	}
	
	@RequestMapping("/forgot")
	public ModelAndView forgot(ModelMap model, HttpServletRequest request) {
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				localizeModel(model, request, "forgot");
			} catch (Exception e) {
				logException(LOGGER, e);
			}
			
			return new ModelAndView("/login/forgot", model);
		}
		
		return new ModelAndView("redirect:/accessDenied", model);
	}
	
	@RequestMapping(value = "/requestCode", method = { RequestMethod.POST })
	public @ResponseBody ResponseDto requestCode(ModelMap model, HttpServletRequest request) {
		ResponseDto response = null;
		
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				LoginDto login = getObjectFromRequest(request, LoginDto.class);
				
				User user = userService.getUser(login.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						int resetCode = SecureUtils.getCode();
						
						user.setResetCode(resetCode);
						
						userService.setUser(user, SecureUtils.getDbKey());
						
						EmailUtils.sendResetCodeEmail(this, request, user.getEmail(), resetCode);
						
						response = new ResultDto(true, null, LocalizerUtils.getLocalizedText(request, "msg_login_reset_code"));
					} else {
						response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_disabled"));
					}
				} else {
					response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_2"));
				}
			} catch (Exception e) {
				logException(LOGGER, e);
				
				response = new RedirectDto(false, "/internalError", ExceptionUtils.getErrorMessage(e));
			}
		} else {
			response = new RedirectDto(false, "/accessDenied", null);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/recover", method = { RequestMethod.GET })
	public ModelAndView recover(ModelMap model, HttpServletRequest request, String email) {
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				localizeModel(model, request, "recover");
			} catch (Exception e) {
				logException(LOGGER, e);
			}
			
			model.put("email", email);
			model.put("minCodeValue", SecureUtils.MIN_RANDOM_VALUE);
			model.put("maxCodeValue", SecureUtils.MAX_RANDOM_VALUE);
			
			return new ModelAndView("/login/recover", model);
		}
		
		return new ModelAndView("redirect:/accessDenied", model);
	}
	
	@RequestMapping(value = "/validateCode", method = { RequestMethod.POST })
	public @ResponseBody ResponseDto validateCode(ModelMap model, HttpServletRequest request) {
		ResponseDto response = null;
		
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				ValidateCodeDto validateCode = getObjectFromRequest(request, ValidateCodeDto.class);
				
				User user = userService.getUser(validateCode.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						if (user.getResetCode() != null && user.getResetCode().equals(validateCode.getCode())) {
							long timestamp = new Date().getTime();
							long difference = timestamp - user.getUpdated().getTime();
							
							if (difference < MAX_REQUEST_CODE_TIME) {
								request.getSession().setAttribute(user.getResetCode().toString(), user.getEmail());
								
								response = new RedirectDto(true, "/reset", SecureUtils.encrypt(user.getResetCode()),
										LocalizerUtils.getLocalizedText(request, "msg_login_valid_code"));
							} else {
								response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_expired_code"));
							}
						} else {
							response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_code"));
						}
					} else {
						response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_disabled"));
					}
				} else {
					response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_2"));
				}
			} catch (Exception e) {
				logException(LOGGER, e);
				
				response = new RedirectDto(false, "/internalError", ExceptionUtils.getErrorMessage(e));
			}
		} else {
			response = new RedirectDto(false, "/accessDenied", null);
		}
		
		return response;
	}
	
	@RequestMapping(value = "/reset", method = { RequestMethod.POST })
	public ModelAndView reset(ModelMap model, HttpServletRequest request, String encryptedData) {
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				localizeModel(model, request, "reset");
				
				Integer code = SecureUtils.decrypt(encryptedData, Integer.class);
				
				if (code != null) {
					Object attribute = request.getSession().getAttribute(code.toString());
					
					if (attribute != null) {
						String email = attribute.toString();
						
						model.put("email", email);
					} else {
						model.put("error", LocalizerUtils.getLocalizedText(request, "msg_login_invalid_code"));
					}
				} else {
					model.put("error", LocalizerUtils.getLocalizedText(request, "msg_login_invalid_code"));
				}
			} catch (Exception e) {
				logException(LOGGER, e);
			}
			
			return new ModelAndView("/login/reset", model);
		}
		
		return new ModelAndView("redirect:/accessDenied", model);
	}
	
	@RequestMapping(value = "/resetPassword", method = { RequestMethod.POST })
	public @ResponseBody ResponseDto resetPassword(ModelMap model, HttpServletRequest request) {
		ResponseDto response = null;
		
		if (IpAddressUtils.areInSameSubNet(request.getLocalAddr(), request.getRemoteAddr())) {
			try {
				LoginDto login = getObjectFromRequest(request, LoginDto.class);
				
				User user = userService.getUser(login.getEmail(), SecureUtils.getDbKey());
				
				if (user != null) {
					if (user.isActive()) {
						user.setPass(login.getPass());
						user.setResetCode(null);
						user.setToken(null);
						
						userService.setUser(user, SecureUtils.getDbKey());
						
						response = new RedirectDto(true, "/welcome", LocalizerUtils.getLocalizedText(request, "msg_login_pass_recovered"));
					} else {
						response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_disabled"));
					}
				} else {
					response = new ResultDto(false, null, LocalizerUtils.getLocalizedText(request, "msg_login_invalid_2"));
				}
			} catch (Exception e) {
				logException(LOGGER, e);
				
				response = new RedirectDto(false, "/internalError", ExceptionUtils.getErrorMessage(e));
			}
		} else {
			response = new RedirectDto(false, "/accessDenied", null);
		}
		
		return response;
	}
}
