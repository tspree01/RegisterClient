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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class SalesReportActivity extends AppCompatActivity {
    private ImageButton submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales_report_view);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.employeeTransition = this.getIntent().getParcelableExtra(this.getString(R.string.intent_sales_report));

    }
    public void ProductSalesReportButtonOnClick(View view){
        this.startActivity(new Intent(getApplicationContext(),ProductSalesReportListingActivity.class));
    }
    public void BestSellingCashierOnClick(View view){
        this.startActivity(new Intent(getApplicationContext(),BestSellingCashierActivity.class));
    }
    public void BestSellingProductOnClick(View view){
        this.startActivity(new Intent(getApplicationContext(),BestSellingProductActivity.class));
    }
    public void SalesReportButtonOnClick(View view){
        this.startActivity(new Intent(getApplicationContext(), SalesReportListingActivity.class));
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

/*		this.getEmployeeFirstNameEditText().setText(SignUpActivity.employeeTransition.getFirst_Name());
		this.getEmployeeLastNameEditText().setText(SignUpActivity.employeeTransition.getLast_Name());
		this.getEmployeeIDEditText().setText(SignUpActivity.employeeTransition.getId().toString());
		this.getEmployeeRoleEditText().setText(SignUpActivity.employeeTransition.getRole());*/
/*		this.getEmployeeCreatedOnEditText().setText(
			(new SimpleDateFormat("MM/dd/yyyy", Locale.US)).format(this.employeeTransition.getCreatedOn())
		);*/


    private boolean validateInput() {
        boolean inputIsValid = true;
        String validationMessage = StringUtils.EMPTY;

/*		if (StringUtils.isBlank(this.getEmployeeRecordIDEditText().getText().toString())) {
			validationMessage = this.getString(R.string.validation_employee_recordID);
			inputIsValid = false;
		}*/

        if (!inputIsValid) {
            new AlertDialog.Builder(this).
                    setMessage(validationMessage).
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

        return inputIsValid;
    }

    private EmployeeTransition employeeTransition;
}
