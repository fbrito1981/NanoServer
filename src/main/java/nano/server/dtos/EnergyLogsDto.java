package nano.server.dtos;

import java.util.ArrayList;
import java.util.List;

import nano.server.db.entities.EnergyLog;

public class EnergyLogsDto {
	private List<EnergyLogDto> logs;
	
	public EnergyLogsDto(List<EnergyLog> energyLogs) {
		logs = new ArrayList<>();
		
		if (energyLogs != null && !energyLogs.isEmpty()) {
			for (EnergyLog energyLog : energyLogs) {
				EnergyLogDto log = new EnergyLogDto(energyLog);
				logs.add(log);
			}
		}
	}
	
	public List<EnergyLogDto> getLogs() {
		return logs;
	}
}
