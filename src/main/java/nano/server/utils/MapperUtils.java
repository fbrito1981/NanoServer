package nano.server.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

public class MapperUtils {
	public static String getString(Object object) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		
		return objectMapper.writeValueAsString(object);
	}
	
	public static <T> T getObject(String value, Class<T> tClass) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		objectMapper.addHandler(new DeserializationProblemHandler() {
			@Override
			public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
					String valueToConvert, String failureMsg) throws IOException {
				if (targetType == Boolean.class) {
					String[] trueValues = { "true", "on", "1" };
					for (String trueValue : trueValues) {
						if (valueToConvert.equalsIgnoreCase(trueValue)) {
							return Boolean.TRUE;
						}
					}
					String[] falseValues = { "false", "off", "0" };
					for (String falseValue : falseValues) {
						if (valueToConvert.equalsIgnoreCase(falseValue)) {
							return Boolean.FALSE;
						}
					}
				}
				
				return super.handleWeirdStringValue(ctxt, targetType, valueToConvert, failureMsg);
			}
		});
		
		return objectMapper.readValue(value, tClass);
	}
}
