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

/*	private int recordID;
	public int getRecordID() {
		return this.recordID;
	}
	public Employee setRecordID(int RecordID) {
		this.recordID = RecordID;
		return this;
	}*/

/*	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public Employee setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}*/
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
/*	private String password;
	public String getPassword(){return this.password;}
	public Employee setPassword(String password){
		this.password = password;
		return this;
	}*/
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
		//this.password = rawJsonObject.optString(EmployeeFieldName.Password.getFieldName());
		//this.recordID = rawJsonObject.optInt(EmployeeFieldName.RecordID.getFieldName());
		this.active = rawJsonObject.optBoolean(EmployeeFieldName.Role.getFieldName());
		value = rawJsonObject.optString(EmployeeFieldName.CREATED_ON.getFieldName());
/*		if (!StringUtils.isBlank(value)) {
			try {
				this.createdOn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}*/

		return this;
	}

	@Override
	public JSONObject convertToJson() {
		JSONObject jsonObject = new JSONObject();

		try {
			//jsonObject.put(EmployeeFieldName.RecordID.getFieldName(), this.recordID);
			jsonObject.put(EmployeeFieldName.First_Name.getFieldName(),this.first_name);
			jsonObject.put(EmployeeFieldName.Last_Name.getFieldName(),this.last_name);
			jsonObject.put(EmployeeFieldName.EmployeeID.getFieldName(), this.id.toString());
			jsonObject.put(EmployeeFieldName.Active.getFieldName(),this.active);
			jsonObject.put(EmployeeFieldName.Role.getFieldName(),this.role);
			jsonObject.put(EmployeeFieldName.Manager.getFieldName(), this.managerID.toString());
			//jsonObject.put(EmployeeFieldName.Password.getFieldName(),this.password);
			//jsonObject.put(EmployeeFieldName.CREATED_ON.getFieldName(), (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)).format(this.createdOn));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public Employee() {
		//this.recordID = 1;
		this.id= new UUID(0, 0);
		//this.createdOn = new Date();
		this.managerID = new UUID(0,0);
		//this.password = " ";
		this.first_name = " ";
		this.last_name = " ";
		this.active = false;
		this.role = " ";
	}

	public Employee(EmployeeTransition employeeTransition) {
		//this.id = employeeTransition.getId();
		//this.createdOn = employeeTransition.getCreatedOn();
	//	this.recordID = employeeTransition.getRecordID();
		this.managerID = employeeTransition.getManagerID();
	//	this.password = employeeTransition.getPassword();
		this.first_name = employeeTransition.getFirst_Name();
		this.last_name = employeeTransition.getLast_Name();
		this.active = employeeTransition.getActive();
		this.role = employeeTransition.getRole();
	}
}
