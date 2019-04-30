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
	}*/
	private double total_sales;
	public double getTotal_Sales(){return this.total_sales;}
	public EmployeeTransition setTotal_Sales(double total_sales){
		this.total_sales = total_sales;
		return this;
	}
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
	private double amount_of_money_made;
	public double getAmount_Of_Money_Made() {
		return this.amount_of_money_made;
	}
	public EmployeeTransition setAmount_Of_Money_Made(double amount_of_money_made) {
		this.amount_of_money_made = amount_of_money_made;
		return this;
	}

	private boolean employeeLoggedIn;
	public boolean getEmployeeLoggedIn() {
		return this.employeeLoggedIn;
	}
	public EmployeeTransition setEmployeeLoggedIn(boolean employeeLoggedIn) {
		this.employeeLoggedIn = employeeLoggedIn;
		return this;
	}

	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeByteArray((new UUIDToByteConverterCommand()).setValueToConvert(this.id).execute());
		destination.writeString(this.first_name);
		destination.writeString(this.last_name);
		destination.writeString(this.role);
		destination.writeBooleanArray(new boolean[]{this.active});
		destination.writeDouble(this.amount_of_money_made);
		destination.writeValue(this.employeeLoggedIn);
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
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		this.active = false;
		this.role = StringUtils.EMPTY;
		this.amount_of_money_made = 0.0;
		this.employeeLoggedIn = false;
	}
	public EmployeeTransition(PublicClientApplication application) {
		this.id = new UUID(0, 0);
		this.loginApp = application;
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		this.active = false;
		this.role = StringUtils.EMPTY;
		this.amount_of_money_made = 0.0;
	}

	public EmployeeTransition(IAccount userAccounts) {
		this.id = new UUID(0, 0);
		this.userAccounts = userAccounts;
		this.first_name = StringUtils.EMPTY;
		this.last_name = StringUtils.EMPTY;
		this.active = false;
		this.role = StringUtils.EMPTY;
		this.amount_of_money_made = 0.0;
	}

	public EmployeeTransition(Employee employee) {
		this.id = employee.getId();
		this.first_name = employee.getFirst_Name();
		this.last_name = employee.getLast_Name();
		this.active = employee.getActive();
		this.role = employee.getRole();
		this.amount_of_money_made = employee.getAmount_Of_Money_Made();
		this.employeeLoggedIn = employee.getEmployeeLoggedIn();
	}

	private EmployeeTransition(Parcel employeeTransitionParcel) {
		this.id = (new ByteToUUIDConverterCommand()).setValueToConvert(employeeTransitionParcel.createByteArray()).execute();
		this.first_name = employeeTransitionParcel.readString();
		this.last_name = employeeTransitionParcel.readString();
		this.role = employeeTransitionParcel.readString();
		this.amount_of_money_made = employeeTransitionParcel.readDouble();;
	}
}
