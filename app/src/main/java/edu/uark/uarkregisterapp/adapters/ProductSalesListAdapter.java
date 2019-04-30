package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductSalesListAdapter extends ArrayAdapter<Product> {
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			view = inflater.inflate(R.layout.list_view_item_product_sales, parent, false);
		}

		Product product = this.getItem(position);
		if (product != null) {
			TextView lookupCodeTextView =  view.findViewById(R.id.list_view_item_product_lookup_code);
			if (lookupCodeTextView != null) {
				lookupCodeTextView.setText(product.getLookupCode());
			}
			TextView ProductSalesTextView = view.findViewById(R.id.list_view_product_total_sales);
			if (ProductSalesTextView != null){
			    ProductSalesTextView.setText(String.format(Locale.getDefault(), "$ %.2f",product.getTotal_Sales()));
            }

		}

		return view;
	}

	public ProductSalesListAdapter(Context context, List<Product> products) {
		super(context, R.layout.list_view_item_product, products);
	}
}