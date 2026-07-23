package nano.server.enums;

public enum EntityType {
	USERS("users");
	
	private String value;
	
	EntityType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public static EntityType fromValue(String value) {
		for (EntityType entityType : EntityType.values()) {
			if (entityType.getValue().equalsIgnoreCase(value)) {
				return entityType;
			}
		}
		
		return null;
	}
}
