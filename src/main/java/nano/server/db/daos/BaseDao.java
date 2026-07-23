package nano.server.db.daos;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nano.server.db.access.DBAccess;
import nano.server.db.access.DBException;
import nano.server.db.access.DBScript;

public abstract class BaseDao<T> {
	public abstract DBScript getAllScript(String search, String order, int limit, int offset);
	
	public abstract DBScript getCountScript(String search);
	
	public abstract DBScript getGetScript(String key);
	
	public abstract DBScript getSetScript(T entity);
	
	public abstract DBScript getDelScript(String key);

	public abstract T getNewEntity(ResultSet resultSet) throws DBException;
	
	public String getEntityModel() {
		return getClass().getSimpleName().replace("Dao", "");
	}
	
	public String getEntityModelWithSuffix(String suffix) {
		String customModel = String.format(Locale.getDefault(), "%s%s",
				getEntityModel(), suffix);
		
		return customModel;
	}

	public List<T> allEntities(String search, String order, int limit, int offset) throws DBException {
		DBScript script = getAllScript(search, order, limit, offset);
		
		return allEntities(script);
	}
	
	public List<T> allEntities(DBScript script) throws DBException {
		DBAccess dbAccess = new DBAccess();
		
		try {
			List<T> entities = new ArrayList<>();
			
			ResultSet resultSet = dbAccess.getResultSet(script);
			if (resultSet != null) {
				while (resultSet.next()) {
					T entity = getNewEntity(resultSet);
					
					entities.add(entity);
				}
			}
			resultSet.close();
			
			return entities;
		} catch (Exception e) {
			throw new DBException(script, e);
		} finally {
			dbAccess.close();
		}
	}

	public int countEntities(String search) throws DBException {
		DBScript script = getCountScript(search);
		
		return countEntities(script);
	}

	public int countEntities(DBScript script) throws DBException {
		DBAccess dbAccess = new DBAccess();
		
		try {
			int count = 0;
			
			ResultSet resultSet = dbAccess.getResultSet(script);
			if (resultSet != null && resultSet.next()) {
				count = resultSet.getInt("count");
			}
			resultSet.close();
			
			return count;
		} catch (Exception e) {
			throw new DBException(script, e);
		} finally {
			dbAccess.close();
		}
	}

	public T getEntity(String key) throws DBException {
		DBScript script = getGetScript(key);
		
		return getEntity(script);
	}
	
	public T getEntity(DBScript script) throws DBException {
		DBAccess dbAccess = new DBAccess();
		
		try {
			T enitity = null;
			
			ResultSet resultSet = dbAccess.getResultSet(script);
			if (resultSet != null && resultSet.next()) {
				enitity = getNewEntity(resultSet);
			}
			resultSet.close();
			
			return enitity;
		} catch (Exception e) {
			throw new DBException(script, e);
		} finally {
			dbAccess.close();
		}
	}

	public void setEntity(T entity) throws DBException {
		DBScript script = getSetScript(entity);
		
		DBAccess dbAccess = new DBAccess();
		
		try {
			dbAccess.getResultSet(script).close();
		} catch (Exception e) {
			throw new DBException(script, e);
		} finally {
			dbAccess.close();
		}
	}

	public void delEntity(String key) throws DBException {
		DBScript script = getDelScript(key);
		
		delEntity(script);
	}
	
	public void delEntity(DBScript script) throws DBException {
		DBAccess dbAccess = new DBAccess();
		
		try {
			dbAccess.getResultSet(script).close();
		} catch (Exception e) {
			throw new DBException(script, e);
		} finally {
			dbAccess.close();
		}
	}
}
