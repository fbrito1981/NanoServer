package nano.server.dtos;

import nano.server.enums.ResponseAction;

public class ResultDto extends ResponseDto {
	public ResultDto(boolean success, Object data, String message) {
		super(success, ResponseAction.RESULT, null, data, message);
	}
	
	public ResultDto(boolean success, Object data) {
		super(success, ResponseAction.RESULT_NO_MESSAGE, null, data, null);
	}
}
