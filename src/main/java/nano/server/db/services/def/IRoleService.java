package nano.server.db.services.def;

import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.Role;

public interface IRoleService {
	List<Role> allRoles() throws DBException;
	
	List<Role> allRoles(String search, String order, int limit, int offset) throws DBException;
	
	int countRoles(String search) throws DBException;
	
	Role getRole(String name) throws DBException;
	
	void setRole(Role role) throws DBException;
	
	void delRole(String name) throws DBException;
}
