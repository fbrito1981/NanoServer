package nano.server.db.entities;

import java.util.Date;

import nano.server.dtos.EnvironmentDto;
import nano.server.utils.KeyUtils;

public class EnvironmentLog extends KeyEntity {
	private Device device;
	private Date created;
	private float temp;
	private float hum;

	public EnvironmentLog(Device device, float temp, float hum) {
		this.device = device;
		this.created = new Date();
		this.temp = temp;
		this.hum = hum;
	}

	public EnvironmentLog(Device device, EnvironmentDto environmentDto) {
		this(device, environmentDto.getTemp(), environmentDto.getHum());
	}

	public EnvironmentLog(Device device, Date created, float temp, float hum) {
		this.device = device;
		this.created = created;
		this.temp = temp;
		this.hum = hum;
	}

	public Device getDevice() {
		return device;
	}

	public Date getCreated() {
		return created;
	}

	public float getTemp() {
		return temp;
	}

	public float getHum() {
		return hum;
	}

	@Override
	public String getKey() {
		return KeyUtils.group(new String[] { device.getUuid(), Long.toString(created.getTime()) });
	}

	public static String getDeviceUuidFromKey(String key) {
		return KeyUtils.pick(key, 0);
	}

	public static Date getCreatedFromKey(String key) throws NumberFormatException {
		return new Date(Long.parseLong(KeyUtils.pick(key, 1)));
	}
}
