package nano.server.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.logging.log4j.Logger;

public class ExceptionUtils {
	public static String getErrorMessage(Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append(" - Localized error message: ").append(e.getLocalizedMessage()).append(System.lineSeparator());
		builder.append(" - Error message: ").append(e.getMessage()).append(System.lineSeparator());
		builder.append(" - Stack trace: ").append(getStackTraceMessage(e));
		
		return builder.toString();
	}
	
	public static String getStackTraceMessage(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		
		return stringWriter.toString();
	}
	
	public static boolean hasExceptionType(Exception e, String typeName) {
		if (e.getMessage().equals(typeName) ||
				getStackTraceMessage(e).contains(typeName)) {
			return true;
		}
		return false;
	}
	
	public static void logException(Logger logger, Throwable t) {
		String errorMessage = String.format(Locale.getDefault(),
				"Exception in '%s': %s.",
				t.getStackTrace()[0].getClassName(),
				t.getMessage());
		
		logger.error(errorMessage, t);
	}
}
