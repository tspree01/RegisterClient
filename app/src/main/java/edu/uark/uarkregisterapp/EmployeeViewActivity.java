package edu.uark.uarkregisterapp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

public class EmployeeViewActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_employee_view);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

		ActionBar actionBar = this.getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		this.employeeTransition = this.getIntent().getParcelableExtra(this.getString(R.string.intent_extra_employee));
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

	@Override
	protected void onResume() {
		super.onResume();

		if (!this.employeeTransition.getId().equals(new UUID(0, 0))) {
			this.getDeleteImageButton().setVisibility(View.VISIBLE);
		} else {
			this.getDeleteImageButton().setVisibility(View.INVISIBLE);
		}
		this.getEmployeeRecordIDEditText().setText(this.employeeTransition.getRecordID());
		this.getEmployeeCreatedOnEditText().setText(
			(new SimpleDateFormat("MM/dd/yyyy", Locale.US)).format(this.employeeTransition.getCreatedOn())
		);
	}

	public void saveButtonOnClick(View view) {
		if (!this.validateInput()) {
			return;
		}

		(new SaveEmployeeTask()).execute();
	}

	public void deleteButtonOnClick(View view) {
		new AlertDialog.Builder(this).
			setMessage(R.string.alert_dialog_employee_delete_confirm).
			setPositiveButton(
				R.string.button_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						(new DeleteEmployeeTask()).execute();
					}
				}
			).
			setNegativeButton(
				R.string.button_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}
			).
			create().
			show();
	}

	private EditText getEmployeeRecordIDEditText() {
		return (EditText) this.findViewById(R.id.edit_text_employee_recordID);
	}
	private EditText getEmployeeFirst_nameText(){
		return (EditText) this.findViewById(R.id.edit_text_employee_first_name);
	}

	private EditText getEmployeeCreatedOnEditText() {
		return (EditText) this.findViewById(R.id.edit_text_employee_created_on);
	}

	private ImageButton getDeleteImageButton() {
		return (ImageButton) this.findViewById(R.id.button_activity_edit_delete);
	}

	private boolean validateInput() {
		boolean inputIsValid = true;
		String validationMessage = StringUtils.EMPTY;

		if (StringUtils.isBlank(this.getEmployeeRecordIDEditText().getText().toString())) {
			validationMessage = this.getString(R.string.validation_employee_recordID);
			inputIsValid = false;
		}

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

	private class SaveEmployeeTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			this.savingEmployeeAlert.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Employee employee = (new Employee()).
				setId(employeeTransition.getId()).
				setRecordID(getEmployeeRecordIDEditText().getText().toString());

			ApiResponse<Employee> apiResponse = (
				(employee.getId().equals(new UUID(0, 0)))
					? (new EmployeeService()).createEmployee(employee)
					: (new EmployeeService()).updateEmployee(employee)
			);

			if (apiResponse.isValidResponse()) {
				employeeTransition.setRecordID(apiResponse.getData().getRecordID());
			}

			return apiResponse.isValidResponse();
		}

		@Override
		protected void onPostExecute(Boolean successfulSave) {
			String message;

			savingEmployeeAlert.dismiss();

			if (successfulSave) {
				message = getString(R.string.alert_dialog_employee_save_success);
			} else {
				message = getString(R.string.alert_dialog_employee_save_failure);
			}

			new AlertDialog.Builder(EmployeeViewActivity.this).
				setMessage(message).
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

		private AlertDialog savingEmployeeAlert;

		private SaveEmployeeTask() {
			this.savingEmployeeAlert = new AlertDialog.Builder(EmployeeViewActivity.this).
				setMessage(R.string.alert_dialog_employee_save).
				create();
		}
	}

	private class DeleteEmployeeTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			this.deletingEmployeeAlert.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return (new EmployeeService())
				.deleteEmployee(employeeTransition.getId())
				.isValidResponse();
		}

		@Override
		protected void onPostExecute(final Boolean successfulSave) {
			String message;

			deletingEmployeeAlert.dismiss();

			if (successfulSave) {
				message = getString(R.string.alert_dialog_employee_delete_success);
			} else {
				message = getString(R.string.alert_dialog_employee_delete_failure);
			}

			new AlertDialog.Builder(EmployeeViewActivity.this).
				setMessage(message).
				setPositiveButton(
					R.string.button_dismiss,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							if (successfulSave) {
								finish();
							}
						}
					}
				).
				create().
				show();
		}

		private AlertDialog deletingEmployeeAlert;

		private DeleteEmployeeTask() {
			this.deletingEmployeeAlert = new AlertDialog.Builder(EmployeeViewActivity.this).
				setMessage(R.string.alert_dialog_employee_delete).
				create();
		}
	}

	private EmployeeTransition employeeTransition;
}
