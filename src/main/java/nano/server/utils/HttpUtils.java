package nano.server.utils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import nano.server.enums.ServerProperties;

public class HttpUtils {
	public static String doGet(String completeUri) throws Exception {
		return doGet(completeUri, false);
	}
	
	public static String doGet(String uri, Map<String, Object> params) throws Exception {
		return doGet(uri, params, false);
	}
	
	public static String doGet(String uri, Map<String, Object> params, boolean secure) throws Exception {
		StringBuilder completeUri = new StringBuilder();
		completeUri.append(uri);
		
		if (params != null && !params.isEmpty()) {
			completeUri.append("?");

			for (String key : params.keySet()) {
				if (!completeUri.toString().endsWith("?")) {
					completeUri.append("&");
				}
				completeUri.append(key).append("=").append(params.get(key));
			}
		}
		
		return doGet(completeUri.toString(), secure);
	}
	
	public static String doGet(String completeUri, boolean secure) throws Exception {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet httpGet = new HttpGet(completeUri);
		
		if (secure) {
			WSSecurityUtils.setHeaderAuthorization(httpGet,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue());
		}
		
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		
		String result = null;
		
		if (httpEntity != null) {
			result = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
		}
		
		httpResponse.close();
		httpClient.close();
		
		return result;
	}
	
	public static String doPost(String uri) throws Exception {
		return doPost(uri, null, false);
	}
	
	public static String doPost(String uri, boolean secure) throws Exception {
		return doPost(uri, null, secure);
	}
	
	public static String doPost(String uri, List<NameValuePair> nameValuePairs) throws Exception {
		return doPost(uri, nameValuePairs, false);
	}
	
	public static String doPost(String uri, List<NameValuePair> nameValuePairs, boolean secure) throws Exception {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = new HttpPost(uri);
		
		if (secure) {
			WSSecurityUtils.setHeaderAuthorization(httpPost,
					ServerProperties.SECURITY_KEY.getValue(), ServerProperties.SECURITY_SECRET.getValue());
		}
		
		if (nameValuePairs != null) {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		
		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		
		String result = null;
		
		if (httpEntity != null) {
			result = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
		}
		
		httpResponse.close();
		httpClient.close();
		
		return result;
	}
}
