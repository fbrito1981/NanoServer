package nano.server.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecureUtils {
	public static int MIN_RANDOM_VALUE = 10000000;
	public static int MAX_RANDOM_VALUE = 99999999;

	private static Logger LOG = LogManager.getLogger(SecureUtils.class);
	private static String[] KEYS = { "ZrYd+p^kg4jpeKSb", "-hZJSdUtUJ2Ys_fK", "MT?=Lt&p!P!bBG5^", "$sXjYC6vvU5NEF-@", "mz5FUuA8%hqYPj6*", "WHsHDQsthCTrw!9t", "WLma58Jbq?cp5gXd", "Vb66E+t2DDsE&TnB", "cb$54xtc^WLA$zg" };
	private static String LETTERS = "abcdefghijklmnñopqrstuvwxyz";
	private static String NUMBERS = "0123456789";
	private static String SYMBOLS = "!$%&/=?@#";
	private static int RANDOM_PASS_SIZE = 12;

	private static int[] reverse(int[] array) {
		int[] reversed = new int[array.length];
		int r = array.length;
		for (int i = 0; i < array.length; i++) {
			r--;
			reversed[i] = array[r];
		}
		return reversed;
	}
	
	private static int pickKeyIndex() {
		Calendar calendar = Calendar.getInstance();
		String date = String.valueOf(calendar.get(Calendar.YEAR));
		date += String.valueOf(calendar.get(Calendar.MONTH) + 1);
		date += String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		date += String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		date += String.valueOf(calendar.get(Calendar.MINUTE));
		
		while (date.length() > 1) {
			int total = 0;
			for (int i = 0; i < date.length(); i++) {
				total += Integer.valueOf("" + date.charAt(i));
			}
			date = "" + total;
		}
		
		int index = 9 - Integer.valueOf(date);
		
		return index;
	}
	
	private static String encrypt(String value) {
		int keyIndex = pickKeyIndex();
		String key = KEYS[keyIndex];
		int k = 0;
		int[] encrypted = new int[value.length()];
		for (int i = 0; i < value.length(); i++) {
			if (k == key.length()) {
				k = 0;
			}
			int valueChar = value.charAt(i);
			int keyChar = key.charAt(k);
			encrypted[i] = valueChar ^ keyChar;
			k++;
		}
		int[] reversed = reverse(encrypted);
		String result = "";
		for (int i = 0; i < reversed.length; i++) {
			if (i > 0) {
				result += key.charAt(1);
			}
			result += "" + reversed[i];
		}
		return keyIndex + result;
	}
	
	private static String decrypt(String value) throws Exception {
		String decrypted = "";
		if (value.length() > 0) {
			int k = 0;
			int keyIndex = Integer.parseInt(value.substring(0, 1));
			String key = KEYS[keyIndex];
			value = value.substring(1);
			String[] splited = value.split("" + key.charAt(1));
			int[] encrypted = new int[splited.length];
			for (int i = 0; i < splited.length; i++) {
				encrypted[i] = Integer.valueOf(splited[i]);
			}
			int[] reversed = reverse(encrypted);
			for (int i = 0; i < reversed.length; i++) {
				if (k == key.length()) {
					k = 0;
				}
				int encryptedChar = reversed[i];
				int keyChar = key.charAt(k);
				decrypted += (char) (encryptedChar ^ keyChar);
				k++;
			}
		}
		return decrypted;
	}
	
	public static <T> String encrypt(T object) throws Exception {
		String value = MapperUtils.getString(object);
		
		return encrypt(value);
	}
	
	public static <T> T decrypt(String value, Class<T> tClass) throws Exception {
		String decrypted = decrypt(value);

		T object = null;
		
		try {
			object = MapperUtils.getObject(decrypted, tClass);
		} catch (Exception e) {
			String errorMessage = String.format(Locale.getDefault(),
					"Error trying to decrypt: %s", value);
			
			LOG.error(errorMessage, e);			
		}
		
		return object;
	}
	
	public static String getDbKey() {
		return KEYS[4];
	}
	
	public static String getToken(String email) throws Exception {
		String value = String.format(Locale.getDefault(), "%s:%d", email, new Date().getTime());
		return encrypt(value);
	}
	
	public static String getEmail(String token) throws Exception {
		String value = decrypt(token);
		return value.split(":")[0];
	}
	
	public static int getCode() {
		return (int) (Math.random() * ((MAX_RANDOM_VALUE - MIN_RANDOM_VALUE) + 1)) + MIN_RANDOM_VALUE;
	}
	
	public static String getRanbomPass() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < RANDOM_PASS_SIZE; i++) {
			char c;
			double kindOfChar = Math.random() * 30.0;
			if (kindOfChar >= 20.0) {
				boolean toUpperCase = Math.random() >= 0.5;
				int index = (int) Math.round(Math.random() * (LETTERS.length() - 1));
				if (toUpperCase) {
					c = LETTERS.toUpperCase().charAt(index);
				} else {
					c = LETTERS.charAt(index);
				}
			} else if (kindOfChar >= 10.0) {
				int index = (int) Math.round(Math.random() * (NUMBERS.length() - 1));
				c = NUMBERS.charAt(index);
			} else {
				int index = (int) Math.round(Math.random() * (SYMBOLS.length() - 1));
				c = SYMBOLS.charAt(index);
			}
			builder.append(c);
		}
		
		return builder.toString();
	}
}
