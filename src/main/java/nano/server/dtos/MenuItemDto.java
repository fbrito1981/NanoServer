package nano.server.dtos;

public class MenuItemDto {
	private String title;
	private String section;
	private boolean active;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSection() {
		return section;
	}
	
	public void setSection(String section) {
		this.section = section;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getIcon() {
		String sectionName = section.substring(section.lastIndexOf("/") + 1);
		
		switch (sectionName) {
		case "roles":
			return "fas fa-user-tag";
		case "sections":
			return "fas fa-bars";
		case "users":
			return "fas fa-id-card";
		case "devices":
			return "fas fa-microchip";
		case "energy":
			return "fas fa-lightbulb";
		case "environment":
			return "fas fa-thermometer-half";
		default:
			return "fas fa-times-circle";
		}
	}
}
