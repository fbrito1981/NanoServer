package nano.server.dtos;

import nano.server.enums.ResponseAction;

public class RedirectDto extends ResponseDto {
	public RedirectDto(boolean success, String url, String message) {
		super(success, ResponseAction.REDIRECT, url, null, message);
	}
	
	public RedirectDto(boolean success, String url, Object data, String message) {
		super(success, ResponseAction.REDIRECT, url, data, message);
	}
}
