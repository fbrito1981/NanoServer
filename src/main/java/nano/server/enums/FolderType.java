package nano.server.enums;

public enum FolderType {
	CONFIG("conf"),
	FILES("files");
	
	private String value;
	
	FolderType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
