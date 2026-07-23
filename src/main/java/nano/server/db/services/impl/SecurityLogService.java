package nano.server.db.services.impl;

import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.daos.SecurityLogDao;
import nano.server.db.entities.SecurityLog;
import nano.server.db.services.def.ISecurityLogService;

@Service
public class SecurityLogService implements ISecurityLogService {
	@Override
	public SecurityLog getSecurityLog(String nonce) throws DBException {
		SecurityLogDao dao = new SecurityLogDao();
		
		return dao.getEntity(nonce);
	}

	@Override
	public void setSecurityLog(SecurityLog securityLog) throws DBException {
		SecurityLogDao dao = new SecurityLogDao();
		
		dao.setEntity(securityLog);
	}
}
