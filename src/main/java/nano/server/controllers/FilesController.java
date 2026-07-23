package nano.server.controllers;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.utils.FileUtils;

@Controller
public class FilesController extends BaseController {
	private static Logger LOGGER = LogManager.getLogger(FilesController.class);

	@RequestMapping("/files/{fileType}/{entityType}/{filename:.+}")
	public void files(@PathVariable(value = "fileType") String fileType,
			@PathVariable(value = "entityType") String entityType,
			@PathVariable(value = "filename") String filename, 
			HttpServletResponse response) {
		try {
			FileType eFileType = FileType.fromValue(fileType);
			EntityType eEntityType = EntityType.fromValue(entityType);
			
			if (eFileType != null && eEntityType != null && filename != null && !filename.isEmpty()) {
				byte[] fileContent = FileUtils.loadFile(eFileType, eEntityType, filename);
				
				if (fileContent != null) {
					String originalFilename = filename.substring(filename.indexOf("_") + 1);
					
					response.setContentType(eFileType.getContentType());
					response.setContentLength(fileContent.length);
					response.setHeader("Content-Disposition", "attachment; filename=" + originalFilename);
					
					OutputStream outputStream = response.getOutputStream();
					outputStream.write(fileContent);
					outputStream.flush();
					outputStream.close();
				}
			}
		} catch (Exception e) {
			logException(LOGGER, e);
		}
	}
}
