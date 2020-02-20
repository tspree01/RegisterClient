package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Employee;

public class EmployeeSalesListAdapter extends BaseAdapter {
	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(searchContext);
			view = inflater.inflate(R.layout.list_view_employee_sales_listing_product, parent, false);
		}

		if(employee_list != null && position < employee_list.size()){
			final Employee employee = employee_list.get(position);
			TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
			if (lookupCodeTextView != null) {
				lookupCodeTextView.setText(employee.getFirst_Name());
			}

			TextView priceTextView = (TextView) view.findViewById (R.id.list_view_item_product_price);
			if (priceTextView != null) {
				priceTextView.setText(String.format(Locale.getDefault(), "$ %.2f", employee.getAmount_Of_Money_Made()));
			}
		}
		return view;
	}


	@Override
	public int getCount() {
		return employee_list.size();
	}

	@Override
	public Employee getItem(int position) {
		return employee_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public EmployeeSalesListAdapter(Context context, List<Employee> employees) {
		this.searchContext = context;
		this.employee_list = employees;
	}

	Context searchContext;
	private List<Employee> employee_list;
}
