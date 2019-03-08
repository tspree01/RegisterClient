package edu.uark.uarkregisterapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import java.util.List;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class LandingActivity extends AppCompatActivity {
	final static String SCOPES [] = {"https://uarkregisterapp.onmicrosoft.com/api/read"};
	private static final String TAG = LandingActivity.class.getSimpleName();
	private Button loginButton;
	private Button signOutButton;
	private boolean logged_In = false;
	private EmployeeTransition employeeTransition = new EmployeeTransition();
	private EmployeeTransition tokenCliams = new EmployeeTransition();


	/* Azure AD Variables */
	private PublicClientApplication sampleApp;
	private AuthenticationResult authResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* Configure your sample app and save state for this activity */
		sampleApp = null;
		if (sampleApp == null) {
			sampleApp = new PublicClientApplication(
					this.getApplicationContext(), "e2266648-f2aa-444a-9767-a0a40ae3105a", "https://uarkregisterapp.b2clogin.com/tfp/uarkregisterapp.onmicrosoft.com/B2C_1_uarkregisterapp_SignIn");
		}

		if(!logged_In) {
			setContentView(R.layout.activity_main);


			loginButton = (Button) findViewById(R.id.Login);

			loginButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onLoginClicked();
				}
			});
		}
		else{
			setContentView(R.layout.activity_landing);
		}

	}

	/* Handles the redirect from the System Browser */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
	}

	private void onLoginClicked() {
		sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
	}

	//
	// Helper methods manage UI updates
	// ================================
	// updateSuccessLoginUI() - Updates UI when token acquisition succeeds
	// updateSignedOutUI() - Updates UI when app sign out succeeds
	//

	/* Set the UI for successful token acquisition data */
	private void updateSuccessLoginUI() {
		loginButton.setVisibility(View.INVISIBLE);
		logged_In = true;
        //((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
		if (tokenCliams.getRole().equals("Manager")) {
			setContentView(R.layout.activity_landing);
			//this.getWelcomeText().setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
			((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
		}
		else{
			setContentView(R.layout.employee_landing);
			//this.getWelcomeText().setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
			((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
		}
		//this.startActivity(new Intent(getApplicationContext(), LandingActivity.class));
	}
	private void updateSuccessSignUpUI() {
		logged_In = true;
	}

	/* Set the UI for signed out account */
	private void updateSignedOutUI() {
		logged_In = false;
		this.startActivity(new Intent(getApplicationContext(), LandingActivity.class));
	}
	public Activity getActivity() {
		return this;
	}
	private class SaveEmployeeTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			this.savingEmployeeAlert.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Employee employee = (new Employee())
					.setFirst_Name(employeeTransition.getFirst_Name())
					.setLast_Name(employeeTransition.getLast_Name())
					.setId(employeeTransition.getId())
					.setRole(employeeTransition.getRole());

			ApiResponse<Employee> apiResponse = (
					(employee.getManagerID().equals(new UUID(0, 0)))
							? (new EmployeeService()).createEmployee(employee)
							: (new EmployeeService()).updateEmployee(employee)
			);

/*			if (apiResponse.isValidResponse()) {
				employeeTransition.setRecordID(apiResponse.getData().getRecordID());
			}*/

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

			new AlertDialog.Builder(LandingActivity.this).
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

		SaveEmployeeTask() {
			this.savingEmployeeAlert = new AlertDialog.Builder(LandingActivity.this).
					setMessage(R.string.alert_dialog_employee_save).
					create();
		}
	}

	/* Callback used for interactive request.  If succeeds we use the access
	 * token to call the Microsoft Graph. Does not check cache
	 */
	private AuthenticationCallback getAuthInteractiveCallback() {
		return new AuthenticationCallback() {
			@Override
			public void onSuccess(AuthenticationResult authenticationResult) {
				/* Successfully got a token, call graph now */
				Log.d(TAG, "Successfully authenticated");
				Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

				/* Store the auth result */
				authResult = authenticationResult;
				JWT ID_token = new JWT(authResult.getIdToken());
				if(!logged_In) {
					Claim first_Name_Claim = ID_token.getClaim("given_name");
					Claim last_Name_Claim = ID_token.getClaim("family_name");
					Claim job_Title_Claim = ID_token.getClaim("jobTitle");
					Claim new_User_Claim = ID_token.getClaim("newUser");
					Claim object_ID_Claim = ID_token.getClaim("oid");
					UUID object_ID = UUID.fromString(object_ID_Claim.asString());
					employeeTransition.setId(object_ID);
					employeeTransition.setFirst_Name(first_Name_Claim.asString());
					employeeTransition.setLast_Name(last_Name_Claim.asString());
					employeeTransition.setRole(job_Title_Claim.asString());
					tokenCliams.setRole(job_Title_Claim.asString());
					tokenCliams.setFirst_Name(first_Name_Claim.asString());
				}else{
					Claim first_Name_Claim = ID_token.getClaim("given_name");
					Claim last_Name_Claim = ID_token.getClaim("family_name");
					Claim job_Title_Claim = ID_token.getClaim("jobTitle");
					Claim new_User_Claim = ID_token.getClaim("newUser");
					Claim object_ID_Claim = ID_token.getClaim("oid");
					UUID object_ID = UUID.fromString(object_ID_Claim.asString());
					//boolean isNewUser = new_User_Claim.asBoolean();
					employeeTransition.setId(object_ID);
					employeeTransition.setFirst_Name(first_Name_Claim.asString());
					employeeTransition.setLast_Name(last_Name_Claim.asString());
					employeeTransition.setRole(job_Title_Claim.asString());
					tokenCliams.setRole(job_Title_Claim.asString());
					tokenCliams.setFirst_Name(first_Name_Claim.asString());
						(new SaveEmployeeTask()).execute();

				}

				/* call graph */
				//callWebAPI();
				if(!logged_In) {
					/* update the UI to post call graph state */
					updateSuccessLoginUI();
				}else {
					updateSuccessSignUpUI();
				}
			}

			@Override
			public void onError(MsalException exception) {
				/* Failed to acquireToken */
				Log.d(TAG, "Authentication failed: " + exception.toString());

				if (exception instanceof MsalClientException) {
					/* Exception inside MSAL, more info inside MsalError.java */
				} else if (exception instanceof MsalServiceException) {
					/* Exception when communicating with the STS, likely config issue */
				}
			}

			@Override
			public void onCancel() {
				/* User canceled the authentication */
				Log.d(TAG, "User cancelled login.");
			}
		};
	}
	private TextView getWelcomeText() {
		return (TextView) this.findViewById(R.id.text_view_welcome);
	}

	public void displayTransactionButtonOnClick(View view) {
		this.startActivity(new Intent(getApplicationContext(), ProductsListingActivity.class));
	}

	public void displayCreateEmployeeButtonOnClick(View view) {
		/* Configure your sample app and save state for this activity */
		sampleApp = null;
		if (sampleApp == null) {
			sampleApp = new PublicClientApplication(
					this.getApplicationContext(), "e2266648-f2aa-444a-9767-a0a40ae3105a", "https://uarkregisterapp.b2clogin.com/tfp/uarkregisterapp.onmicrosoft.com/B2C_1_uarkregisterapp_signup");
		}

		sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
	}

	public void SalesReportButtonOnClick(View view) {
		Intent intent = new Intent(getApplicationContext(), ProductViewActivity.class);

		intent.putExtra(
			getString(R.string.intent_extra_product),
			new ProductTransition()
		);

		this.startActivity(intent);
	}

	public void SignOut(View view) {
		//* Attempt to get a account and remove their cookies from cache *//*
		List<IAccount> accounts = null;

		try {
			accounts = sampleApp.getAccounts();

			if (accounts == null) {
				//* We have no accounts *//*

			} else if (accounts.size() == 1) {
				//* We have 1 account *//*
				//* Remove from token cache *//*
				sampleApp.removeAccount(accounts.get(0));
				updateSignedOutUI();

			}
			else {
				//* We have multiple accounts *//*
				for (int i = 0; i < accounts.size(); i++) {
					sampleApp.removeAccount(accounts.get(i));
				}
				updateSignedOutUI();
			}

			Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
					.show();

		} catch (IndexOutOfBoundsException e) {
			Log.d(TAG, "User at this position does not exist: " + e.toString());
		}
	}
}