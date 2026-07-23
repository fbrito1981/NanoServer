package nano.server.dtos;

import java.util.ArrayList;
import java.util.List;

import nano.server.db.entities.Device;

public class DeviceListDto {
	private List<DeviceDto> devices;

	public DeviceListDto(List<Device> devices) {
		this.devices = new ArrayList<>();

		if (devices != null) {
			for (Device device : devices) {
				this.devices.add(new DeviceDto(device));
			}
		}
	}

	public List<DeviceDto> getDevices() {
		return devices;
	}
}
