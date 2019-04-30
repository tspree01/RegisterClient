package edu.uark.uarkregisterapp.models.api;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.fields.ProductFieldName;
import edu.uark.uarkregisterapp.models.api.interfaces.ConvertToJsonInterface;
import edu.uark.uarkregisterapp.models.api.interfaces.LoadFromJsonInterface;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class Product implements ConvertToJsonInterface, LoadFromJsonInterface<Product> {
	private UUID id;
	public UUID getId() {
		return this.id;
	}
	public Product setId(UUID id) {
		this.id = id;
		return this;
	}

	private String lookupCode;
	public String getLookupCode() {
		return this.lookupCode;
	}
	public Product setLookupCode(String lookupCode) {
		this.lookupCode = lookupCode;
		return this;
	}
	private int quantity_sold;
	public int getQuantity_sold() { return quantity_sold; }
	public Product setQuantity_sold(int quantity_sold) {
		this.quantity_sold = quantity_sold;
		return this;
	}

	private int count;
	public int getCount() {
		return count;
	}
	public Product setCount(int count) {
		this.count = count;
		return this;
	}

	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public Product setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	private double price;
	public double getPrice() {
		return this.price;
	}
	public Product setPrice(double price) {
		this.price = price;
		return this;
	}

	private UUID cartid;
	public UUID getCartId() {
		return this.cartid;
	}
	public Product setCartId(UUID cartid) {
		this.cartid = cartid;
		return this;
	}


	@Override
	public Product loadFromJson(JSONObject rawJsonObject) {
		String value = rawJsonObject.optString(ProductFieldName.ID.getFieldName());
		if (!StringUtils.isBlank(value)) {
			this.id = UUID.fromString(value);
		}

		this.lookupCode = rawJsonObject.optString(ProductFieldName.LOOKUP_CODE.getFieldName());
		this.quantity_sold = rawJsonObject.optInt(ProductFieldName.QUANTITY_SOLD.getFieldName());
		this.count = rawJsonObject.optInt(ProductFieldName.COUNT.getFieldName());

		value = rawJsonObject.optString(ProductFieldName.CREATED_ON.getFieldName());
		if (!StringUtils.isBlank(value)) {
			try {
				this.createdOn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).parse(value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		this.price = rawJsonObject.optDouble(ProductFieldName.PRICE.getFieldName());

		String values = rawJsonObject.optString(ProductFieldName.CARTID.getFieldName());
		if (!StringUtils.isBlank(values)) {
			this.cartid = UUID.fromString(values);
		}

		return this;
	}

	@Override
	public JSONObject convertToJson() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(ProductFieldName.ID.getFieldName(), this.id.toString());
			jsonObject.put(ProductFieldName.LOOKUP_CODE.getFieldName(), this.lookupCode);
			jsonObject.put(ProductFieldName.QUANTITY_SOLD.getFieldName(), this.quantity_sold);
			jsonObject.put(ProductFieldName.COUNT.getFieldName(), this.count);
			jsonObject.put(ProductFieldName.CREATED_ON.getFieldName(), (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)).format(this.createdOn));
			jsonObject.put(ProductFieldName.PRICE.getFieldName(), this.price);
			jsonObject.put(ProductFieldName.CARTID.getFieldName(), this.cartid.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public Product() {
		this.count = -1;
		this.quantity_sold = 0;
		this.lookupCode = "";
		this.id = new UUID(0, 0);
		this.createdOn = new Date();
		this.price = 0;
		this.cartid = new UUID(0, 0);
	}

	public Product(ProductTransition productTransition) {
		this.id = productTransition.getId();
		this.count = productTransition.getCount();
		this.quantity_sold = productTransition.getQuantity_sold();
		this.createdOn = productTransition.getCreatedOn();
		this.lookupCode = productTransition.getLookupCode();
		this.price = productTransition.getPrice();
	}
}
