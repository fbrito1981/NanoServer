package nano.server.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nano.server.db.entities.SecurityLog;
import nano.server.db.services.def.ISecurityLogService;

public class WSSecurityUtils {
	private static Logger LOGGER = LogManager.getLogger(WSSecurityUtils.class);
	private static String HEADER_KEY = "X-WSSE";
	private static String CUSTOMER_TOKEN_KEY = "UsernameToken Username";
	private static String NONCE_KEY = "Nonce";
	private static String CREATED_KEY = "Created";
	private static String PASSWORD_DIGEST_KEY = "PasswordDigest";
	private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static long NONCE_TIME_TO_LIVE = 1 * 60 * 60 * 1000;
	
	public static boolean isHeaderValid(ISecurityLogService securityLogService,
			HttpServletRequest request, String customerToken, String secretToken) throws Exception {
		String wsseHeaders = request.getHeader(HEADER_KEY);
		if (wsseHeaders != null) {
			String[] tokens = wsseHeaders.split("\", ");
			Map<String, String> keyValueMap = new HashMap<String, String>();

			for (int i = 0; i< tokens.length; i++) {
				String token = tokens[i];
				String[] keyValue = token.split("=\"");
				keyValueMap.put(keyValue[0], keyValue[1].replaceAll("\"", ""));
			}

			String headerCustomerToken = keyValueMap.get(CUSTOMER_TOKEN_KEY);
			if (!customerToken.equals(headerCustomerToken)) {
				LOGGER.error("Customer token did not match - token: {} header: {}", customerToken, headerCustomerToken);
				return false;
			}

			String nonceEncoded = keyValueMap.get(NONCE_KEY);
			if (securityLogService != null) {
				SecurityLog securityLog = securityLogService.getSecurityLog(nonceEncoded);
				
				if (securityLog != null) {
					return false;
				}
			}

			byte[] nonce = Base64.getDecoder().decode(nonceEncoded);
			String created = keyValueMap.get(CREATED_KEY);
			try {
				DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
				long createdTimestamp = dateFormat.parse(created).getTime();
				long currentTimestamp =  new Date().getTime();
				if (currentTimestamp - createdTimestamp > NONCE_TIME_TO_LIVE) {
					LOGGER.error("Created too old {}.", created);
					
					return false;
				}
			} catch (ParseException e) {
				LOGGER.warn("Ignored the error in parsing created at {}.", created);
			}
			
			LOGGER.info("nonceEncoded [{}] created [{}] password [{}].", nonceEncoded, created, secretToken);
			
			String passwordDigest = generatePasswordDigest(nonce,
					created.getBytes(StandardCharsets.UTF_8), secretToken.getBytes(StandardCharsets.UTF_8));
			String passwordDigestInHeader = keyValueMap.get(PASSWORD_DIGEST_KEY);
			if (passwordDigest.equals(passwordDigestInHeader)) {
				if (securityLogService != null) {
					SecurityLog securityLog = new SecurityLog(nonceEncoded);
					
					securityLogService.setSecurityLog(securityLog);
				}
				
				return true;
			}
			
			LOGGER.error("Password Digest did not match - calculated [{}] header [{}].", passwordDigest, passwordDigestInHeader);
		}
		
		return false;
	}
	
	public static void setHeaderAuthorization(HttpRequestBase httpRequestBase, String customerToken, String secretToken) throws Exception {
		Integer randomNumber = SecureUtils.getCode();
		byte[] randomBytes = randomNumber.toString().getBytes(StandardCharsets.UTF_8);
		String nonceEncoded = Base64.getEncoder().encodeToString(randomBytes);
		String created = new SimpleDateFormat(DATE_FORMAT).format(new Date());
		String passwordDigest = generatePasswordDigest(randomBytes, created.getBytes(StandardCharsets.UTF_8),
				secretToken.getBytes(StandardCharsets.UTF_8));
		
		LOGGER.info("nonceEncoded [{}] created [{}] password [{}].", nonceEncoded, created, secretToken);
		
		String header = String.format(Locale.getDefault(), "%s=\"%s\", %s=\"%s\", %s=\"%s\", %s=\"%s\"",
				CUSTOMER_TOKEN_KEY, customerToken, PASSWORD_DIGEST_KEY, passwordDigest, NONCE_KEY,
				nonceEncoded, CREATED_KEY, created);
		
		httpRequestBase.addHeader("Authorization", "WSSE profile=\"UsernameToken\"");
		httpRequestBase.addHeader(HEADER_KEY, header);
	}
	
	public static synchronized String generatePasswordDigest(byte[] nonce, byte[] created, byte[] password) throws RuntimeException {
		try {
			MessageDigest messageDigester = MessageDigest.getInstance("SHA-1");
			messageDigester.reset();
			messageDigester.update(nonce);
			messageDigester.update(created);
			messageDigester.update(password);
			
			return Base64.getEncoder().encodeToString(messageDigester.digest());
		} catch (java.security.NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} 
	}
}
