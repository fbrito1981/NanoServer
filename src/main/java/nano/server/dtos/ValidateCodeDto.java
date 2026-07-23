package nano.server.dtos;

import java.util.Locale;

public class ValidateCodeDto {
	private String email;
	private int code;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getCode() {
		return code;
	}
	
	public void setCode(int code) {
		this.code = code;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "{ email: '%s', code: %d }", email, code);
	}
}
