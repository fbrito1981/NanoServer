package nano.server.db.access;

import java.awt.List;

public class DBParam {
	private String name;
	private Object value;
	private boolean quoted;

	public DBParam(String name, Object value, boolean quoted) {
		this.name = name;
		this.value = value;
		this.quoted = quoted;
	}
	
	public String getName() {
		return name;
	}

	public String getValue() {
		return value.toString();
	}
	
	public String getListValues() {
		return value.toString().replace("[", "").replace("]", "");
	}

	public boolean isQuoted() {
		return quoted;
	}
	
	public boolean isNull() {
		return value == null;
	}
	
	public boolean isAList() {
		return value.getClass().isInstance(List.class);
	}
}
