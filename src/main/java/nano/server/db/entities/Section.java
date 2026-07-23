package nano.server.db.entities;

import java.util.Locale;

public class Section implements Comparable<Section> {
	private String name;
	private Role minRole;
	private int menuOrder;
	
	public Section() {		
	}

	public Section(String name, Role minRole, int menuOrder) {
		this.name = name;
		this.minRole = minRole;
		this.menuOrder = menuOrder;
	}
	
	public String getName() {
		return name;
	}
	
	public Role getMinRole() {
		return minRole;
	}
	
	public void setMinRole(Role minRole) {
		this.minRole = minRole;
	}

	public int getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(int menuOrder) {
		this.menuOrder = menuOrder;
	}
	
	public String getMenuLabel() {
		return String.format(Locale.getDefault(), "menu_item_%s", name);
	}
	
	public boolean hasAccess(Role role) {
		return role.getLevel() <= minRole.getLevel();
	}

	@Override
	public int compareTo(Section o) {
		return Integer.compare(menuOrder, o.getMenuOrder());
	}
}
