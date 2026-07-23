package nano.server.db.entities;

import java.util.Date;

public class SecurityLog {
	private String nonce;
	private Date created;
	
	public SecurityLog() {
	}
	
	public SecurityLog(String nonce) {
		this.nonce = nonce;
	}
	
	public SecurityLog(String nonce, Date created) {
		this.nonce = nonce;
		this.created = created;
	}

	public String getNonce() {
		return nonce;
	}

	public Date getCreated() {
		return created;
	}
}
