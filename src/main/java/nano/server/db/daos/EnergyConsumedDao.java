package nano.server.db.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnergyConsumed;
import nano.server.db.services.def.IDeviceService;
import nano.server.utils.DbUtils;

public class EnergyConsumedDao extends BaseDao<EnergyConsumed> {
	private static final int MINUTES_BETWEEN_REEDS = 5;
	
	private IDeviceService deviceService;
	
	public EnergyConsumedDao(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}
	
	@Override
	public DBScript getAllScript(String search, String order, int limit, int offset) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public DBScript getCountScript(String search) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public DBScript getGetScript(String deviceUuid) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public DBScript getSetScript(EnergyConsumed entity) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public DBScript getDelScript(String key) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public EnergyConsumed getNewEntity(ResultSet resultSet) throws DBException {
		try {
			Device device = deviceService.getDevice(resultSet.getString("deviceUuid"));
			
			EnergyConsumed energyConsumed = new EnergyConsumed(device,
					resultSet.getTimestamp("created"),
					resultSet.getFloat("energy"));
			
			return energyConsumed;
		} catch (SQLException e) {
			throw new DBException(Device.class, e);
		}
	}
	
	public DBScript getAllByMinutesScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByMinute"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));
		script.addParam(new DBParam("mMinBetReed", MINUTES_BETWEEN_REEDS, false));
		
		return script;
	}

	public DBScript getAllByHoursScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByHour"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));
		script.addParam(new DBParam("mMinBetReed", MINUTES_BETWEEN_REEDS, false));
		
		return script;
	}

	public DBScript getAllByDaysScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByDay"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));
		script.addParam(new DBParam("mMinBetReed", MINUTES_BETWEEN_REEDS, false));
		
		return script;
	}

	public DBScript getAllByMonthsScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByMonth"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));
		script.addParam(new DBParam("mMinBetReed", MINUTES_BETWEEN_REEDS, false));
		
		return script;
	}

	public DBScript getAllByYearsScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByYear"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));
		script.addParam(new DBParam("mMinBetReed", MINUTES_BETWEEN_REEDS, false));
		
		return script;
	}
}
