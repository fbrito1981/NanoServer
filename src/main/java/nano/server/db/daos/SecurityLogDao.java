package nano.server.db.daos;

import java.sql.ResultSet;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.SecurityLog;

public class SecurityLogDao extends BaseDao<SecurityLog> {
	@Override
	public DBScript getAllScript(String search, String order, int limit, int offset) {
		// Not implemented
		return null;
	}

	@Override
	public DBScript getCountScript(String search) {
		// Not implemented
		return null;
	}

	@Override
	public DBScript getGetScript(String key) {
		DBScript script = new DBScript(DBScriptType.GET, getEntityModel());
		script.addParam(new DBParam("mNonce", key, true));

		return script;
	}

	@Override
	public DBScript getSetScript(SecurityLog entity) {
		DBScript script = new DBScript(DBScriptType.SET, getEntityModel());
		script.addParam(new DBParam("mNonce", entity.getNonce(), true));
		
		return script;
	}

	@Override
	public DBScript getDelScript(String key) {
		// Not implemented
		return null;
	}

	@Override
	public SecurityLog getNewEntity(ResultSet resultSet) throws DBException {
		try {
			SecurityLog securityLog = new SecurityLog(
					resultSet.getString("none"),
					resultSet.getTimestamp("created"));
			
			return securityLog;
		} catch (Exception e) {
			throw new DBException(SecurityLog.class, e);
		}
	}

}
