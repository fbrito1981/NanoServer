package nano.server.enums;

public enum FileType {
	IMAGES("images", "image/png");
	
	private String value;
	private String contentType;
	
	FileType(String value, String contentType) {
		this.value = value;
		this.contentType = contentType;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public static FileType fromValue(String value) {
		for (FileType fileType : FileType.values()) {
			if (fileType.getValue().equalsIgnoreCase(value)) {
				return fileType;
			}
		}
		
		return null;
	}
}
