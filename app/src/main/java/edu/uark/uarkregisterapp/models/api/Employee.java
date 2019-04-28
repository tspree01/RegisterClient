package edu.uark.uarkregisterapp.models.api;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.fields.EmployeeFieldName;
import edu.uark.uarkregisterapp.models.api.interfaces.ConvertToJsonInterface;
import edu.uark.uarkregisterapp.models.api.interfaces.LoadFromJsonInterface;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class Employee implements ConvertToJsonInterface, LoadFromJsonInterface<Employee> {
	private UUID id;
	public UUID getId() {
		return this.id;
	}
	public Employee setId(UUID id) {
		this.id = id;
		return this;
	}
	private String first_name;
	public String getFirst_Name(){return this.first_name;}
	public Employee setFirst_Name(String first_name){
		this.first_name = first_name;
		return this;
	}
	private String last_name;
	public String getLast_Name(){return this.last_name;}
	public Employee setLast_Name(String last_name){
		this.last_name = last_name;
		return this;
	}
	private boolean active;
	public boolean getActive(){return this.active;}
	public Employee setActive(Boolean active){
		this.active = active;
		return this;
	}
	private String role;
	public String getRole(){return this.role;}
	public Employee setRole(String role){
		this.role = role;
		return this;
	}
	private UUID managerID;
	public UUID getManagerID(){return this.managerID;}
	public Employee setManagerID(UUID managerID){
		this.managerID = managerID;
		return this;
	}
	private double amount_of_money_made;
	public double getAmount_Of_Money_Made() {
		return this.amount_of_money_made;
	}
	public Employee setAmount_Of_Money_Made(double amount_of_money_made) {
		this.amount_of_money_made = amount_of_money_made;
		return this;
	}

	private boolean employeeLoggedIn;
	public boolean getEmployeeLoggedIn() {
		return this.employeeLoggedIn;
	}
	public Employee setEmployeeLoggedIn(boolean employeeLoggedIn) {
		this.employeeLoggedIn = employeeLoggedIn;
		return this;
	}



	@Override
	public Employee loadFromJson(JSONObject rawJsonObject) {
		String value = rawJsonObject.optString(EmployeeFieldName.EmployeeID.getFieldName());
		if (!StringUtils.isBlank(value)) {
			this.id = UUID.fromString(value);
		}
		value = rawJsonObject.optString(EmployeeFieldName.Manager.getFieldName());
		if (!StringUtils.isBlank(value)) {
			this.managerID = UUID.fromString(value);
		}
		this.role = rawJsonObject.optString(EmployeeFieldName.Role.getFieldName());
		this.first_name = rawJsonObject.optString(EmployeeFieldName.First_Name.getFieldName());
		this.last_name = rawJsonObject.optString(EmployeeFieldName.Last_Name.getFieldName());
		this.active = rawJsonObject.optBoolean(EmployeeFieldName.Role.getFieldName());
		this.amount_of_money_made = rawJsonObject.optDouble(EmployeeFieldName.Amount_Of_Money_Made.getFieldName());
		this.employeeLoggedIn = rawJsonObject.optBoolean(EmployeeFieldName.EmployeeLoggedIn.getFieldName());

		return this;
	}

	@Override
	public JSONObject convertToJson() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(EmployeeFieldName.First_Name.getFieldName(),this.first_name);
			jsonObject.put(EmployeeFieldName.Last_Name.getFieldName(),this.last_name);
			jsonObject.put(EmployeeFieldName.EmployeeID.getFieldName(), this.id.toString());
			jsonObject.put(EmployeeFieldName.Active.getFieldName(),this.active);
			jsonObject.put(EmployeeFieldName.Role.getFieldName(),this.role);
			jsonObject.put(EmployeeFieldName.Manager.getFieldName(), this.managerID.toString());
			jsonObject.put(EmployeeFieldName.Amount_Of_Money_Made.getFieldName(), this.amount_of_money_made);
			jsonObject.put(EmployeeFieldName.EmployeeLoggedIn.getFieldName(), this.employeeLoggedIn);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public Employee() {
		this.id= new UUID(0, 0);
		this.managerID = new UUID(0,0);
		this.first_name = " ";
		this.last_name = " ";
		this.active = false;
		this.role = " ";
		this.amount_of_money_made = 0.0;
		this.employeeLoggedIn = false;
	}

	public Employee(EmployeeTransition employeeTransition) {
		this.managerID = employeeTransition.getManagerID();
		this.first_name = employeeTransition.getFirst_Name();
		this.last_name = employeeTransition.getLast_Name();
		this.active = employeeTransition.getActive();
		this.role = employeeTransition.getRole();
		this.amount_of_money_made = employeeTransition.getAmount_Of_Money_Made();
		this.employeeLoggedIn = employeeTransition.getEmployeeLoggedIn();
	}
}
