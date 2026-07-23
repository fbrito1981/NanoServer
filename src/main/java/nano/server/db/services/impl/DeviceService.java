package nano.server.db.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import nano.server.db.access.DBException;
import nano.server.db.access.DBScript;
import nano.server.db.daos.DeviceDao;
import nano.server.db.entities.Device;
import nano.server.db.services.def.IDeviceService;

@Service
public class DeviceService implements IDeviceService {
	@Override
	public List<Device> allDevices(String search, String order, int limit, int offset) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		return dao.allEntities(search, order, limit, offset);
	}
	
	@Override
	public int countDevices(String search) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		return dao.countEntities(search);
	}
	
	@Override
	public Device getDevice(String uuid) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		return dao.getEntity(uuid);
	}
	
	@Override
	public Device getDeviceByUserEmailAndName(String userEmail, String name) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		DBScript script = dao.getGetByUserEmailAndNameScript(userEmail, name);
		
		return dao.getEntity(script);
	}

	@Override
	public void setDevice(Device device) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		dao.setEntity(device);
	}
	
	@Override
	public void delDevice(String uuid) throws DBException {
		DeviceDao dao = new DeviceDao();
		
		dao.delEntity(uuid);
	}
}
