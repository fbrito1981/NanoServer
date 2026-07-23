package nano.server.db.entities;

import java.util.Date;

import nano.server.dtos.EnergyDto;
import nano.server.utils.KeyUtils;

public class EnergyLog extends KeyEntity {
	private Device device;
	private Date created;
	private float volts;
	private float amps;
	private float frequency;
	private int difference;
	
	public EnergyLog(Device device, float volts, float amps, float frequency, int difference) {
		this.device = device;
		this.created = new Date();
		this.volts = volts;
		this.amps = amps;
		this.frequency = frequency;
		this.difference = difference;
	}
	
	public EnergyLog(Device device, EnergyDto energyDto) {
		this(
			device,
			energyDto.getVolts(),
			energyDto.getAmps(),
			energyDto.getFreq(),
			energyDto.getDiff()
		);
	}
	
	public EnergyLog(Device device, Date created, float volts, float amps,
			float frequency, int difference) {
		this.device = device;
		this.created = created;
		this.volts = volts;
		this.amps = amps;
		this.frequency = frequency;
		this.difference = difference;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public float getVolts() {
		return volts;
	}
	
	public float getAmps() {
		return amps;
	}
	
	public float getFrequency() {
		return frequency;
	}
	
	public int getDifference() {
		return difference;
	}
	
	@Override
	public String getKey() {
		return KeyUtils.group(new String[] { device.getUuid(), new Long(created.getTime()).toString() });
	}
	
	public static String getDeviceUuidFromKey(String key) {
		return KeyUtils.pick(key, 0);
	}
	
	public static Date getCreatedFromKey(String key) throws NumberFormatException {
		return new Date(new Long(KeyUtils.pick(key, 1)));
	}
}
