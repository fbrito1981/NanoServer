package nano.server.dtos;

import java.util.Date;

public class UserDto {
	private Boolean active;
	private String activeLabel;
	private String email;
	private String name;
	private String roleName;
	private String picture;
	private Boolean pictureRemoved;
	private Date created;
	private Date updated;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Boolean getPictureRemoved() {
		return pictureRemoved;
	}

	public void setPictureRemoved(Boolean pictureRemoved) {
		this.pictureRemoved = pictureRemoved;
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
