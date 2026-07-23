package nano.server.dtos;

import java.util.Date;

import nano.server.db.entities.EnergyConsumed;

public class EnergyConsumedDto {
	private DeviceDto device;
	private Date created;
	private float energy;
	
	public EnergyConsumedDto(EnergyConsumed energyConsumed) {
		if (energyConsumed.getDevice() != null) {
			this.device = new DeviceDto(energyConsumed.getDevice());
		}
		this.created = energyConsumed.getCreated();
		this.energy = energyConsumed.getEnergy();
	}
	
	public DeviceDto getDevice() {
		return device;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public float getEnergy() {
		return energy;
	}
}
