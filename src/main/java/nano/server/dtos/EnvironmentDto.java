package nano.server.dtos;

public class EnvironmentDto {
	private String id;
	private float temp;
	private float hum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}

	public float getHum() {
		return hum;
	}

	public void setHum(float hum) {
		this.hum = hum;
	}
}
