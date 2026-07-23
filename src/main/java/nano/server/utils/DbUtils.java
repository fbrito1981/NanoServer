package nano.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DbUtils {
	private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static long getUnixTimestamp(Date date) {
		return date.getTime() / 1000;
	}
	
	public static Date getDate(long unixTime) {
		return new Date(unixTime * 1000);
	}
	
	public static String getDateFormated(Date date) throws Exception {
		return new SimpleDateFormat(DB_DATE_FORMAT).format(date);
	}
}
