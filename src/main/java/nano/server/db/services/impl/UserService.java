package nano.server.db.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.daos.UserDao;
import nano.server.db.entities.User;
import nano.server.db.services.def.IRoleService;
import nano.server.db.services.def.IUserService;

@Service
public class UserService implements IUserService {
	@Autowired
	private IRoleService roleService;
	
	@Override
	public List<User> allUsers() throws DBException {
		UserDao dao = new UserDao(roleService);
		
		String search = null;
		
		int total = dao.countEntities(search);
		
		return dao.allEntities(null, null, total, 0);
	}
	
	@Override
	public List<User> allActiveUsers() throws DBException {
		UserDao dao = new UserDao(roleService);
		
		String search = null;
		
		int total = dao.countEntities(search);
		
		List<User> users = dao.allEntities(null, null, total, 0);
		users.removeIf(u -> !u.isActive());
		
		return users;
	}

	@Override
	public List<User> allUsers(String search, String order, int limit, int offset) throws DBException {
		UserDao dao = new UserDao(roleService);
		
		return dao.allEntities(search, order, limit, offset);
	}

	@Override
	public int countUsers(String search) throws DBException {
		UserDao dao = new UserDao();
		
		return dao.countEntities(search);
	}

	@Override
	public User getUser(String email, String key) throws DBException {
		UserDao dao = new UserDao(roleService, key);
		
		return dao.getEntity(email);
	}

	@Override
	public void setUser(User user, String key) throws DBException {
		UserDao dao = new UserDao(key);
		
		dao.setEntity(user);
	}

	@Override
	public void delUser(String email) throws DBException {
		UserDao dao = new UserDao();
		
		dao.delEntity(email);
	}
}
