package nano.server.db.entities;

import java.util.Date;
import java.util.Locale;

public class User {
	private String email;
	private String pass;
	private String name;
	private boolean active;
	private Integer resetCode;
	private Role role;
	private String token;
	private String picture;
	private Date created;
	private Date updated;
	
	public User() {
	}
	
	public User(String email, String pass, String name, Role role, String picture) {
		this.email = email;
		this.pass = pass;
		this.name = name;
		this.role = role;
		this.picture = picture;
	}
	
	public User(String email, String pass, String name, boolean active, Integer resetCode, Role role,
			String token, String picture, Date created, Date updated) {
		this.email = email;
		this.pass = pass;
		this.name = name;
		this.active = active;
		this.resetCode = resetCode;
		this.role = role;
		this.token = token;
		this.picture = picture;
		this.created = created;
		this.updated = updated;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPass() {
		return pass;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Integer getResetCode() {
		return resetCode;
	}
	
	public void setResetCode(Integer resetCode) {
		this.resetCode = resetCode;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Date getCreated() {
		return created;
	}
	
	public Date getUpdated() {
		return updated;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%s (%s)", name, email);
	}
}
