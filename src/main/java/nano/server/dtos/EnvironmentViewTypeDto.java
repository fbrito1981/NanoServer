package nano.server.dtos;

import javax.servlet.http.HttpServletRequest;

import nano.server.enums.EnvironmentViewType;
import nano.server.utils.LocalizerUtils;

public class EnvironmentViewTypeDto {
	private String value;
	private String label;

	public EnvironmentViewTypeDto(EnvironmentViewType environmentViewType, HttpServletRequest request) {
		this.value = environmentViewType.getValue();
		this.label = LocalizerUtils.getLocalizedText(request, environmentViewType.getLabel());
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
			EnvironmentViewTypeDto environmentViewType = (EnvironmentViewTypeDto) obj;
			return environmentViewType.getValue().equals(value);
		}
		return false;
	}
}
