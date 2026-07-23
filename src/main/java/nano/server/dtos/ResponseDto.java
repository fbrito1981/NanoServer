package nano.server.dtos;

import nano.server.enums.ResponseAction;

public class ResponseDto {
	private boolean success;
	private int action;
	private String url;
	private Object data;
	private String message;
	
	public ResponseDto(boolean success, ResponseAction action, String url, Object data, String message) {
		this.success = success;
		this.action = action.getValue();
		this.url = url;
		this.data = data;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public int getAction() {
		return action;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Object getData() {
		return data;
	}
	
	public String getMessage() {
		return message;
	}
}
