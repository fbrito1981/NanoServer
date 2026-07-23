package nano.server.dtos;

import java.util.Date;

import nano.server.db.entities.Device;

public class DeviceDto {
	private Boolean active;
	private String activeLabel;
	private String uuid;
	private String name;
	private String model;
	private String os;
	private String version;
	private String type;
	private Date created;
	private Date updated;

	public DeviceDto() {
	}

	public DeviceDto(Device device) {
		this.active = device.isActive();
		this.uuid = device.getUuid();
		this.name = device.getName();
		this.model = device.getModel();
		this.os = device.getOs();
		this.version = device.getVersion();
		this.type = device.getType();
		this.created = device.getCreated();
		this.updated = device.getUpdated();
	}
	
	public Boolean getActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public String getActiveLabel() {
		return activeLabel;
	}
	
	public void setActiveLabel(String activeLabel) {
		this.activeLabel = activeLabel;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public String getOs() {
		return os;
	}
	
	public void setOs(String os) {
		this.os = os;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getUpdated() {
		return updated;
	}
	
	public void setUpdated(Date updated) {
		this.updated = updated;
	}
}
