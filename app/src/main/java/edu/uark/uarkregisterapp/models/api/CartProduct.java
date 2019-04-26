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

public class CartProduct implements ConvertToJsonInterface, LoadFromJsonInterface<CartProduct> {
	private UUID id;
	public UUID getId() {
		return this.id;
	}
	public CartProduct setId(UUID id) {
		this.id = id;
		return this;
	}

	private String lookupCode;
	public String getLookupCode() {
		return this.lookupCode;
	}
	public CartProduct setLookupCode(String lookupCode) {
		this.lookupCode = lookupCode;
		return this;
	}

	private int quantity_sold;
	public int getQuantity_sold() { return quantity_sold; }
	public CartProduct setQuantity_sold(int quantity_sold) {
		this.quantity_sold = quantity_sold;
		return this;
	}

	private Date createdOn;
	public Date getCreatedOn() {
		return this.createdOn;
	}
	public CartProduct setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	private double price;
	public double getPrice() {
		return this.price;
	}
	public CartProduct setPrice(double price) {
		this.price = price;
		return this;
	}

	private UUID cartid;
	public UUID getCartId() {
		return this.cartid;
	}
	public CartProduct setCartId(UUID cartid) {
		this.cartid = cartid;
		return this;
	}


	@Override
	public CartProduct loadFromJson(JSONObject rawJsonObject) {
		String value = rawJsonObject.optString(ProductFieldName.ID.getFieldName());
		if (!StringUtils.isBlank(value)) {
			this.id = UUID.fromString(value);
		}

		this.lookupCode = rawJsonObject.optString(ProductFieldName.LOOKUP_CODE.getFieldName());
		this.quantity_sold = rawJsonObject.optInt(ProductFieldName.QUANTITY_SOLD.getFieldName());

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
			jsonObject.put(ProductFieldName.CREATED_ON.getFieldName(), (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)).format(this.createdOn));
			jsonObject.put(ProductFieldName.PRICE.getFieldName(), this.price);
			jsonObject.put(ProductFieldName.CARTID.getFieldName(), this.cartid.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	public CartProduct() {
		this.quantity_sold = 0;
		this.lookupCode = "";
		this.id = new UUID(0, 0);
		this.createdOn = new Date();
		this.price = 0;
		this.cartid = new UUID(0, 0);
	}

	public CartProduct(ProductTransition productTransition) {
		this.id = productTransition.getId();
		this.quantity_sold = productTransition.getCount();
		this.createdOn = productTransition.getCreatedOn();
		this.lookupCode = productTransition.getLookupCode();
		this.price = productTransition.getPrice();
	}
}
