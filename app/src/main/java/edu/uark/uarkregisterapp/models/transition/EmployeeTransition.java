package edu.uark.uarkregisterapp.models.transition;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
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

	private String recordID;
	public String getRecordID() {
		return this.recordID;
	}
	public EmployeeTransition setRecordID(String lookupCode) {
		this.recordID = lookupCode;
		return this;
	}

	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public EmployeeTransition setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeByteArray((new UUIDToByteConverterCommand()).setValueToConvert(this.id).execute());
		destination.writeString(this.recordID);
		destination.writeLong(this.createdOn.getTime());
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
		this.createdOn = new Date();
		this.recordID = StringUtils.EMPTY;
	}

	public EmployeeTransition(Employee employee) {
		this.id = employee.getId();
		this.createdOn = employee.getCreatedOn();
		this.recordID = employee.getRecordID();
	}

	private EmployeeTransition(Parcel employeeTransitionParcel) {
		this.id = (new ByteToUUIDConverterCommand()).setValueToConvert(employeeTransitionParcel.createByteArray()).execute();
		this.recordID = employeeTransitionParcel.readString();
		this.createdOn = new Date();
		this.createdOn.setTime(employeeTransitionParcel.readLong());
	}
}
