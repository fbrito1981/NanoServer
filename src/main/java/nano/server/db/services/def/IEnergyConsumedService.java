package nano.server.db.services.def;

import java.util.Date;
import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.EnergyConsumed;

public interface IEnergyConsumedService {
	List<EnergyConsumed> allEnergyLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyConsumed> allEnergyLogsByHours(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyConsumed> allEnergyLogsByDays(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyConsumed> allEnergyLogsByMonths(String deviceUuid, Date from, Date until) throws DBException;
	
	List<EnergyConsumed> allEnergyLogsByYears(String deviceUuid, Date from, Date until) throws DBException;
}
