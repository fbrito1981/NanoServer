package nano.server.enums;

public enum EnvironmentViewType {
	BY_MINUTE("byMinute"),
	BY_HOUR("byHour"),
	BY_DAY("byDay"),
	BY_MONTH("byMonth"),
	BY_YEAR("byYear");

	private String value;

	EnvironmentViewType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getLabel() {
		switch (value) {
		case "byMinute":
			return "environment_by_minute_label";
		case "byHour":
			return "environment_by_hour_label";
		case "byDay":
			return "environment_by_day_label";
		case "byMonth":
			return "environment_by_month_label";
		case "byYear":
			return "environment_by_year_label";
		default:
			return "environment_by_minute_label";
		}
	}

	public static EnvironmentViewType fromValue(String value) {
		switch (value) {
		case "byMinute":
			return EnvironmentViewType.BY_MINUTE;
		case "byHour":
			return EnvironmentViewType.BY_HOUR;
		case "byDay":
			return EnvironmentViewType.BY_DAY;
		case "byMonth":
			return EnvironmentViewType.BY_MONTH;
		case "byYear":
			return EnvironmentViewType.BY_YEAR;
		default:
			return EnvironmentViewType.BY_MINUTE;
		}
	}
}
