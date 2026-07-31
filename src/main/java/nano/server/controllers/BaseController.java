package nano.server.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import nano.server.db.entities.Section;
import nano.server.db.entities.User;
import nano.server.db.services.def.ISectionService;
import nano.server.db.services.def.IUserService;
import nano.server.dtos.MenuItemDto;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.SecureUtils;
import nano.server.utils.UrlUtils;

public class BaseController {
	private static final String COOKIE_HEADER = "cookie";
	private static final String LOGIN_HEADER = "LoginToken";
	private static final String USER_SESSION_COOKIE_NAME = "nano.server.token";

	@Autowired
	protected IUserService userService;
	
	@Autowired
	protected ISectionService sectionService;
	
	protected String getControllerPath() {
		return this.getClass().getSimpleName().replace("Controller", "").toLowerCase();
	}
	
	protected <T> T getObjectFromRequest(HttpServletRequest request, Class<T> tClass) throws Exception {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		
		String encryptedData = multipartRequest.getParameter("encryptedData");
		
		return SecureUtils.decrypt(encryptedData, tClass);
	}
	
	protected MultipartFile getFileFromRequest(HttpServletRequest request,String name) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		
		return multipartRequest.getFile(name);
	}
	
	protected void localizeModel(ModelMap model, HttpServletRequest request) throws Exception {
		localizeModel(model, request, getControllerPath());
	}
	
	protected void localizeModel(ModelMap model, HttpServletRequest request, String controllerPath) throws Exception {
		Map<String, String> dictionary = LocalizerUtils.getLocalizedDictionary(request, controllerPath);
		
		if (!dictionary.isEmpty()) {
			for (String key : dictionary.keySet()) {
				model.put(key, dictionary.get(key));
			}
		}
	}
	
	protected void loadMenu(ModelMap model, HttpServletRequest request, User user) throws Exception {
		List<MenuItemDto> menuItems = getMenuItems(request, user);
		
		model.put("menuItems", menuItems);
	}
	
	protected User getLoggedUserFromCookie(HttpServletRequest request) throws Exception {
		User user = null;
		String cookieValues = request.getHeader(COOKIE_HEADER);
		
		if (cookieValues != null && cookieValues.length() > 0 && cookieValues.contains(USER_SESSION_COOKIE_NAME)) {
			String[] cookies = cookieValues.split("; ");
			String token = "";
			for (String cookie : cookies) {
				if (cookie.startsWith(USER_SESSION_COOKIE_NAME)) {
					token = cookie.split("=")[1];
					break;
				}
			}
			
			user = getLoggedUserFromToken(token);
		}
		
		return user;
	}
	
	protected User getLoggedUserFromHeader(HttpServletRequest request) throws Exception {
		User user = null;
		String token = request.getHeader(LOGIN_HEADER);
		
		if (token != null && !token.isEmpty()) {
			user = getLoggedUserFromToken(token);
		}
		
		return user;
	}
	
	protected User getLoggedUserFromToken(String token) throws Exception {
		User user = null;
		
		if (token != null && !token.isEmpty()) {
			String email = SecureUtils.getEmail(token);
			
			if (email != null && !email.isEmpty()) {
				User tokenUser = userService.getUser(email, SecureUtils.getDbKey());

				// tokenUser can be null (stale token for a deleted user) and getToken() can be
				// null (cleared by a password reset/logout) — either means "not logged in",
				// not an NPE that should fall through to a half-rendered page.
				if (tokenUser != null && token.equals(tokenUser.getToken())) {
					user = tokenUser;
				}
			}
		}
		
		return user;
	}
	
	protected void addTokenToResponse(String token, HttpServletResponse response) {
		response.addHeader(LOGIN_HEADER, token);
	}
	
	protected List<MenuItemDto> getMenuItems(HttpServletRequest request, User user) throws Exception {
		List<MenuItemDto> menuItems = new ArrayList<>();
		
		int limit = sectionService.countSections(null);
		
		List<Section> sections = sectionService.allSections(null, null, limit, 0);
		if (sections != null && sections.size() > 0) {
			Collections.sort(sections);
			
			for (Section section : sections) {
				if (section.hasAccess(user.getRole())) {
					MenuItemDto menuItem = new MenuItemDto();
					menuItem.setTitle(LocalizerUtils.getLocalizedText(request, section.getMenuLabel()));
					menuItem.setSection(UrlUtils.getSectionPath(request, section.getName()));
					
					if (getControllerPath().equals(section.getName())) {
						menuItem.setActive(true);
					} else {
						menuItem.setActive(false);
					}
					
					menuItems.add(menuItem);
				}
			}
		}
		
		return menuItems;
	}
	
	protected boolean userHasAccess(User user) throws Exception {
		Section section = sectionService.getSection(getControllerPath());
		
		if (section != null) {
			return section.hasAccess(user.getRole());
		}
		
		return false;
	}
	
	protected void logException(Logger logger, Throwable t) {
		String errorMessage = String.format(Locale.getDefault(),
				"Exception in '%s': %s.",
				getClass().getSimpleName(),
				t.getMessage());
		
		logger.error(errorMessage, t);
	}
}
