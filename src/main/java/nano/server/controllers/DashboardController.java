package nano.server.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import nano.server.db.entities.User;
import nano.server.utils.LocalizerUtils;

@Controller
public class DashboardController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(DashboardController.class);

	@RequestMapping("/dashboard")
	public ModelAndView dashboard(ModelMap model, HttpServletRequest request, String irs) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				localizeModel(model, request);
				
				loadMenu(model, request, user);
				
				if (irs != null && irs.length() > 0) {
					model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_invalid_role"));
					
					String infoMessage = String.format(Locale.getDefault(),
							"User with role '%s' and level '%d' can't acces to section '%s'.",
							user.getRole().getName(),
							user.getRole().getLevel(),
							irs);
					
					LOGGER.info(infoMessage);
				}
			} else {
				return new ModelAndView("redirect:/logout?ws=1", model);
			}
		} catch (Exception e) {
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
			
			logException(LOGGER, e);
		}
		
		return new ModelAndView("/dashboard/dashboard", model);
	}
}
