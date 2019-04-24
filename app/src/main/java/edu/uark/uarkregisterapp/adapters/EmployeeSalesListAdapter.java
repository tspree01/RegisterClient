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
import edu.uark.uarkregisterapp.models.api.Employee;

public class EmployeeSalesListAdapter extends ArrayAdapter<Employee> {
	@NonNull
	@Override
	public View getView(int position, View convertView, @NonNull ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(this.getContext());
			view = inflater.inflate(R.layout.list_view_item_employee_sales, parent, false);
		}

		Employee employee = this.getItem(position);
		if (employee != null) {
			TextView EmployeeIDTextView = view.findViewById(R.id.list_view_item_employee_id);
			if (EmployeeIDTextView != null) {
				EmployeeIDTextView.setText(employee.getFirst_Name());
			}
			TextView EmployeeSalesTextView = view.findViewById(R.id.list_view_employees_total_sales);
			if (EmployeeSalesTextView != null) {
				EmployeeSalesTextView.setText(String.format(Locale.getDefault(), "%d",employee.getTotal_gain()));
			}
		}

		return view;
	}

	public EmployeeSalesListAdapter(Context context, List<Employee> employee) {
		super(context, R.layout.list_view_item_employee_sales, employee);
	}
}
