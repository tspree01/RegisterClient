package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductSalesListAdapter extends BaseAdapter {
	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(searchContext);
			view = inflater.inflate(R.layout.list_view_item_product_sales, parent, false);
		}

		if (product_list.get(position) != null) {
			TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
			if (lookupCodeTextView != null) {
				lookupCodeTextView.setText(product_list.get(position).getLookupCode());
			}

			TextView countTextView = (TextView) view.findViewById(R.id.list_view_item_product_count);
			if (countTextView != null) {
				countTextView.setText(String.format(Locale.getDefault(), "%d", product_list.get(position).getTotal_Sales()));
			}
		}
		return view;
	}


	@Override
	public int getCount() {
		return product_list.size();
	}

	@Override
	public Product getItem(int position) {
		return product_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public ProductSalesListAdapter(Context context, List<Product> products) {
		this.searchContext = context;
		this.product_list = products;
	}

	Context searchContext;
	private List<Product> product_list;
}
