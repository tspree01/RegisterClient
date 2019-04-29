package edu.uark.uarkregisterapp.models.api.fields;

import edu.uark.uarkregisterapp.models.api.interfaces.FieldNameInterface;

public enum EmployeeFieldName implements FieldNameInterface {
	EmployeeID("employee_id"),
	RecordID("record_id"),
	First_Name("first_name"),
	Last_Name("last_name"),
	Active("active"),
	Role("role"),
	Manager("manager"),
	Password("password"),
	API_REQUEST_STATUS("apiRequestStatus"),
	API_REQUEST_MESSAGE("apiRequestMessage"),
	CREATED_ON("created_on"),
	total_gain("amount_of_money_made");

	private String fieldName;
	public String getFieldName() {
		return this.fieldName;
	}

	EmployeeFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
