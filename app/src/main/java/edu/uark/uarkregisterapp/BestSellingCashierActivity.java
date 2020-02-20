package edu.uark.uarkregisterapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.EmployeeSalesListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class BestSellingCashierActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_sales_report_listing);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.employees = new ArrayList<>();
        this.employeeSalesListAdapter = new EmployeeSalesListAdapter(this, this.employees);

        this.getEmployeesListView().setAdapter(this.employeeSalesListAdapter);
        this.getEmployeesListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EmployeeViewActivity.class);

                intent.putExtra(
                        getString(R.string.intent_extra_employee),
                        new EmployeeTransition((Employee) getEmployeesListView().getItemAtPosition(position))
                );

                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        (new RetrieveEmployeesTask()).execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Respond to the action bar's Up/Home button
                this.finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private ListView getEmployeesListView() {
        return (ListView) this.findViewById(R.id.list_view_products);
    }

    private class RetrieveEmployeesTask extends AsyncTask<Void, Void, ApiResponse<List<Employee>>> {
        @Override
        protected void onPreExecute() {
            this.loadingEmployeesAlert.show();
        }

        @Override
        protected ApiResponse<List<Employee>> doInBackground(Void... params) {
            ApiResponse<List<Employee>> apiResponse = (new EmployeeService()).getEmployees();

            if (apiResponse.isValidResponse()) {
                employees.clear();
                employees.addAll(apiResponse.getData());

                Iterator<Employee> iterator = employees.iterator();
                Employee employee = employees.get(0);
                double bigger_number = 0;
                while(iterator.hasNext()){

                    Employee employeee = iterator.next();
                        if (employeee.getAmount_Of_Money_Made() > bigger_number) {
                            employee = employeee;
                            bigger_number = employee.getAmount_Of_Money_Made();
                        }

                    }
                employees.clear();
                employees.add(employee);
                }


            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse<List<Employee>> apiResponse) {
            if (apiResponse.isValidResponse()) {
                employeeSalesListAdapter.notifyDataSetChanged();
            }

            this.loadingEmployeesAlert.dismiss();

            if (!apiResponse.isValidResponse()) {
                new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingCashierActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
                        setMessage(R.string.alert_dialog_employees_load_failure).
                        setPositiveButton(
                                R.string.button_dismiss,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                }
                        ).
                        create().
                        show();
            }
        }

        private AlertDialog loadingEmployeesAlert;

        private RetrieveEmployeesTask() {
            this.loadingEmployeesAlert = new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingCashierActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
                    setMessage(R.string.alert_dialog_employees_loading).
                    create();
        }
    }

    private List<Employee> employees;
    private EmployeeSalesListAdapter employeeSalesListAdapter;
}
