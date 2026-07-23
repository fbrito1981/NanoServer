package nano.server.dtos;

import java.util.Locale;

public class LoginDto {
	private String email;
	private String pass;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "{ email: '%s', pass: '%s' }", email, pass);
	}
}
