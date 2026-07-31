package nano.server.enums;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

import nano.server.utils.FileUtils;

public enum ServerProperties {
	DB_HOST("dbHost"),
	DB_USER("dbUser"),
	DB_PASS("dbPass"),
	ADMIN_HOST("adminHost"),
	EMAIL_USER("emailUser"),
	EMAIL_PASS("emailPass"),
	EMAIL_FROM("emailFrom"),
	EMAIL_HOST("emailHost"),
	EMAIL_PORT("emailPort"),
	EMAIL_SMTP_AUTH("emailSmtpAuth"),
	EMAIL_SMTP_SSL_TRUST("mailSmtpSslTrust"),
	EMAIL_SMTP_SSL_PROTOCOLS("mailSmtpSslProtocols"),
	EMAIL_SMTP_STARTTTLS_ENABLE("emailSmtpStartTtls"),
	ALLOW_EXTERNAL_ACCESS("allowExternalAccess"),
	SECURITY_KEY("securityKey"),
	SECURITY_SECRET("securitySecret"),
	GROK_API_KEY("grokApiKey"),
	GROK_MODEL("grokModel");
	
	private static final String PROPERTIES_FILE_NAME = "app.properties";
	
	private String name;
	
	ServerProperties(String name) {
		this.name = name; 
	}
	
	private static Properties properties;
	
	static {
		properties = new Properties();
		
		FileInputStream in = null;
		InputStreamReader reader = null;
		
		try {
			String path = FileUtils.getBasePath(FolderType.CONFIG);
			String filePath = String.format(Locale.getDefault(),
					"%s%s%s", path, File.separator, PROPERTIES_FILE_NAME);
			
			in = new FileInputStream(filePath);
			reader = new InputStreamReader(in);
			
			properties.load(reader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return properties.getProperty(name);
	}
}
