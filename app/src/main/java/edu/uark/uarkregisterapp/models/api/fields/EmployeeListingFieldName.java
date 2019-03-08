package edu.uark.uarkregisterapp.models.api.fields;

import edu.uark.uarkregisterapp.models.api.interfaces.FieldNameInterface;

public enum EmployeeListingFieldName implements FieldNameInterface {
	EMPLOYEE("employee");
	private String fieldName;
	public String getFieldName() {
		return this.fieldName;
	}

	EmployeeListingFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
