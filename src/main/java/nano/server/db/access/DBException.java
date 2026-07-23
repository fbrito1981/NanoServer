package nano.server.db.access;

import java.util.Locale;

public class DBException extends Exception {
	private static final long serialVersionUID = 5506486555114574878L;

	public DBException(String customMessage) {
		super(customMessage);
	}
	
	public DBException(String customMessage, Throwable originalException) {
		super(customMessage, originalException);
	}

	public DBException(Class<?> entityClass, Throwable originalException) {
		super(getClassErrorMessage(entityClass, originalException.getLocalizedMessage()), originalException);
	}
	
	public DBException(DBScript script, Throwable originalException) {
		super(getScriptErrorMessage(script, originalException.getLocalizedMessage()), originalException);
	}
	
	private static String getClassErrorMessage(Class<?> entityClass, String localizedErrorMessage) {
		String errorMessage = String.format(Locale.getDefault(),
				"Exception trying to get new entity of type %s: %s",
				entityClass.getSimpleName(), localizedErrorMessage);

		return errorMessage;
	}
	
	private static String getScriptErrorMessage(DBScript script, String localizedErrorMessage) {
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append("Exception with CALL ");
		errorMessage.append(script.getMethod());
		errorMessage.append("(");
		if (script.getParams() != null && script.getParams().size() > 0) {
			for (int i = 0; i < script.getParams().size(); i++) {
				if (i > 0) {
					errorMessage.append(", ");
				}
				DBParam param = script.getParams().get(i);
				if (param.isNull()) {
					errorMessage.append("null");
				} else {
					if (param.isQuoted()) {
						if (param.isAList()) {
							errorMessage.append("'").append(param.getListValues()).append("'");
						} else {
							errorMessage.append("'").append(param.getValue()).append("'");
						}
					} else {
						errorMessage.append(param.getValue());
					}
				}
			}
		}
		errorMessage.append("); error: ");
		errorMessage.append(localizedErrorMessage);
		
		return errorMessage.toString();
	}
}
