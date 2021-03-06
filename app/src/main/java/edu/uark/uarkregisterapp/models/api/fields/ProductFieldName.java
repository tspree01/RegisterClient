package edu.uark.uarkregisterapp.models.api.fields;

import edu.uark.uarkregisterapp.models.api.interfaces.FieldNameInterface;

public enum ProductFieldName implements FieldNameInterface {
	ID("id"),
	LOOKUP_CODE("lookupCode"),
	COUNT("count"),
	QUANTITY_SOLD("quantity_sold"),
	API_REQUEST_STATUS("apiRequestStatus"),
	API_REQUEST_MESSAGE("apiRequestMessage"),
	CREATED_ON("createdOn"),
	PRICE("price"),
	CARTID("cartid"),
	TOTAL_SOLD("total_sold");

	private String fieldName;
	public String getFieldName() {
		return this.fieldName;
	}

	ProductFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
