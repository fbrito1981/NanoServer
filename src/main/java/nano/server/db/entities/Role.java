package nano.server.db.entities;

public class Role implements Comparable<Role> {
	private String name;
	private int level;
	
	public Role() {
	}
	
	public Role(String name, int level) {
		this.name = name;
		this.level = level;
	}
	
	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public int compareTo(Role o) {
		return Integer.compare(level, o.getLevel());
	}
}
