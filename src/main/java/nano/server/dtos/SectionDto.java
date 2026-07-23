package nano.server.dtos;

public class SectionDto {
	private String name;
	private String label;
	private String minRoleName;
	private Integer menuOrder;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMinRoleName() {
		return minRoleName;
	}

	public void setMinRoleName(String minRoleName) {
		this.minRoleName = minRoleName;
	}

	public Integer getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}
}
