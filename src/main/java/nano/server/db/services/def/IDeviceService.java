package nano.server.db.services.def;

import java.util.List;

import nano.server.db.access.DBException;
import nano.server.db.entities.Device;

public interface IDeviceService {
	List<Device> allDevices(String search, String order, int limit, int offset) throws DBException;
	
	int countDevices(String search) throws DBException;
	
	Device getDevice(String uuid) throws DBException;
	
	Device getDeviceByUserEmailAndName(String userEmail, String name) throws DBException;
	
	void setDevice(Device device) throws DBException;
	
	void delDevice(String uuid) throws DBException;
}
