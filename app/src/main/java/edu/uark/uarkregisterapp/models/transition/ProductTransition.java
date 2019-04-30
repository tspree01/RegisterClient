package edu.uark.uarkregisterapp.models.transition;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.UUID;

import edu.uark.uarkregisterapp.commands.converters.ByteToUUIDConverterCommand;
import edu.uark.uarkregisterapp.commands.converters.UUIDToByteConverterCommand;
import edu.uark.uarkregisterapp.models.api.CartProduct;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductTransition implements Parcelable {
	private UUID id;
	public UUID getId() {
		return this.id;
	}
	public ProductTransition setId(UUID id) {
		this.id = id;
		return this;
	}

	private String lookupCode;
	public String getLookupCode() {
		return this.lookupCode;
	}
	public ProductTransition setLookupCode(String lookupCode) {
		this.lookupCode = lookupCode;
		return this;
	}

	private int count;
	public int getCount() {
		return this.count;
	}
	public ProductTransition setCount(int count) {
		this.count = count;
		return this;
	}

	private int quantity_sold;
	public int getQuantity_sold() { return quantity_sold; }
	public ProductTransition setQuantity_sold(int quantity_sold) {
		this.quantity_sold = quantity_sold;
		return this;
	}

	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public ProductTransition setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	private double price;
	public double getPrice() {
		return this.price;
	}
	public ProductTransition setPrice(double price) {
		this.price = price;
		return this;
	}


	@Override
	public void writeToParcel(Parcel destination, int flags) {
		destination.writeByteArray((new UUIDToByteConverterCommand()).setValueToConvert(this.id).execute());
		destination.writeString(this.lookupCode);
		destination.writeInt(this.count);
		destination.writeInt(this.quantity_sold);
		destination.writeLong(this.createdOn.getTime());
		destination.writeDouble(this.price);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ProductTransition> CREATOR = new Parcelable.Creator<ProductTransition>() {
		public ProductTransition createFromParcel(Parcel productTransitionParcel) {
			return new ProductTransition(productTransitionParcel);
		}

		public ProductTransition[] newArray(int size) {
			return new ProductTransition[size];
		}
	};

	public ProductTransition() {
		this.count = -1;
		this.quantity_sold = 0;
		this.id = new UUID(0, 0);
		this.createdOn = new Date();
		this.lookupCode = StringUtils.EMPTY;
		this.price = 0.0;
	}

	public ProductTransition(Product product) {
		this.id = product.getId();
		this.count = product.getCount();
		this.createdOn = product.getCreatedOn();
		this.lookupCode = product.getLookupCode();
		this.price = product.getPrice();
	}

	private ProductTransition(Parcel productTransitionParcel) {
		this.id = (new ByteToUUIDConverterCommand()).setValueToConvert(productTransitionParcel.createByteArray()).execute();
		this.lookupCode = productTransitionParcel.readString();
		this.count = productTransitionParcel.readInt();
		this.quantity_sold = productTransitionParcel.readInt();

		this.createdOn = new Date();
		this.createdOn.setTime(productTransitionParcel.readLong());
		this.price = productTransitionParcel.readDouble();
	}
}
