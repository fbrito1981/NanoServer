package nano.server.db.services.def;

import java.util.Date;
import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.EnergyLog;

public interface IEnergyLogService {
	List<EnergyLog> allEnergyLogs(String search, String order, int limit, int offset) throws DBException;
	
	int countEnergyLogs(String search) throws DBException;
	
	EnergyLog getEnergyLog(String deviceUuid) throws DBException;
	
	void setEnergyLog(EnergyLog energyLog) throws DBException;
	
	List<EnergyLog> allEnergyLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyLog> allEnergyLogsByHours(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyLog> allEnergyLogsByDays(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyLog> allEnergyLogsByMonths(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyLog> allEnergyLogsByYears(String deviceUuid, Date from, Date until) throws DBException;
}
