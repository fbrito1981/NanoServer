package nano.server.db.access;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DBScript {
	private DBScriptType type;
	private String model;
	private List<DBParam> params;
	
	public DBScript(DBScriptType type, String model) {
		this.type = type;
		this.model = model;
		this.params = new ArrayList<>();
	}
	
	public void addParam(DBParam param) {
		this.params.add(param);
	}
	
	public List<DBParam> getParams() {
		return params;
	}
	
	public String getMethod() {
		String template = null;
		
		switch (type) {
		case ALL:
			template = "spAll%ss";
			break;
		case COUNT:
			template = "spCount%ss";
			break;
		case GET:
			template = "spGet%s";
			break;
		case SET:
			template = "spSet%s";
			break;
		case DEL:
			template = "spDel%s";
			break;
		}
		
		return String.format(Locale.getDefault(),
				template,
				model);
	}
}
