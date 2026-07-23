package nano.server.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import nano.server.db.entities.Role;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;
import nano.server.dtos.DataTableRequestDto;
import nano.server.dtos.DataTableResponseDto;
import nano.server.dtos.MoveDto;
import nano.server.dtos.ResultDto;
import nano.server.utils.ExceptionUtils;
import nano.server.utils.LocalizerUtils;

@Controller
public class RolesController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(RolesController.class);

	@Autowired
	private IRoleService roleService;
	
	@RequestMapping("/roles")
	public ModelAndView roles(ModelMap model, HttpServletRequest request) {
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
		
		return new ModelAndView("/roles/roles", model);
	}

	@RequestMapping(value = "/roles/list", method = { RequestMethod.POST })
	public @ResponseBody DataTableResponseDto<Role> list(HttpServletRequest request) {
		try {
			DataTableRequestDto dataTableRequest = new DataTableRequestDto(request);
			String search = null;
			String order = null;
			
			if (dataTableRequest.getSearch().getValue() != null && dataTableRequest.getSearch().getValue().length() > 0) {
				search = dataTableRequest.getSearch().getValue();
			}
			
			if (dataTableRequest.getOrder() != null && dataTableRequest.getOrder().size() > 0) {
				int column = dataTableRequest.getOrder().get(0).getColumn();
				order = String.format(Locale.getDefault(), "%s %s",
						dataTableRequest.getColumns().get(column).getData(),
						dataTableRequest.getOrder().get(0).getDir());
			}
			
			int rolesCounter = roleService.countRoles(search);
			
			List<Role> roles = roleService.allRoles(search, order,
					dataTableRequest.getLength(), dataTableRequest.getStart());
			
			return new DataTableResponseDto<Role>(dataTableRequest.getDraw(), rolesCounter, roles);
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new DataTableResponseDto<Role>(request, e);
		}
	}

	@RequestMapping(value = "/roles/create", method = { RequestMethod.POST })
	public @ResponseBody ResultDto create(HttpServletRequest request) {
		try {
			Role role = getObjectFromRequest(request, Role.class);
			
			boolean alreadyExists = false;
			int maxLevel = 0;
			List<Role> roles = roleService.allRoles();
			for (Role item : roles) {
				if (item.getName().equals(role.getName())) {
					alreadyExists = true;
					break;
				}
				if (item.getLevel() > maxLevel) {
					maxLevel = item.getLevel();
				}
			}

			if (alreadyExists) {
				return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "db_error_msg_duplicate_key"));
			} else {
				role.setLevel(maxLevel + 1);
				
				roleService.setRole(role);
				
				return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_create"));
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/roles/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request) {
		try {
			Role role = getObjectFromRequest(request, Role.class);
			
			List<Role> roles = roleService.allRoles();
			for (Role item : roles) {
				if (item.getLevel() == role.getLevel()) {
					roleService.delRole(item.getName());
					break;
				}
			}
			
			roleService.setRole(role);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_update"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/roles/remove", method = { RequestMethod.POST })
	public @ResponseBody ResultDto remove(HttpServletRequest request) {
		try {
			Role role = getObjectFromRequest(request, Role.class);
			
			try {
				roleService.delRole(role.getName());
				
				List<Role> roles = roleService.allRoles();
				
				Collections.sort(roles);
				
				for (int i = 0; i < roles.size(); i++) {
					Role item = roles.get(i);
					item.setLevel(i);
					
					roleService.setRole(item);
				}
				
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

	@RequestMapping(value = "/roles/move", method = { RequestMethod.POST })
	public @ResponseBody ResultDto move(HttpServletRequest request) {
		try {
			MoveDto moveDto = getObjectFromRequest(request, MoveDto.class);
			
			Role role = roleService.getRole(moveDto.getId());
			
			int currentLevel = role.getLevel();
			int otherLevel = currentLevel + (moveDto.isUp() ? -1 : 1);
			
			Role otherRole = null;
			List<Role> roles = roleService.allRoles();
			for (Role item : roles) {
				if (item.getLevel() == otherLevel) {
					otherRole = item;
					break;
				}
			}
			
			otherRole.setLevel(9999);
			
			roleService.setRole(otherRole);
			
			role.setLevel(otherLevel);
			
			roleService.setRole(role);
			
			otherRole.setLevel(currentLevel);
			
			roleService.setRole(otherRole);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_move"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}
}
