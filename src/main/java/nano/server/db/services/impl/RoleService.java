package nano.server.db.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.daos.RoleDao;
import nano.server.db.entities.Role;
import nano.server.db.services.def.IRoleService;

@Service
public class RoleService implements IRoleService {
	@Override
	public List<Role> allRoles() throws DBException {
		RoleDao dao = new RoleDao();
		
		String search = null;
		
		int total = dao.countEntities(search);
		
		return dao.allEntities(null, null, total, 0);
	}
	
	@Override
	public List<Role> allRoles(String search, String order, int limit, int offset) throws DBException {
		RoleDao dao = new RoleDao();
		
		return dao.allEntities(search, order, limit, offset);
	}
	
	@Override
	public int countRoles(String search) throws DBException {
		RoleDao dao = new RoleDao();
		
		return dao.countEntities(search);
	}
	
	@Override
	public Role getRole(String name) throws DBException {
		RoleDao dao = new RoleDao();
		
		return dao.getEntity(name);
	}

	@Override
	public void setRole(Role role) throws DBException {
		RoleDao dao = new RoleDao();
		
		dao.setEntity(role);
	}

	@Override
	public void delRole(String name) throws DBException {
		RoleDao dao = new RoleDao();
		
		dao.delEntity(name);
	}
}
