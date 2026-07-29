package nano.server.dtos;

import java.util.List;

public class ChatRequestDto {
	private List<ChatTurnDto> history;
	private String message;

	public List<ChatTurnDto> getHistory() {
		return history;
	}

	public void setHistory(List<ChatTurnDto> history) {
		this.history = history;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
