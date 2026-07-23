package nano.server.dtos;

import javax.servlet.http.HttpServletRequest;

import nano.server.enums.EnergyViewType;
import nano.server.utils.LocalizerUtils;

public class EnergyViewTypeDto {
	private String value;
	private String label;
	
	public EnergyViewTypeDto(EnergyViewType energyViewType, HttpServletRequest request) {
		this.value = energyViewType.getValue();
		this.label = LocalizerUtils.getLocalizedText(request, energyViewType.getLabel());
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass().isInstance(this)) {
			EnergyViewTypeDto energyViewType = (EnergyViewTypeDto) obj;
			return energyViewType.getValue().equals(value);
		}
		return false;
	}
}
