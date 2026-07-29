package nano.server.enums;

public enum ResponseType {
	SC_UNAUTHORIZED(401),
	SC_INTERNAL_SERVER_ERROR(500),
	SC_INVALID_CREDENTIALS(800),
	SC_INACTIVE_USER(801),
	SC_INVALID_RECOVERY_CODE(802),
	SC_EXPIRED_RECOVERY_CODE(803),
	SC_INVALID_USER(804),
	SC_INVALID_DEVICE(805),
	SC_INVALID_PARAMS(806),
	SC_DUPLICATE_DEVICE(807);
	
	private int value;
	
	ResponseType(int value) {
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}
	
	public String strValue() {
		return String.valueOf(value);
	}
}
