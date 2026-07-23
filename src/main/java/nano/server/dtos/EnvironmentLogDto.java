package nano.server.dtos;

import java.util.Date;

import nano.server.db.entities.EnvironmentLog;

public class EnvironmentLogDto {
	private DeviceDto device;
	private Date created;
	private float temp;
	private float hum;

	public EnvironmentLogDto(EnvironmentLog environmentLog) {
		if (environmentLog.getDevice() != null) {
			this.device = new DeviceDto(environmentLog.getDevice());
		}
		this.created = environmentLog.getCreated();
		this.temp = environmentLog.getTemp();
		this.hum = environmentLog.getHum();
	}

	public DeviceDto getDevice() {
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
}
