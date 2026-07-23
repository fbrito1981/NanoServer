package nano.server.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import nano.server.utils.LocalizerUtils;

@Controller
public class ErrorController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(ErrorController.class);

	@RequestMapping("/accessDenied")
	public ModelAndView accessDenied(ModelMap model, HttpServletRequest request) {
		try {
			model.put("errorCode", 403);
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_access_denied"));
		} catch (Exception e) {
			logException(LOGGER, e);
		}
		
		return new ModelAndView("/error/error", model);
	}

	@RequestMapping("/notFound")
	public ModelAndView notFound(ModelMap model, HttpServletRequest request) {
		try {
			model.put("errorCode", 404);
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_not_found"));
		} catch (Exception e) {
			logException(LOGGER, e);
		}
		
		return new ModelAndView("/error/error", model);
	}

	@RequestMapping("/notAllowed")
	public ModelAndView notAllowed(ModelMap model, HttpServletRequest request) {
		try {
			model.put("errorCode", 405);
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_not_allowed"));
		} catch (Exception e) {
			logException(LOGGER, e);
		}
		
		return new ModelAndView("/error/error", model);
	}

	@RequestMapping("/internalError")
	public ModelAndView internalError(ModelMap model, HttpServletRequest request, String error) {
		try {
			model.put("errorCode", 500);
			model.put("errorMessage", LocalizerUtils.getLocalizedText(request, "error_msg_internal"));
			model.put("error", error);
		} catch (Exception e) {
			logException(LOGGER, e);
		}

		return new ModelAndView("/error/error", model);
	}
}
