package nano.server.utils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nano.server.controllers.BaseController;
import nano.server.enums.ServerProperties;

public class EmailUtils {
	private static Logger LOGGER = LogManager.getLogger(EmailUtils.class);
	private static String SMTP_TIMEOUT = "30000";
	private static String RESET_CODE_EMAIL_TEMPLATE = "/templates/reset-code-email.html";
	private static String NEW_ACCOUNT_EMAIL_TEMPLATE = "/templates/new-account-email.html";
	
	public static void sendResetCodeEmail(BaseController handler,
			HttpServletRequest request, String to, int code) throws Exception {
		String path = handler.getClass().getResource(RESET_CODE_EMAIL_TEMPLATE).getFile();
		String content = FileUtils.getContent(path, StandardCharsets.UTF_8);
		content = content.replace("page_title", LocalizerUtils.getLocalizedText(request, "page_title"))
				.replace("host", ServerProperties.ADMIN_HOST.getValue())
				.replace("user_email", to)
				.replace("reset_code_value", String.valueOf(code));
		Map<String, String> dictionary = LocalizerUtils.getLocalizedDictionary(request, "email_reset_code");
		String subject = "";
		for (String key : dictionary.keySet()) {
			if (key.contains("subject")) {
				subject = dictionary.get(key);
			} else {
				content = content.replace(key, dictionary.get(key));
			}
		}
		sendEmail(to, subject, content);
	}
	
	public static void sendNewAccountEmail(BaseController handler,
			HttpServletRequest request, String to, String randomPass) throws Exception {
		String path = handler.getClass().getResource(NEW_ACCOUNT_EMAIL_TEMPLATE).getFile();
		String content = FileUtils.getContent(path, StandardCharsets.UTF_8);
		content = content.replace("page_title", LocalizerUtils.getLocalizedText(request, "page_title"))
				.replace("host", ServerProperties.ADMIN_HOST.getValue())
				.replace("random_pass", randomPass);
		Map<String, String> dictionary = LocalizerUtils.getLocalizedDictionary(request, "email_new_account");
		String subject = "";
		for (String key : dictionary.keySet()) {
			if (key.contains("subject")) {
				subject = dictionary.get(key);
			} else {
				content = content.replace(key, dictionary.get(key));
			}
		}
		sendEmail(to, subject, content);
	}

	public static void sendEmail(String to, String subject, String content) throws Exception {
		sendEmail(new String[] { to }, subject, content);
	}
	
	public static void sendEmail(String[] to, String subject, String content) throws Exception {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", ServerProperties.EMAIL_HOST.getValue());
		properties.put("mail.smtp.port", ServerProperties.EMAIL_PORT.getValue());
		properties.put("mail.smtp.auth", ServerProperties.EMAIL_SMTP_AUTH.getValue());
		properties.put("mail.smtp.ssl.trust", ServerProperties.EMAIL_SMTP_SSL_TRUST.getValue());
		properties.put("mail.smtp.ssl.protocols", ServerProperties.EMAIL_SMTP_SSL_PROTOCOLS.getValue());
		properties.put("mail.smtp.starttls.enable", ServerProperties.EMAIL_SMTP_STARTTTLS_ENABLE.getValue());
		properties.put("mail.smtp.connectiontimeout", SMTP_TIMEOUT);
		properties.put("mail.smtp.timeout", SMTP_TIMEOUT);
		
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
					ServerProperties.EMAIL_USER.getValue(),
					ServerProperties.EMAIL_PASS.getValue());
			}
		});

		String toList = getAddressList(to);
		
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(ServerProperties.EMAIL_FROM.getValue()));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toList));
		message.setSubject(subject);
		message.setText(content, "utf-8", "html");
		
		Transport.send(message);
		
		LOGGER.info(String.format(Locale.getDefault(), "Email sent to '%s' with subject '%s'.", toList, subject));
	}
	
	private static String getAddressList(String[] to) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < to.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(to[i]);
		}
		
		return builder.toString();
	}
}
