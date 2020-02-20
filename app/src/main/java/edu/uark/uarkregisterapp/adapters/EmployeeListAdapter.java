package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Employee;

public class EmployeeListAdapter extends ArrayAdapter<Employee> {
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			view = inflater.inflate(R.layout.list_view_employee_sales_listing_product, parent, false);
		}

		Employee employee = this.getItem(position);
		if (employee != null) {
			TextView EmployeeIDTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
			if (EmployeeIDTextView != null) {
				EmployeeIDTextView.setText(employee.getFirst_Name());
			}

		}

		return view;
	}

	public EmployeeListAdapter(Context context, List<Employee> employee) {
		super(context, R.layout.list_view_employee_sales_listing_product, employee);
	}
}
