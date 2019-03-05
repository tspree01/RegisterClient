package edu.uark.uarkregisterapp.models.api.fields;

import edu.uark.uarkregisterapp.models.api.interfaces.FieldNameInterface;

public enum EmployeeFieldName implements FieldNameInterface {
	EmployeeID("EmployeeID"),
	RecordID("recordID"),
	First_Name("first_name"),
	Last_Name("last_name"),
	Active("active"),
	Role("role"),
	Manager("manager"),
	Password("password"),
	API_REQUEST_STATUS("apiRequestStatus"),
	API_REQUEST_MESSAGE("apiRequestMessage"),
	CREATED_ON("createdOn");

	private String fieldName;
	public String getFieldName() {
		return this.fieldName;
	}

	EmployeeFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
