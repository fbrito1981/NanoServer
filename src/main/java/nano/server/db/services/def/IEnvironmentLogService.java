package nano.server.db.services.def;

import java.util.Date;
import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.EnvironmentLog;

public interface IEnvironmentLogService {
	List<EnvironmentLog> allEnvironmentLogs(String search, String order, int limit, int offset) throws DBException;

	int countEnvironmentLogs(String search) throws DBException;

	EnvironmentLog getEnvironmentLog(String deviceUuid) throws DBException;

	void setEnvironmentLog(EnvironmentLog environmentLog) throws DBException;

	List<EnvironmentLog> allEnvironmentLogsByMinutes(String deviceUuid, Date from, Date until) throws DBException;

	List<EnvironmentLog> allEnvironmentLogsByHours(String deviceUuid, Date from, Date until) throws DBException;

	List<EnvironmentLog> allEnvironmentLogsByDays(String deviceUuid, Date from, Date until) throws DBException;

	List<EnvironmentLog> allEnvironmentLogsByMonths(String deviceUuid, Date from, Date until) throws DBException;

	List<EnvironmentLog> allEnvironmentLogsByYears(String deviceUuid, Date from, Date until) throws DBException;
}
