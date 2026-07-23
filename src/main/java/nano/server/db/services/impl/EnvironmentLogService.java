package nano.server.db.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.access.DBScript;
import nano.server.db.daos.EnvironmentLogDao;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnvironmentLogService;

@Service
public class EnvironmentLogService implements IEnvironmentLogService {
	@Autowired
	private IDeviceService deviceService;

	@Override
	public List<EnvironmentLog> allEnvironmentLogs(String search, String order, int limit, int offset) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		return dao.allEntities(search, order, limit, offset);
	}

	@Override
	public int countEnvironmentLogs(String search) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		return dao.countEntities(search);
	}

	@Override
	public EnvironmentLog getEnvironmentLog(String deviceUuid) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getGetScript(deviceUuid);

		return dao.getEntity(script);
	}

	@Override
	public void setEnvironmentLog(EnvironmentLog environmentLog) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		dao.setEntity(environmentLog);
	}

	@Override
	public List<EnvironmentLog> allEnvironmentLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getAllByMinutesScript(deviceUuid, from, until);

		return dao.allEntities(script);
	}

	@Override
	public List<EnvironmentLog> allEnvironmentLogsByHours(String deviceUuid, Date from, Date until) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getAllByHoursScript(deviceUuid, from, until);

		return dao.allEntities(script);
	}

	@Override
	public List<EnvironmentLog> allEnvironmentLogsByDays(String deviceUuid, Date from, Date until) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getAllByDaysScript(deviceUuid, from, until);

		return dao.allEntities(script);
	}

	@Override
	public List<EnvironmentLog> allEnvironmentLogsByMonths(String deviceUuid, Date from, Date until) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getAllByMonthsScript(deviceUuid, from, until);

		return dao.allEntities(script);
	}

	@Override
	public List<EnvironmentLog> allEnvironmentLogsByYears(String deviceUuid, Date from, Date until) throws DBException {
		EnvironmentLogDao dao = new EnvironmentLogDao(deviceService);

		DBScript script = dao.getAllByYearsScript(deviceUuid, from, until);

		return dao.allEntities(script);
	}
}
