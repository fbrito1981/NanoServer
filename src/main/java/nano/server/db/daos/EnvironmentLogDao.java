package nano.server.db.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import nano.server.db.access.DBException;
import nano.server.db.access.DBParam;
import nano.server.db.access.DBScript;
import nano.server.db.access.DBScriptType;
import nano.server.db.entities.Device;
import nano.server.db.entities.EnvironmentLog;
import nano.server.db.services.def.IDeviceService;
import nano.server.utils.DbUtils;

public class EnvironmentLogDao extends BaseDao<EnvironmentLog> {
	private IDeviceService deviceService;

	public EnvironmentLogDao(IDeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@Override
	public DBScript getAllScript(String search, String order, int limit, int offset) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModel());
		script.addParam(new DBParam("mSearch", search, true));
		script.addParam(new DBParam("mOrder", order, true));
		script.addParam(new DBParam("mLimit", limit, false));
		script.addParam(new DBParam("mOffset", offset, false));

		return script;
	}

	@Override
	public DBScript getCountScript(String search) {
		DBScript script = new DBScript(DBScriptType.COUNT, getEntityModel());
		script.addParam(new DBParam("mSearch", search, true));

		return script;
	}

	@Override
	public DBScript getGetScript(String deviceUuid) {
		DBScript script = new DBScript(DBScriptType.GET, getEntityModel());
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));

		return script;
	}

	@Override
	public DBScript getSetScript(EnvironmentLog entity) {
		DBScript script = new DBScript(DBScriptType.SET, getEntityModel());
		script.addParam(new DBParam("mDeviceUuid", entity.getDevice().getUuid(), true));
		script.addParam(new DBParam("mCreated", DbUtils.getUnixTimestamp(entity.getCreated()), false));
		script.addParam(new DBParam("mTemp", entity.getTemp(), false));
		script.addParam(new DBParam("mHum", entity.getHum(), false));

		return script;
	}

	@Override
	public DBScript getDelScript(String key) {
		throw new UnsupportedOperationException("This method is not implemented.");
	}

	@Override
	public EnvironmentLog getNewEntity(ResultSet resultSet) throws DBException {
		try {
			Device device = deviceService.getDevice(resultSet.getString("deviceUuid"));

			EnvironmentLog environmentLog = new EnvironmentLog(device,
					resultSet.getTimestamp("created"),
					resultSet.getFloat("temp"),
					resultSet.getFloat("hum"));

			return environmentLog;
		} catch (SQLException e) {
			throw new DBException(Device.class, e);
		}
	}

	public DBScript getAllByMinutesScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByMinute"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));

		return script;
	}

	public DBScript getAllByHoursScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByHour"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));

		return script;
	}

	public DBScript getAllByDaysScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByDay"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));

		return script;
	}

	public DBScript getAllByMonthsScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByMonth"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));

		return script;
	}

	public DBScript getAllByYearsScript(String deviceUuid, Date from, Date until) {
		DBScript script = new DBScript(DBScriptType.ALL, getEntityModelWithSuffix("ByYear"));
		script.addParam(new DBParam("mDeviceUuid", deviceUuid, true));
		script.addParam(new DBParam("mFrom", DbUtils.getUnixTimestamp(from), false));
		script.addParam(new DBParam("mUntil", DbUtils.getUnixTimestamp(until), false));

		return script;
	}
}
