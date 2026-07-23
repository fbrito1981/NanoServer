package nano.server.db.daos;

import java.sql.ResultSet;
import java.sql.SQLException;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.Device;

public class DeviceDao extends BaseDao<Device> {
	@Override
	public DBScript getAllScript(String search, String order, int limit, int offset) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModel());
		script.addParam(new DBParam("mSearch", search, true));
		script.addParam(new DBParam("mOrder", order, true));
		script.addParam(new DBParam("mLimit", limit, false));
		script.addParam(new DBParam("mOffset", offset, false));
		
		return script;
	}

	@Override
	public DBScript getCountScript(String search) {
		DBScript script = new DBScript(DBScriptType.COUNT, getEntityModel());
		script.addParam(new DBParam("mSearch", search, true));
		
		return script;
	}

	@Override
	public DBScript getGetScript(String uuid) {
		DBScript script = new DBScript(DBScriptType.GET, getEntityModel());
		script.addParam(new DBParam("mUuid", uuid, true));
		
		return script;
	}

	public DBScript getGetByUserEmailAndNameScript(String userEmail, String name) {
		DBScript script = new DBScript(DBScriptType.GET, getEntityModelWithSuffix("ByUserEmailAndName"));
		script.addParam(new DBParam("mUserEmail", userEmail, true));
		script.addParam(new DBParam("mName", name, true));
		
		return script;
	}

	@Override
	public DBScript getSetScript(Device entity) {
		DBScript script = new DBScript(DBScriptType.SET, getEntityModel());
		script.addParam(new DBParam("mUuid", entity.getUuid(), true));
		script.addParam(new DBParam("mUserEmail", entity.getUserEmail(), true));
		script.addParam(new DBParam("mName", entity.getName(), true));
		script.addParam(new DBParam("mModel", entity.getModel(), true));
		script.addParam(new DBParam("mOs", entity.getOs(), true));
		script.addParam(new DBParam("mVersion", entity.getVersion(), true));
		script.addParam(new DBParam("mActive", entity.isActive(), false));
		
		return script;
	}

	@Override
	public DBScript getDelScript(String uuid) {
		DBScript script = new DBScript(DBScriptType.DEL, getEntityModel());
		script.addParam(new DBParam("mUuid", uuid, true));
		
		return script;
	}

	@Override
	public Device getNewEntity(ResultSet resultSet) throws DBException {
		try {
			Device device = new Device(resultSet.getNString("uuid"),
					resultSet.getString("userEmail"),
					resultSet.getString("name"),
					resultSet.getString("model"),
					resultSet.getString("os"),
					resultSet.getString("version"),
					resultSet.getBoolean("active"),
					resultSet.getTimestamp("created"),
					resultSet.getTimestamp("updated"));

			return device;
		} catch (SQLException e) {
			throw new DBException(Device.class, e);
		}
	}
}
