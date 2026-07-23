package nano.server.db.daos;

import java.sql.ResultSet;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.Role;
import nano.server.db.entities.Section;
import nano.server.db.services.def.IRoleService;

public class SectionDao extends BaseDao<Section> {
	private IRoleService roleService;
	
	public SectionDao() {
		this(null);
	}
	
	public SectionDao(IRoleService roleService) {
		this.roleService = roleService;
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
		script.addParam(new DBParam("mName", key, true));
		
		return script;
	}

	@Override
	public DBScript getSetScript(Section entity) {
		DBScript script = new DBScript(DBScriptType.SET, getEntityModel());
		script.addParam(new DBParam("mName", entity.getName(), true));
		script.addParam(new DBParam("mMinRoleName", entity.getMinRole().getName(), true));
		script.addParam(new DBParam("mMenuOrder", entity.getMenuOrder(), false));
		
		return script;
	}

	@Override
	public DBScript getDelScript(String key) {
		DBScript script = new DBScript(DBScriptType.DEL, getEntityModel());
		script.addParam(new DBParam("mName", key, true));
		
		return script;
	}

	@Override
	public Section getNewEntity(ResultSet resultSet) throws DBException {
		try {
			Role role = roleService.getRole(resultSet.getString("minRoleName"));
			
			Section section = new Section(resultSet.getString("name"),
					role, resultSet.getInt("menuOrder"));
			
			return section;
		} catch (Exception e) {
			throw new DBException(Section.class, e);
		}
	}
}
