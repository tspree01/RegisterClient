package edu.uark.uarkregisterapp.models.transition;

import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import edu.uark.uarkregisterapp.commands.converters.ByteToUUIDConverterCommand;
import edu.uark.uarkregisterapp.commands.converters.UUIDToByteConverterCommand;
import edu.uark.uarkregisterapp.models.api.Employee;


public class EmployeeTransition implements Parcelable {
	private UUID id;
	public UUID getId() {
		return this.id;
	}
	public EmployeeTransition setId(UUID id) {
		this.id = id;
		return this;
	}

	private PublicClientApplication loginApp;

	public PublicClientApplication getLoginApp() {
		return loginApp;
	}
	public EmployeeTransition setLoginApp(PublicClientApplication loginApp) {
		this.loginApp = loginApp;
		return this;
	}
	private IAccount userAccounts;

	public IAccount getuserAccounts() {
		return userAccounts;
	}
	public EmployeeTransition setuserAccounts(IAccount userAccounts) {
		this.userAccounts = userAccounts;
		return this;
	}
	/*	private int recordID;
	public int getRecordID() {
		return this.recordID;
	}
	public EmployeeTransition setRecordID(int recordID) {
		this.recordID = recordID;
		return this;
	}*/

/*	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public EmployeeTransition setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}*/
	private String first_name;
	public String getFirst_Name() {
		return this.first_name;
	}
	public EmployeeTransition setFirst_Name(String first_name){
		this.first_name = first_name;
		return this;
	}
	private String last_name;
	public String getLast_Name(){return this.last_name;}
	public EmployeeTransition setLast_Name(String last_name){
		this.last_name = last_name;
		return this;
	}
	private boolean active;
	public boolean getActive(){return this.active;}
	public EmployeeTransition setActive(boolean active){
		this.active = active;
		return this;
	}
	public String role;
	public String getRole(){return this.role;}
	public EmployeeTransition setRole(String role){
		this.role = role;
		return this;
	}
	public UUID managerID;
	public UUID getManagerID(){return this.managerID;}
	public EmployeeTransition setManagerID(UUID managerID){
		this.managerID = managerID;
		return this;
	}
/*	public String password;
	public String getPassword() {return this.password;}
	public EmployeeTransition setPassword(String password){
		this.password = password;
		return this;
	}*/
	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeByteArray((new UUIDToByteConverterCommand()).setValueToConvert(this.id).execute());
		//destination.writeInt(this.recordID);
		//destination.writeLong(this.createdOn.getTime());
		//destination.writeValue(this.loginApp);
		//destination.writeValue(this.userAccounts);
		//destination.writeSerializable(this.userAccounts);
		destination.writeString(this.first_name);
		destination.writeString(this.last_name);
		destination.writeString(this.role);
		destination.writeBooleanArray(new boolean[]{this.active});
		destination.writeByteArray((new UUIDToByteConverterCommand()).setValueToConvert(this.managerID).execute());
		//destination.writeString(this.password);

	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<EmployeeTransition> CREATOR = new Creator<EmployeeTransition>() {
		public EmployeeTransition createFromParcel(Parcel employeeTransitionParcel) {
			return new EmployeeTransition(employeeTransitionParcel);
		}

		public EmployeeTransition[] newArray(int size) {
			return new EmployeeTransition[size];
		}
	};

	public EmployeeTransition() {
		this.id = new UUID(0, 0);
		//this.createdOn = new Date();
		//this.recordID = 0;
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		//this.password = StringUtils.EMPTY;
		this.managerID = new UUID(0,0);
		this.active = false;
		this.role = StringUtils.EMPTY;
	}
	public EmployeeTransition(PublicClientApplication application) {
		this.id = new UUID(0, 0);
		this.loginApp = application;
		//this.createdOn = new Date();
		//this.recordID = 0;
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		//this.password = StringUtils.EMPTY;
		this.managerID = new UUID(0,0);
		this.active = false;
		this.role = StringUtils.EMPTY;
	}

	public EmployeeTransition(IAccount userAccounts) {
		this.id = new UUID(0, 0);
		this.userAccounts = userAccounts;
		//this.createdOn = new Date();
		//this.recordID = 0;
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		//this.password = StringUtils.EMPTY;
		this.managerID = new UUID(0,0);
		this.active = false;
		this.role = StringUtils.EMPTY;
	}

	public EmployeeTransition(Employee employee) {
		this.id = employee.getId();
		//this.createdOn = employee.getCreatedOn();
		//this.recordID = employee.getRecordID();
		this.managerID = employee.getManagerID();
		this.first_name = employee.getFirst_Name();
		this.last_name = employee.getLast_Name();
		//this.password = employee.getPassword();
		this.active = employee.getActive();
		this.role = employee.getRole();
	}

	private EmployeeTransition(Parcel employeeTransitionParcel) {
		this.id = (new ByteToUUIDConverterCommand()).setValueToConvert(employeeTransitionParcel.createByteArray()).execute();
		this.first_name = employeeTransitionParcel.readString();
		this.last_name = employeeTransitionParcel.readString();
		this.role = employeeTransitionParcel.readString();
		//this.loginApp = (PublicClientApplication) employeeTransitionParcel.readValue(getClass().getClassLoader());
		//this.userAccounts = (IAccount)employeeTransitionParcel.readValue(getClass().getClassLoader());

		//this.recordID = employeeTransitionParcel.readInt();
		//this.createdOn = new Date();
		//this.createdOn.setTime(employeeTransitionParcel.readLong());
	}
}
