package nano.server.db.entities;

import nano.server.utils.KeyUtils;

public abstract class KeyEntity {
	public abstract String getKey();
	
	protected String getKey(String[] items) {
		return KeyUtils.group(items);
	}
	
	static final String getKeyPart(String key, int part) {
		return KeyUtils.pick(key, part);
	}
}
