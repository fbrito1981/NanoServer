package nano.server.db.entities;

import java.util.Date;

public class EnergyConsumed {
	private Device device;
	private Date created;
	private float energy;
	
	public EnergyConsumed(Device device, Date created, float energy) {
		this.device = device;
		this.created = created;
		this.energy = energy;
	}
	
	public Device getDevice() {
		return device;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public float getEnergy() {
		return energy;
	}
}
