package nano.server.db.services.def;

import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.User;

public interface IUserService {
	List<User> allUsers() throws DBException;
	
	List<User> allActiveUsers() throws DBException;
	
	List<User> allUsers(String search, String order, int limit, int offset) throws DBException;
	
	int countUsers(String search) throws DBException;
	
	User getUser(String email, String key) throws DBException;
	
	void setUser(User user, String key) throws DBException;
	
	void delUser(String email) throws DBException;
}
