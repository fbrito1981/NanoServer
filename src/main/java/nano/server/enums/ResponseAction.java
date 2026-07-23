package nano.server.enums;

public enum ResponseAction {
	RESULT_NO_MESSAGE(-1),
	RESULT(0),
	REDIRECT(1);
	
	private int value;
	
	ResponseAction(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
