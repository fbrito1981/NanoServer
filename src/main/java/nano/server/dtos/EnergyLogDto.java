package nano.server.dtos;

import java.util.Date;

import nano.server.db.entities.EnergyLog;

public class EnergyLogDto {
	private DeviceDto device;
	private Date created;
	private float volts;
	private float amps;
	private float frequency;
	private int difference;
	private double cosPhi;
	private double apparentPower;
	private double activePower;
	private double reactivePower;

	public EnergyLogDto(EnergyLog energyLog) {
		if (energyLog.getDevice() != null) {
			this.device = new DeviceDto(energyLog.getDevice());
		}
		this.created = energyLog.getCreated();
		this.volts = energyLog.getVolts();
		this.amps = energyLog.getAmps();
		this.frequency = energyLog.getFrequency();
		this.difference = energyLog.getDifference();

		double phiRad = getPhiRad();
		this.cosPhi = Math.cos(phiRad);

		this.apparentPower = volts * amps;
		this.activePower = this.apparentPower * Math.cos(phiRad);
		this.reactivePower = this.apparentPower * Math.sin(phiRad); 
	}
	
	public DeviceDto getDevice() {
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
	
	public double getCosPhi() {
		return cosPhi;
	}
	
	public double getApparentPower() {
		return apparentPower;
	}
	
	public double getActivePower() {
		return activePower;
	}
	
	public double getReactivePower() {
		return this.reactivePower;
	}
	
	private double getPhiRad() {
		double periodSeconds = 1.0 / frequency;
		double timeDiffSeconds = difference / 1_000_000.0;
		return 2 * Math.PI * (timeDiffSeconds / periodSeconds);
	}
}
