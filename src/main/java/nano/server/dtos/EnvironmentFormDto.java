package nano.server.dtos;

import java.util.Date;

import nano.server.enums.EnvironmentViewType;

public class EnvironmentFormDto {
	private String timeZone;
	private String device;
	private String viewType;
	private Date from;
	private Date until;

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getUntil() {
		return until;
	}

	public void setUntil(Date until) {
		this.until = until;
	}

	public EnvironmentViewType getEnvironmentViewType() {
		return EnvironmentViewType.fromValue(viewType);
	}
}
