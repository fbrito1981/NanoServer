package nano.server.db.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.access.DBScript;
import nano.server.db.daos.EnergyLogDao;
import nano.server.db.entities.EnergyLog;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyLogService;

@Service
public class EnergyLogService implements IEnergyLogService {
	@Autowired
	private IDeviceService deviceService;
	
	@Override
	public List<EnergyLog> allEnergyLogs(String search, String order, int limit, int offset) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		return dao.allEntities(search, order, limit, offset);
	}

	@Override
	public int countEnergyLogs(String search) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		return dao.countEntities(search);
	}
	
	@Override
	public EnergyLog getEnergyLog(String deviceUuid) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getGetScript(deviceUuid);
		
		return dao.getEntity(script);
	}

	@Override
	public void setEnergyLog(EnergyLog energyLog) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		dao.setEntity(energyLog);
	}

	@Override
	public List<EnergyLog> allEnergyLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getAllByMinutesScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyLog> allEnergyLogsByHours(String deviceUuid, Date from, Date until) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getAllByHoursScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyLog> allEnergyLogsByDays(String deviceUuid, Date from, Date until) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getAllByDaysScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyLog> allEnergyLogsByMonths(String deviceUuid, Date from, Date until) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getAllByMonthsScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyLog> allEnergyLogsByYears(String deviceUuid, Date from, Date until) throws DBException {
		EnergyLogDao dao = new EnergyLogDao(deviceService);
		
		DBScript script = dao.getAllByYearsScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}
}
