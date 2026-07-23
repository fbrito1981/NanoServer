package nano.server.dtos;

import java.util.ArrayList;
import java.util.List;

import nano.server.db.entities.EnvironmentLog;

public class EnvironmentLogsDto {
	private List<EnvironmentLogDto> logs;

	public EnvironmentLogsDto(List<EnvironmentLog> environmentLogs) {
		logs = new ArrayList<>();

		if (environmentLogs != null && !environmentLogs.isEmpty()) {
			for (EnvironmentLog environmentLog : environmentLogs) {
				EnvironmentLogDto log = new EnvironmentLogDto(environmentLog);
				logs.add(log);
			}
		}
	}

	public List<EnvironmentLogDto> getLogs() {
		return logs;
	}
}
