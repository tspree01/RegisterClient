package edu.uark.uarkregisterapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.common.internal.dto.AccountRecord;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.LoginActivity;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class LandingActivity extends AppCompatActivity {
    final static String SCOPES[] = {"https://uarkregisterapp.onmicrosoft.com/api/read"};
    private static final String TAG = LandingActivity.class.getSimpleName();
  //  private Button loginButton;
   // private Button signOutButton;
    private boolean createEmployeeClicked = false;
    private boolean logged_In = false;
    private EmployeeTransition loginTokenClaims = new EmployeeTransition();


    /* Azure AD Variables */
    private PublicClientApplication sampleApp;
    private AuthenticationResult authResult;
    private StringBuilder mLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Configure your sample app and save state for this activity */
        sampleApp = new PublicClientApplication(
                this.getApplicationContext(), R.raw.b2c_config);




        if(!logged_In) {
            setContentView(R.layout.activity_main);
        }else{
            if (loginTokenClaims.getRole().equals("Manager")) {
                setContentView(R.layout.activity_landing);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                ActionBar actionBar = this.getSupportActionBar();
                if (actionBar != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }

                //this.getWelcomeText().setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
                ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", loginTokenClaims.getFirst_Name()));
            } else {
                setContentView(R.layout.employee_landing);
                //this.getWelcomeText().setText(String.format("Welcome %s! What would you like to do next?", tokenCliams.getFirst_Name()));
                ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", loginTokenClaims.getFirst_Name()));
            }
        }

//        //* Enable logging *//*
//       mLogs = new StringBuilder();
//        Logger.getInstance().setLogLevel(Logger.LogLevel.VERBOSE);
//        Logger.getInstance().setEnablePII(true);
//        Logger.getInstance().setEnableLogcatLog(true);
//        Logger.getInstance().setExternalLogger(new ILoggerCallback() {
//            @Override
//            public void log(String tag, Logger.LogLevel logLevel, String message, boolean containsPII) {
//                mLogs.append(message).append('\n');
//            }
//        });

    }
    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                this.startActivity(new Intent(getApplicationContext(), ProductsListingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Handles the redirect from the System Browser */
    //@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    public void Login(View view) {
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
        logged_In = true;
        if (loginTokenClaims.getRole().equals("Manager")) {
            setContentView(R.layout.activity_landing);
            ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", loginTokenClaims.getFirst_Name()));
        } else {
            setContentView(R.layout.employee_landing);
            ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do next?", loginTokenClaims.getFirst_Name()));
        }
        //this.startActivity(new Intent(getApplicationContext(), LandingActivity.class));
    }

/*    private void updateSuccessSignUpUI() {
        logged_In = true;
    }*/

    /* Set the UI for signed out account */
    private void updateSignedOutUI() {
        logged_In = false;
        setContentView(R.layout.activity_main);
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
                    .setFirst_Name(loginTokenClaims.getFirst_Name())
                    .setLast_Name(loginTokenClaims.getLast_Name())
                    .setId(loginTokenClaims.getId())
                    .setRole(loginTokenClaims.getRole());

            ApiResponse<Employee> apiResponse = (
                    (employee.getManagerID().equals(new UUID(0, 0)))
                            ? (new EmployeeService()).createEmployee(employee)
                            : (new EmployeeService()).updateEmployee(employee)
            );

			if (apiResponse.isValidResponse()) {
				//loginTokenClaims.setRecordID(apiResponse.getData().getRecordID());
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
                //Successfully got a token, call graph now
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                //Store the auth result
                authResult = authenticationResult;
                JWT access_Token = new JWT(authResult.getAccessToken());
                Claim first_Name_Claim = access_Token.getClaim("given_name");
                Claim last_Name_Claim = access_Token.getClaim("family_name");
                Claim job_Title_Claim = access_Token.getClaim("jobTitle");
                Claim new_User_Claim = access_Token.getClaim("newUser");
                Claim object_ID_Claim = access_Token.getClaim("oid");
                UUID object_ID = UUID.fromString(object_ID_Claim.asString());
                loginTokenClaims.setId(object_ID);
                loginTokenClaims.setFirst_Name(first_Name_Claim.asString());
                loginTokenClaims.setLast_Name(last_Name_Claim.asString());
                loginTokenClaims.setRole(job_Title_Claim.asString());

                if (logged_In) {
                    (new SaveEmployeeTask()).execute();
                } else {
                    updateSuccessLoginUI();
				}

			}

            @Override
            public void onError(MsalException exception) {
                //Failed to acquireToken
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    //Exception inside MSAL, more info inside MsalError.java
                } else if (exception instanceof MsalServiceException) {
                    //Exception when communicating with the STS, likely config issue
                }
            }

            @Override
            public void onCancel() {
                //User canceled the authentication
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
        //this.startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        /* Configure your sample app and save state for this activity */

       sampleApp = new PublicClientApplication(
                this.getApplicationContext(), "e2266648-f2aa-444a-9767-a0a40ae3105a", "https://uarkregisterapp.b2clogin.com/tfp/uarkregisterapp.onmicrosoft.com/B2C_1_uarkregisterapp_signup");
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

            } else {
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