package nano.server.db.daos;

import java.sql.ResultSet;
import java.sql.SQLException;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.Role;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;

public class UserDao extends BaseDao<User> {
	private IRoleService roleService;
	private String secret;

	public UserDao() {
		this(null, null);
	}
	
	public UserDao(IRoleService roleService) {
		this(roleService, null);
	}
	
	public UserDao(String secret) {
		this(null, secret);
	}
	
	public UserDao(IRoleService roleService, String secret) {
		this.roleService = roleService;
		this.secret = secret;
	}
	
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
	public DBScript getGetScript(String key) {
		DBScript script = new DBScript(DBScriptType.GET, getEntityModel());
		script.addParam(new DBParam("mEmail", key, true));
		script.addParam(new DBParam("mKey", secret, true));
		
		return script;
	}

	@Override
	public DBScript getSetScript(User entity) {
		DBScript script = new DBScript(DBScriptType.SET, getEntityModel());
		script.addParam(new DBParam("mEmail", entity.getEmail(), true));
		script.addParam(new DBParam("mPass", entity.getPass(), true));
		script.addParam(new DBParam("mKey", secret, true));
		script.addParam(new DBParam("mName", entity.getName(), true));
		script.addParam(new DBParam("mActive", entity.isActive(), false));
		script.addParam(new DBParam("mResetCode", entity.getResetCode(), false));
		script.addParam(new DBParam("mRoleName", entity.getRole().getName(), true));
		script.addParam(new DBParam("mToken", entity.getToken(), true));
		script.addParam(new DBParam("mPicture", entity.getPicture(), true));
		
		return script;
	}

	@Override
	public DBScript getDelScript(String key) {
		DBScript script = new DBScript(DBScriptType.DEL, getEntityModel());
		script.addParam(new DBParam("mEmail", key, true));

		return script;
	}

	@Override
	public User getNewEntity(ResultSet resultSet) throws DBException {
		try {
			Role role = roleService.getRole(resultSet.getString("roleName"));
			
			User user = new User(resultSet.getString("email"),
					resultSet.getString("pass"),
					resultSet.getString("name"),
					resultSet.getBoolean("active"),
					resultSet.getInt("resetCode"),
					role,
					resultSet.getString("token"),
					resultSet.getString("picture"),
					resultSet.getTimestamp("created"),
					resultSet.getTimestamp("updated"));
			
			return user;
		} catch (SQLException e) {
			throw new DBException(User.class, e);
		}
	}
}
