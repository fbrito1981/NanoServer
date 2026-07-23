package nano.server.enums;

public enum EnergyViewType {
	BY_MINUTE("byMinute"),
	BY_HOUR("byHour"),
	BY_DAY("byDay"),
	BY_MONTH("byMonth"),
	BY_YEAR("byYear");
	
	private String value;
	
	EnergyViewType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLabel() {
		switch (value) {
		case "byMinute":
			return "energy_by_minute_label";
		case "byHour":
			return "energy_by_hour_label";
		case "byDay":
			return "energy_by_day_label";
		case "byMonth":
			return "energy_by_month_label";
		case "byYear":
			return "energy_by_year_label";
		default:
			return "energy_by_minute_label";
		}
	}
	
	public static EnergyViewType fromValue(String value) {
		switch (value) {
		case "byMinute":
			return EnergyViewType.BY_MINUTE;
		case "byHour":
			return EnergyViewType.BY_HOUR;
		case "byDay":
			return EnergyViewType.BY_DAY;
		case "byMonth":
			return EnergyViewType.BY_MONTH;
		case "byYear":
			return EnergyViewType.BY_YEAR;
		default:
			return EnergyViewType.BY_MINUTE;
		}
	}
}
