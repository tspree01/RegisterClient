package edu.uark.uarkregisterapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class LandingActivity extends AppCompatActivity {
    final static String SCOPES[] = {"https://uarkregisterapp.onmicrosoft.com/api/read"};
    final static String AUTHORITY = "https://login.microsoftonline.com/common";
    private static final String TAG = LandingActivity.class.getSimpleName();
    private boolean createEmployeeClicked = false;
    private boolean logged_In = false;
    private View landingView;
    private AlertDialog loggingInAlert;
    private AlertDialog errorAlert;
    private EmployeeTransition employeeTransition;
    private EmployeeTransition employeeLoginTokenClaims = new EmployeeTransition();

    /* Azure AD Variables */
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAuthenticationResult authResult;
    private StringBuilder mLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.b2c_config_login,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        /**
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         **/
                        mSingleAccountApp = application;
                        loadAccount();

                    }

                    @Override
                    public void onError(MsalException exception) {
                        displayError(exception);
                    }
                });

/*        if(!logged_In) {
            setContentView(R.layout.activity_main);
            landingView = findViewById(R.id.CoordinatorLayout);
            //setContentView(R.layout.activity_landing);
        }else{
            if (employeeLoginTokenClaims.getRole().equals("Manager")) {
                setContentView(R.layout.activity_landing);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                ActionBar actionBar = this.getSupportActionBar();
                if (actionBar != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", employeeLoginTokenClaims.getFirst_Name()));
            } else {
                setContentView(R.layout.employee_landing);
                ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", employeeLoginTokenClaims.getFirst_Name()));
            }
        }*/
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


    /**
     * Load the currently signed-in account, if there's any.
     */
    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                if(activeAccount == null){
                    mSingleAccountApp.signIn(getActivity(), null,SCOPES, getAuthInteractiveCallback());
                }
                else {
                    // You can use the account data to update your UI or your app database.
                    if (activeAccount.getClaims().get("jobTitle").equals("Manager")) {
                        setContentView(R.layout.activity_landing);
                        landingView = findViewById(R.id.CoordinatorLayout);
                        ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", activeAccount.getClaims().get("given_name")));
                    } else {
                        setContentView(R.layout.employee_landing);
                        ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", activeAccount.getClaims().get("given_name")));
                    }
                }
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                   // performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                displayError(exception);
            }
        });
    }

    /**
     * Display the error message
     */
    private void displayError(@NonNull final Exception exception) {
        errorAlert = new AlertDialog.Builder(LandingActivity.this, R.style.Theme_MaterialComponents_Dialog).
                setMessage(exception.toString()).
                create();
        errorAlert.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * The account may have been removed from the device (if broker is in use).
         *
         * In shared device mode, the account might be signed in/out by other apps while this app is not in focus.
         * Therefore, we want to update the account state by invoking loadAccount() here.
         */
        //loadAccount();
    }

    /* Handles the redirect from the System Browser */
/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSingleAccountApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }*/

    public void Login(View view) {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.signIn(getActivity(), null,SCOPES, getAuthInteractiveCallback());
        loggingInAlert = new AlertDialog.Builder(LandingActivity.this, R.style.Theme_MaterialComponents_Dialog).
                setMessage(R.string.alert_dialog_logging_employee_in).
                create();
        loggingInAlert.show();
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
        if (employeeLoginTokenClaims.getRole().equals("Manager")) {
            setContentView(R.layout.activity_landing);
            landingView = findViewById(R.id.CoordinatorLayout);

            ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", employeeLoginTokenClaims.getFirst_Name()));
            employeeLoginTokenClaims.setEmployeeLoggedIn(logged_In);
        } else {
            setContentView(R.layout.employee_landing);
            ((TextView) findViewById(R.id.text_view_welcome)).setText(String.format("Welcome %s! What would you like to do?", employeeLoginTokenClaims.getFirst_Name()));
        }
        //this.startActivity(new Intent(getApplicationContext(), LandingActivity.class));
    }

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
                    .setFirst_Name(employeeLoginTokenClaims.getFirst_Name())
                    .setLast_Name(employeeLoginTokenClaims.getLast_Name())
                    .setId(employeeLoginTokenClaims.getId())
                    .setRole(employeeLoginTokenClaims.getRole());

            ApiResponse<Employee> apiResponse = (
                    (employee.getManagerID().equals(new UUID(0, 0)))
                            ? (new EmployeeService()).createEmployee(employee)
                            : (new EmployeeService()).updateEmployee(employee)
            );

			if (apiResponse.isValidResponse()) {
				//employeeLoginTokenClaims.setRecordID(apiResponse.getData().getRecordID());
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

            new AlertDialog.Builder(LandingActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
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
            this.savingEmployeeAlert = new AlertDialog.Builder(LandingActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
                    setMessage(R.string.alert_dialog_employee_save).
                    create();
        }
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
/*    private AuthenticationCallback getAuthInteractiveCallback() {
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
                employeeLoginTokenClaims.setId(object_ID);
                employeeLoginTokenClaims.setFirst_Name(first_Name_Claim.asString());
                employeeLoginTokenClaims.setLast_Name(last_Name_Claim.asString());
                employeeLoginTokenClaims.setRole(job_Title_Claim.asString());
                if (logged_In) {
                    (new SaveEmployeeTask()).execute();
                } else {
                    loggingInAlert.dismiss();
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
    }*/
    /**
     * Callback used for interactive request.
     * If succeeds we use the access token to call the Microsoft Graph.
     * Does not check cache.
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getAccount().getClaims().get("id_token"));

                authResult = authenticationResult;
                JWT access_Token = new JWT(authResult.getAccessToken());
                Claim first_Name_Claim = access_Token.getClaim("given_name");
                Claim last_Name_Claim = access_Token.getClaim("family_name");
                Claim job_Title_Claim = access_Token.getClaim("jobTitle");
                Claim new_User_Claim = access_Token.getClaim("newUser");
                Claim object_ID_Claim = access_Token.getClaim("oid");
                //UUID object_ID = UUID.fromString(object_ID_Claim.asString());
                //employeeLoginTokenClaims.setId(object_ID);
                employeeLoginTokenClaims.setFirst_Name(first_Name_Claim.asString());
                employeeLoginTokenClaims.setLast_Name(last_Name_Claim.asString());
                employeeLoginTokenClaims.setRole(job_Title_Claim.asString());
                if (logged_In) {
                    (new SaveEmployeeTask()).execute();
                } else {
                    if(loggingInAlert == null){
                        updateSuccessLoginUI();
                    }
                    else{
                        loggingInAlert.dismiss();
                        updateSuccessLoginUI();
                    }

                }
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
                displayError(exception);

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


    public void startTransactionButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ProductsListingActivity.class);
        intent.putExtra(
                getString(R.string.intent_extra_employee),
                employeeLoginTokenClaims
        );
        this.startActivity(intent);
    }

    public void displayCreateEmployeeButtonOnClick(View view) {
        /* Configure your sample app and save state for this activity */

/*        sampleApp = new PublicClientApplication(
                this.getApplicationContext(), R.raw.b2c_config_signup);
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());*/
    }
	public void displaySalesReportButtonOnClick(View view){
        this.startActivity(new Intent(getApplicationContext(), SalesReportActivity.class));
    }

    public void SalesReportButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ProductViewActivity.class);
    }

	public void InventoryButtonOnClick(View view) {
		Intent intent = new Intent(getApplicationContext(), ProductViewActivity.class);

        intent.putExtra(
                getString(R.string.intent_extra_product),
                new ProductTransition()
        );

        this.startActivity(intent);
    }

    public void ProductSearchButtonOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), ProductViewSearch.class);

        intent.putExtra(
                getString(R.string.intent_extra_product),
                new ProductTransition()
        );

        this.startActivity(intent);
    }

    public void SignOut(View view) {
        if (mSingleAccountApp == null) {
            return;
        }

        /**
         * Removes the signed-in account and cached tokens from this app (or device, if the device is in shared mode).
         */
        mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                updateSignedOutUI();
                //performOperationOnSignOut();
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                displayError(exception);
            }
        });
    }

    /**
     * Updates UI when app sign out succeeds
     */
/*    private void performOperationOnSignOut() {
        final String signOutText = "Signed Out.";
        Snackbar.make(landingView, signOutText, Snackbar.LENGTH_SHORT)
                .show();
    }*/


}