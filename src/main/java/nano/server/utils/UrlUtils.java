package nano.server.utils;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import nano.server.enums.EntityType;
import nano.server.enums.FileType;
import nano.server.enums.ServerProperties;

public class UrlUtils {
	public static String getSectionPath(HttpServletRequest request, String section) {
		return String.format(
				Locale.getDefault(),
				"%s/%s",
				request.getContextPath(),
				section);
	}

	public static String getFilePath(FileType fileType, EntityType entityType, String filename) {
		return String.format(
				Locale.getDefault(),
				"%s/files/%s/%s/%s",
				ServerProperties.ADMIN_HOST.getValue(),
				fileType.getValue(),
				entityType.getValue(),
				filename);
	}
}
