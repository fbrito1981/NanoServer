package nano.server.dtos;

import java.util.Locale;

public class TokenDto {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "{ token: '%s' }", token);
	}
}
