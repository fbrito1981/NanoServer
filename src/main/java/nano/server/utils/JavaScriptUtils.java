package nano.server.utils;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JavaScriptUtils {
	public static String escape(String value) throws Exception {
		Param param = new Param(true, value);
		List<Param> params = new ArrayList<>();
		params.add(param);
		return evalFunction("escape", params).toString();
	}

	public static String unescape(String value) throws Exception {
		Param param = new Param(true, value);
		List<Param> params = new ArrayList<>();
		params.add(param);
		return evalFunction("unescape", params).toString();
	}
	
	public static Object evalFunction(String function, List<Param> params) throws Exception {
		StringBuilder command = new StringBuilder();
		command.append(function);
		command.append("(");
		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				Param param = params.get(i);
				if (i > 0) {
					command.append(", ");
				}
				if (param.isQuoted()) {
					command.append("'");
					command.append(param.getValue());
					command.append("'");
				} else {
					command.append(param.getValue());
				}
			}
		}
		command.append(")");
		return eval(command.toString());
	}
	
	public static Object eval(String command) throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		return engine.eval(command);
	}
	
	public static class Param {
		private boolean quoted;
		private Object value;
		
		public Param(boolean quoted, Object value) {
			this.quoted = quoted;
			this.value = value;
		}
		
		public boolean isQuoted() {
			return quoted;
		}
		
		public String getValue() {
			return value.toString();
		}
	}
}
