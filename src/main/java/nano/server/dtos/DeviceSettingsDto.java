package nano.server.dtos;

public class DeviceSettingsDto {
	private String ssid;
	private Double volts;
	private Double amps;
	private Double temp;
	private Double hum;

	public DeviceSettingsDto() {
	}

	public DeviceSettingsDto(String ssid, Double volts, Double amps, Double temp, Double hum) {
		this.ssid = ssid;
		this.volts = volts;
		this.amps = amps;
		this.temp = temp;
		this.hum = hum;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public Double getVolts() {
		return volts;
	}

	public void setVolts(Double volts) {
		this.volts = volts;
	}

	public Double getAmps() {
		return amps;
	}

	public void setAmps(Double amps) {
		this.amps = amps;
	}

	public Double getTemp() {
		return temp;
	}

	public void setTemp(Double temp) {
		this.temp = temp;
	}

	public Double getHum() {
		return hum;
	}

	public void setHum(Double hum) {
		this.hum = hum;
	}
}
