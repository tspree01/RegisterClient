package edu.uark.uarkregisterapp.models.api.enums;

import edu.uark.uarkregisterapp.models.api.interfaces.PathElementInterface;

public enum EmployeeApiMethod implements PathElementInterface {
	NONE(""),
	BY_RECORD_ID("recordID");

	@Override
	public String getPathValue() {
		return value;
	}

	private String value;

	EmployeeApiMethod(String value) {
		this.value = value;
	}
}
