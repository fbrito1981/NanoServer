package nano.server.controllers;

import java.util.ArrayList;
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

import nano.server.db.entities.Role;
import nano.server.db.entities.Section;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;
import nano.server.dtos.DataTableRequestDto;
import nano.server.dtos.DataTableResponseDto;
import nano.server.dtos.MoveDto;
import nano.server.dtos.ResultDto;
import nano.server.dtos.SectionDto;
import nano.server.utils.LocalizerUtils;

@Controller
public class SectionsController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(SectionsController.class);

	@Autowired
	private IRoleService roleService;
	
	@RequestMapping("/sections")
	public ModelAndView sections(ModelMap model, HttpServletRequest request) {
		try {
			User user = getLoggedUserFromCookie(request);
			
			if (user != null) {
				if (userHasAccess(user)) {
					localizeModel(model, request);
					
					localizeModel(model, request, "data_table");
					
					loadMenu(model, request, user);
					
					model.put("roles", roleService.allRoles());
					model.put("info_msg_general", LocalizerUtils.getLocalizedText(request, "info_msg_no_creation"));
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
		
		return new ModelAndView("/sections/sections", model);
	}

	@RequestMapping(value = "/sections/list", method = { RequestMethod.POST })
	public @ResponseBody DataTableResponseDto<SectionDto> list(HttpServletRequest request) {
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
			
			int sectionsCounter = sectionService.countSections(search);
			
			List<Section> sections = sectionService.allSections(search, order,
					dataTableRequest.getLength(), dataTableRequest.getStart());
			
			List<SectionDto> sectionDtos = new ArrayList<>();
			for (Section section : sections) {
				SectionDto sectionDto = new SectionDto();
				sectionDto.setName(section.getName());
				sectionDto.setLabel(LocalizerUtils.getLocalizedText(request, section.getMenuLabel()));
				sectionDto.setMinRoleName(section.getMinRole().getName());
				sectionDto.setMenuOrder(section.getMenuOrder());
				
				sectionDtos.add(sectionDto);
			}
			
			return new DataTableResponseDto<SectionDto>(dataTableRequest.getDraw(), sectionsCounter, sectionDtos);
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new DataTableResponseDto<SectionDto>(request, e);
		}
	}

	@RequestMapping(value = "/sections/update", method = { RequestMethod.POST })
	public @ResponseBody ResultDto update(HttpServletRequest request) {
		try {
			SectionDto sectionDto = getObjectFromRequest(request, SectionDto.class);
			
			Section section = sectionService.getSection(sectionDto.getName());
			
			Role minRole = roleService.getRole(sectionDto.getMinRoleName());
			
			section.setMinRole(minRole);
			
			sectionService.setSection(section);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_update"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}

	@RequestMapping(value = "/sections/move", method = { RequestMethod.POST })
	public @ResponseBody ResultDto move(HttpServletRequest request) {
		try {
			MoveDto moveDto = getObjectFromRequest(request, MoveDto.class);
			
			Section section = sectionService.getSection(moveDto.getId());
			
			int currentMenuOrder = section.getMenuOrder();
			int otherMenuOrder = currentMenuOrder + (moveDto.isUp() ? -1 : 1);
			
			Section otherSection = null;
			List<Section> sections = sectionService.allSections();
			for (Section item : sections) {
				if (item.getMenuOrder() == otherMenuOrder) {
					otherSection = item;
					break;
				}
			}
			
			otherSection.setMenuOrder(9999);
			
			sectionService.setSection(otherSection);
			
			section.setMenuOrder(otherMenuOrder);
			
			sectionService.setSection(section);
			
			otherSection.setMenuOrder(currentMenuOrder);
			
			sectionService.setSection(otherSection);
			
			return new ResultDto(true, "", LocalizerUtils.getLocalizedText(request, "db_success_msg_move"));
		} catch (Exception e) {
			logException(LOGGER, e);
			
			return new ResultDto(false, "", LocalizerUtils.getLocalizedText(request, "error_msg_general"));
		}
	}
}
