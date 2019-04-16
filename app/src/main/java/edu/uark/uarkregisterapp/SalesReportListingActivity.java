package edu.uark.uarkregisterapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.EmployeeListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.SalesReportActivity;
public class SalesReportListingActivity extends AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_employees_listing);
            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

            ActionBar actionBar = this.getSupportActionBar();
            if (actionBar != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            this.employees = new ArrayList<>();
            this.employeeListAdapter = new EmployeeListAdapter(this, this.employees);

            this.getEmployeesListView().setAdapter(this.employeeListAdapter);
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
            return (ListView) this.findViewById(R.id.list_view_employees);
        }

        private class RetrieveEmployeesTask extends AsyncTask<Void, Void, ApiResponse<Employee>> {
            @Override
            protected void onPreExecute() {
                this.loadingEmployeesAlert.show();
            }

            @Override
            protected ApiResponse<Employee> doInBackground(Void... params) {
                int stuff = 10;
                ApiResponse<Employee> apiResponse = (new EmployeeService()).getEmployeeByTotalSales(stuff);

                if (apiResponse.isValidResponse()) {
                    employees.clear();
                    employees.add(apiResponse.getData());
                }

                return apiResponse;
            }

            @Override
            protected void onPostExecute(ApiResponse<Employee> apiResponse) {
                if (apiResponse.isValidResponse()) {
                    employeeListAdapter.notifyDataSetChanged();
                }

                this.loadingEmployeesAlert.dismiss();

                if (!apiResponse.isValidResponse()) {
                    new AlertDialog.Builder(edu.uark.uarkregisterapp.SalesReportListingActivity.this).
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
                this.loadingEmployeesAlert = new AlertDialog.Builder(edu.uark.uarkregisterapp.SalesReportListingActivity.this).
                        setMessage(R.string.alert_dialog_employees_loading).
                        create();
            }
        }


        private List<Employee> employees;
        private EmployeeListAdapter employeeListAdapter;
    }

