package nano.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.ModelMap;

public class LocalizerUtils {
	private final static String RESOURCE_BUNDLE = "localization.dictionary";
	private final static String LOCALE_ATTRIBUTE = "localeValue";
	private final static String DEFAULT_LOCALE = "es";
	private final static String[] AVAILABLE_LOCALES = new String[] { DEFAULT_LOCALE, "en" };
	private final static String PAGE_TITLE_KEY = "page_title";
	private final static String LOGOUT_BTN_KEY = "logout_btn";
	private final static String GENERAL_ERROR_MESSAGE_KEY = "error_msg_general";
	
	private static Logger LOGGER = LogManager.getLogger(LocalizerUtils.class);

	private ResourceBundle bundle;
    
    private LocalizerUtils(String localeValue) throws Exception {
    	this.bundle = ResourceBundle.getBundle(
				RESOURCE_BUNDLE,
				new Locale(localeValue),
				this.getClass().getClassLoader(),
				new ResourceBundle.Control() {
					@Override
					public ResourceBundle newBundle(String baseName, Locale locale, String format,
							ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
						String bundleName = toBundleName(baseName, locale);
						String resourceName = toResourceName(bundleName, "properties");
						InputStream stream = loader.getResourceAsStream(resourceName);
						if (stream == null) return null;
						try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
							return new PropertyResourceBundle(reader);
						}
					}
				});
    }
    
    private ResourceBundle getBundle() {
    	return bundle;
    }
    
    public static String[] getAvailableLocales() {
    	return AVAILABLE_LOCALES;
    }
    
    public static String getCurrentLocale(HttpServletRequest request) {
    	try {
        	Object value = request.getSession().getAttribute(LOCALE_ATTRIBUTE);
        	if (value != null) {
        		return value.toString();
        	}
    	} catch (Exception e) {
            logException(e);
    	}
    	
		return DEFAULT_LOCALE;
    }
    
    public static void setLocale(HttpServletRequest request, String locale) {
    	request.getSession().setAttribute(LOCALE_ATTRIBUTE, locale);
    }
    
    public static String getLocalizedText(ResourceBundle bundle, String key) {
    	if (bundle.keySet().contains(key)) {
    		return bundle.getString(key);
    	} else {
    		return String.format(Locale.getDefault(), "%s (No localization entry found)", key);
    	}
    }
    
    public static String getLocalizedText(HttpServletRequest request, String key) {
        try {
        	String localeValue = getCurrentLocale(request);
        	ResourceBundle bundle = new LocalizerUtils(localeValue).getBundle();
    
        	return getLocalizedText(bundle, key);
        } catch (Exception e) {
            logException(e);
        	
            return String.format(Locale.getDefault(), "Exception localizing [%s]: %s", key, e.toString());
        }
    }
    
    public static Map<String, String> getLocalizedDictionary(HttpServletRequest request, String modelPath) {
    	Map<String, String> dictionary = new HashMap<>();
    	
    	try {
        	String localeValue = getCurrentLocale(request);
        	ResourceBundle bundle = new LocalizerUtils(localeValue).getBundle();

        	if (!bundle.keySet().isEmpty()) {
        		for (String key : bundle.keySet()) {
        			if (key.startsWith(modelPath)) {
        				dictionary.put(key, getLocalizedText(bundle, key));
        			}
        		}
        	}
        	
        	dictionary.put(PAGE_TITLE_KEY, getLocalizedText(bundle, PAGE_TITLE_KEY));
        	dictionary.put(LOGOUT_BTN_KEY, getLocalizedText(bundle, LOGOUT_BTN_KEY));
        	dictionary.put(GENERAL_ERROR_MESSAGE_KEY, getLocalizedText(bundle, GENERAL_ERROR_MESSAGE_KEY));
    	} catch (Exception e) {
            logException(e);
    	}
    	
    	return dictionary;
    }
    
    public static void localizeModel(ModelMap model, HttpServletRequest request, String modelPath) {
        try {
        	Map<String, String> dictionary = getLocalizedDictionary(request, modelPath);
    
        	if (!dictionary.isEmpty()) {
        		for (String key : dictionary.keySet()) {
    				model.put(key, dictionary.get(key));
        		}
        	}
        } catch (Exception e) {
            logException(e);
        }
    }
	
	private static void logException(Throwable t) {
		String errorMessage = String.format(Locale.getDefault(),
				"Exception in 'LocalizerUtils': %s.",
				t.getMessage());
		
		LOGGER.error(errorMessage, t);
	}
}
