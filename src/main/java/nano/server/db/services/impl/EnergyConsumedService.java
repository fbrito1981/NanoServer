package nano.server.db.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.access.DBScript;
import nano.server.db.daos.EnergyConsumedDao;
import nano.server.db.entities.EnergyConsumed;
import nano.server.db.services.def.IDeviceService;
import nano.server.db.services.def.IEnergyConsumedService;

@Service
public class EnergyConsumedService implements IEnergyConsumedService {
	@Autowired
	private IDeviceService deviceService;

	@Override
	public List<EnergyConsumed> allEnergyLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException {
		EnergyConsumedDao dao = new EnergyConsumedDao(deviceService);
		
		DBScript script = dao.getAllByMinutesScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyConsumed> allEnergyLogsByHours(String deviceUuid, Date from, Date until) throws DBException {
		EnergyConsumedDao dao = new EnergyConsumedDao(deviceService);
		
		DBScript script = dao.getAllByHoursScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyConsumed> allEnergyLogsByDays(String deviceUuid, Date from, Date until) throws DBException {
		EnergyConsumedDao dao = new EnergyConsumedDao(deviceService);
		
		DBScript script = dao.getAllByDaysScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyConsumed> allEnergyLogsByMonths(String deviceUuid, Date from, Date until) throws DBException {
		EnergyConsumedDao dao = new EnergyConsumedDao(deviceService);
		
		DBScript script = dao.getAllByMonthsScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}

	@Override
	public List<EnergyConsumed> allEnergyLogsByYears(String deviceUuid, Date from, Date until) throws DBException {
		EnergyConsumedDao dao = new EnergyConsumedDao(deviceService);
		
		DBScript script = dao.getAllByYearsScript(deviceUuid, from, until);
		
		return dao.allEntities(script);
	}
}
