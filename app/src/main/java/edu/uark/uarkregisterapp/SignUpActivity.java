package edu.uark.uarkregisterapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class SignUpActivity extends AppCompatActivity {

    /* Azure AD v2 Configs */
    final static String SCOPES [] = {"https://uarkregisterapp.onmicrosoft.com/api/read"};

    /* UI & Debugging Variables */
    private static final String TAG = SignUpActivity.class.getSimpleName();

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
                    this.getApplicationContext(), "e2266648-f2aa-444a-9767-a0a40ae3105a", "https://uarkregisterapp.b2clogin.com/tfp/uarkregisterapp.onmicrosoft.com/B2C_1_uarkregisterapp_signup");
        }

        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
    }

    //
    // Core Identity methods used by MSAL
    // ==================================
    // onActivityResult() - handles redirect from System browser
    // onCallGraphClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // onSignOutClicked() - Signs account out of the app & updates UI
    // callWebAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Handles the redirect from the System Browser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

/*    @Override
    protected void onResume() {
        super.onResume();
        (new SaveEmployeeTask()).execute();

    }*/
    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
        //LandingActivity.logged_In = true;
    }

    //
    // App callbacks for MSAL
    // ======================
    // getActivity() - returns activity so we can acquireToken within a callback
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;


                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
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

            new AlertDialog.Builder(SignUpActivity.this).
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
            this.savingEmployeeAlert = new AlertDialog.Builder(SignUpActivity.this).
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
                Claim first_Name_Claim = ID_token.getClaim("given_name");
                Claim last_Name_Claim = ID_token.getClaim("family_name");
                Claim job_Title_Claim = ID_token.getClaim("jobTitle");
                Claim object_ID_Claim = ID_token.getClaim("oid");
                UUID object_ID = UUID.fromString(object_ID_Claim.asString());
                employeeTransition.setId(object_ID);
                employeeTransition.setFirst_Name(first_Name_Claim.asString());
                employeeTransition.setLast_Name(last_Name_Claim.asString());
                employeeTransition.setRole(job_Title_Claim.asString());

                (new SaveEmployeeTask()).execute();

                /* update the UI to post call graph state */
                //updateSuccessUI();
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
    private EmployeeTransition employeeTransition = new EmployeeTransition();

}
