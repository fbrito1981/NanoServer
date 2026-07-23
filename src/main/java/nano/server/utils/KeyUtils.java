package nano.server.utils;

public class KeyUtils {
	public static final String TEXT_SEPARATOR = "//";
	
	public static String group(String[] items) {
		return group(items, TEXT_SEPARATOR);
	}

	public static String group(String[] items, String separator) {
		if (items != null && items.length > 0) {
			StringBuilder builder = new StringBuilder();
			for (String item : items) {
				builder.append(item);
				builder.append(separator);
			}
			return builder.toString();
		}
		return null;
	}
	
	public static String pick(String text, int position) {
		return pick(text, position, TEXT_SEPARATOR);
	}

	public static String pick(String text, int position, String separator) {
		if (text != null && text.contains(separator)) {
			String[] parts = text.split(separator);
			if (parts.length > position) {
				return parts[position];
			}
		}
		return null;
	}
}
