package nano.server.db.services.def;

import nano.server.db.access.DBException;
import nano.server.db.entities.SecurityLog;

public interface ISecurityLogService {
	SecurityLog getSecurityLog(String nonce) throws DBException;
	
	void setSecurityLog(SecurityLog securityLog) throws DBException;
}
