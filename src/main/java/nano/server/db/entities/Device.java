package nano.server.db.entities;

import java.util.Date;

public class Device {
	private String uuid;
	private String userEmail;
	private String name;
	private String model;
	private String os;
	private String version;
	private Boolean active;
	private Date created;
	private Date updated;
	
	public Device() {
	}
	
	public Device(String uuid, String userEmail, String name, String model, String os, String version) {
		this.uuid = uuid;
		this.userEmail = userEmail;
		this.name = name;
		this.model = model;
		this.os = os;
		this.version = version;
		this.active = true;
	}
	
	public Device(String uuid, String userEmail, String name, String model, String os, String version, Boolean active, Date created, Date updated) {
		this.uuid = uuid;
		this.userEmail = userEmail;
		this.name = name;
		this.model = model;
		this.os = os;
		this.version = version;
		this.active = active;
		this.created = created;
		this.updated = updated;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public Date getUpdated() {
		return updated;
	}

	public String getType() {
		if (uuid == null || uuid.length() < 4) {
			return null;
		}
		switch (uuid.substring(0, 4)) {
		case "WFEM": return "energy";
		case "WFTM": return "environment";
		default: return null;
		}
	}
}