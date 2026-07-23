package nano.server.dtos;

import java.util.ArrayList;
import java.util.List;

import nano.server.db.entities.EnergyConsumed;

public class EnergyConsumedListDto {
	private List<EnergyConsumedDto> energyLogs;
	
	public EnergyConsumedListDto(List<EnergyConsumed> logs) {
		energyLogs = new ArrayList<>();
		
		if (logs != null && !logs.isEmpty()) {
			for (EnergyConsumed energyLog : logs) {
				EnergyConsumedDto log = new EnergyConsumedDto(energyLog);
				energyLogs.add(log);
			}
		}
	}
	
	public List<EnergyConsumedDto> getEnergyLogs() {
		return energyLogs;
	}
}
