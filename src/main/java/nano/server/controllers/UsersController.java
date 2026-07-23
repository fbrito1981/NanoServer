package nano.server.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import nano.server.db.access.DBException;
import nano.server.db.entities.Role;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;
import nano.server.dtos.DataTableRequestDto;
import nano.server.dtos.DataTableResponseDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.SendEMailDto;
import nano.server.dtos.UserDto;
import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.utils.EmailUtils;
import nano.server.utils.ExceptionUtils;
import nano.server.utils.FileUtils;
import nano.server.utils.LocalizerUtils;
import nano.server.utils.SecureUtils;
import nano.server.utils.UrlUtils;

@Controller
public class UsersController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(UsersController.class);

	@Autowired
	private IRoleService roleService;
	
	@RequestMapping("/users")
	public ModelAndView users(ModelMap model, HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				if (userHasAccess(user)) {
					localizeModel(model, request);
					
					localizeModel(model, request, "data_table");
					
					loadMenu(model, request, user);

					List<Role> createRoles = new ArrayList<>();
					List<Role> updateRoles = new ArrayList<>();
					for (Role role : roleService.allRoles()) {
						if (role.getLevel() > user.getRole().getLevel() ) {
							createRoles.add(role);
						}
						if (role.getLevel() >= user.getRole().getLevel() ) {
							updateRoles.add(role);
						}
					}

					Collections.sort(createRoles, new Comparator<Role>() {
						@Override
						public int compare(Role o1, Role o2) {
							return o2.compareTo(o1);
						}
					});
					
					model.put("createRoles", createRoles);
					model.put("updateRoles", updateRoles);
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
		
		return new ModelAndView("/users/users", model);
	}

	@RequestMapping(value = "/users/list", method = { RequestMethod.POST })
	public @ResponseBody DataTableResponseDto<UserDto> list(HttpServletRequest request) {
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
			
			int usersCounter = userService.countUsers(search);
			
			List<User> users = userService.allUsers(search, order,
					dataTableRequest.getLength(), dataTableRequest.getStart());
			
			List<UserDto> userDtos = new ArrayList<>();
			for (User user : users) {
				String activeLabel = user.isActive() ? "db_status_active" : "db_status_inactive";
				String picture = null;
				if (user.getPicture() != null) {
					picture = UrlUtils.getFilePath(FileType.IMAGES, EntityType.USERS, user.getPicture());
				}
				
				UserDto userDto = new UserDto();
				userDto.setActive(user.isActive());
				userDto.setActiveLabel(LocalizerUtils.getLocalizedText(request, activeLabel));
				userDto.setEmail(user.getEmail());
				userDto.setName(user.getName());
				userDto.setRoleName(user.getRole().getName());
				userDto.setPicture(picture);
				userDto.setCreated(user.getCreated());
				userDto.setUpdated(user.getUpdated());
				
				userDtos.add(userDto);
			}
			
			return new DataTableResponseDto<UserDto>(dataTableRequest.getDraw(), usersCounter, userDtos);
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new DataTableResponseDto<UserDto>(request, e);
		}
	}

	@RequestMapping(value = "/users/create", method = { RequestMethod.POST })
	public @ResponseBody ResultDto create(HttpServletRequest request) {
		try {
			UserDto userDto = getObjectFromRequest(request, UserDto.class);
			
			MultipartFile pictureFile = getFileFromRequest(request, "picture");
			
			User existingUser = userService.getUser(userDto.getEmail(), SecureUtils.getDbKey());

			if (existingUser != null) {
				return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "db_error_msg_duplicate_key"));
			} else {
				Role role = roleService.getRole(userDto.getRoleName());
				
				String picture = FileUtils.saveImage(EntityType.USERS, userDto.getName(), pictureFile);
				
				String randomPass = SecureUtils.getRanbomPass();
				
				User user = new User(userDto.getEmail(), randomPass, userDto.getName(), role, picture);
				
				userService.setUser(user, SecureUtils.getDbKey());

				EmailUtils.sendNewAccountEmail(this, request, user.getEmail(), randomPass);
				
				return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_create"));
			}
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/users/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request) {
		try {
			UserDto userDto = getObjectFromRequest(request, UserDto.class);
			
			MultipartFile pictureFile = getFileFromRequest(request, "picture");
			
			Role role = roleService.getRole(userDto.getRoleName());
			
			User user = userService.getUser(userDto.getEmail(), SecureUtils.getDbKey());
			user.setActive(userDto.getActive() != null ? userDto.getActive() : false);
			user.setName(userDto.getName());
			user.setRole(role);

			String picture = user.getPicture();
			if (userDto.getPictureRemoved() != null && userDto.getPictureRemoved()) {
				FileUtils.deleteFile(FileType.IMAGES, EntityType.USERS, user.getPicture());
				picture = null;
			}
			if (pictureFile != null) {
				picture = FileUtils.saveImage(EntityType.USERS, userDto.getName(), pictureFile);
				
				if (user.getPicture() != null) {
					FileUtils.deleteFile(FileType.IMAGES, EntityType.USERS, user.getPicture());
				}
			}
			
			user.setPicture(picture);
			
			userService.setUser(user, SecureUtils.getDbKey());
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_update"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/users/remove", method = { RequestMethod.POST })
	public @ResponseBody ResultDto remove(HttpServletRequest request) {
		try {
			UserDto userDto = getObjectFromRequest(request, UserDto.class);
			
			try {
				User user = userService.getUser(userDto.getEmail(), SecureUtils.getDbKey());
				
				userService.delUser(user.getEmail());
				
				if (user.getPicture() != null) {
					FileUtils.deleteFile(FileType.IMAGES, EntityType.USERS, user.getPicture());
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

	@RequestMapping(value = "/users/sendEMail", method = { RequestMethod.POST })
	public @ResponseBody ResultDto sendEMail(HttpServletRequest request) {
		try {
			SendEMailDto sendEMailDto = getObjectFromRequest(request, SendEMailDto.class);
			
			User user = userService.getUser(sendEMailDto.getEmail(), SecureUtils.getDbKey());
			
			String randomPass = SecureUtils.getRanbomPass();
			
			user.setPass(randomPass);
			
			userService.setUser(user, SecureUtils.getDbKey());
			
			EmailUtils.sendNewAccountEmail(this, request, user.getEmail(), randomPass);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_send_email"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}
}
