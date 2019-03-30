package edu.uark.uarkregisterapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.ILoggerCallback;
import com.microsoft.identity.client.Logger;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import java.util.List;
import java.util.UUID;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;


public class LoginActivity extends AppCompatActivity {

    /* Azure AD v2 Configs */
    final static String SCOPES[] = {"https://uarkregisterapp.onmicrosoft.com/api/read"};

    /* UI & Debugging Variables */
    private static final String TAG = LoginActivity.class.getSimpleName();
    Button loginButton;
    Button signOutButton;

    /* Azure AD Variables */
    public PublicClientApplication sampleApp;
    private AuthenticationResult authResult;
    public EmployeeTransition loginActivityCliams = new EmployeeTransition();
    private StringBuilder mLogs;
    public static List<IAccount> userAccounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOutButton = (Button) findViewById(R.id.clearCache);

        loginButton = (Button) findViewById(R.id.Login);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSignOutClicked();
            }
        });

        /* Configure your sample app and save state for this activity */
        sampleApp = null;
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(
                    this.getApplicationContext(), R.raw.b2c_config);
        }

        //Enable logging
/*        mLogs = new StringBuilder();
        Logger.getInstance().setLogLevel(Logger.LogLevel.VERBOSE);
        Logger.getInstance().setEnablePII(true);
        Logger.getInstance().setEnableLogcatLog(true);
        Logger.getInstance().setExternalLogger(new ILoggerCallback() {
            @Override
            public void log(String tag, Logger.LogLevel logLevel, String message, boolean containsPII) {
                mLogs.append(message).append('\n');
            }
        });*/


        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
//         */
//        List<IAccount> accounts = null;
//
//        try {
//            accounts = sampleApp.getAccounts();
//
//            if (accounts != null && accounts.size() == 1) {
//                //* We have 1 account *//*
//
//                sampleApp.acquireTokenSilentAsync(SCOPES, accounts.get(0), getAuthSilentCallback());
//            } else {
//                //* We have no account or >1 account *//*
//            }
//        } catch (IndexOutOfBoundsException e) {
//            Log.d(TAG, "Account at this position does not exist: " + e.toString());
//        }

    }

    //
    // Core Identity methods used by MSAL
    // ==================================
    // onActivityResult() - handles redirect from System browser
    // onLoginClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // onSignOutClicked() - Signs account out of the app & updates UI
    // callWebAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Handles the redirect from the System Browser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    /* Use MSAL to acquireToken for the end-user
     * Callback will have a access token & update UI
     */
    public void Login(View view) {
        //Navigation.findNavController(view).navigate(R.id.fragment);
        //NavController navController = Navigation.findNavController(getActivity(), view.getId());
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
/*        findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
        findViewById(R.id.Login).setVisibility(View.INVISIBLE);
        findViewById(R.id.clearCache).setVisibility(View.VISIBLE);*/

    }

    private void onLoginClicked() {
        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
    }

    /* Clears an account's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     */
    public void onSignOutClicked() {

        /* Attempt to get a account and remove their cookies from cache */
        List<IAccount> accounts = null;

        try {
            accounts = sampleApp.getAccounts();

            if (accounts == null) {
                /* We have no accounts */

            } else if (accounts.size() == 1) {
                /* We have 1 account */
                /* Remove from token cache */
                sampleApp.removeAccount(accounts.get(0));
                updateSignedOutUI();

            }
            else {
                /* We have multiple accounts */
                for (int i = 0; i < accounts.size(); i++) {
                    sampleApp.removeAccount(accounts.get(i));
                }
            }

            Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                    .show();

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }
    //
    // Helper methods manage UI updates
    // ================================
    // updateGraphUI() - Sets graph response in UI
    // updateSuccessUI() - Updates UI when token acquisition succeeds
    // updateSignedOutUI() - Updates UI when app sign out succeeds
    //

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {
        Intent intent = new Intent(getApplicationContext(), LandingActivity.class);

        intent.putExtra("loginActivityClaim", loginActivityCliams);

        this.startActivity(intent);
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

                /* call graph */
                //callWebAPI();

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

    /* Callback used for interactive request.  If succeeds we use the access
     * token. Does not check cache
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
                JWT ID_token = new JWT(authResult.getAccessToken());
                userAccounts = sampleApp.getAccounts();
                Claim first_Name_Claim = ID_token.getClaim("given_name");
                Claim last_Name_Claim = ID_token.getClaim("family_name");
                Claim job_Title_Claim = ID_token.getClaim("jobTitle");
                Claim object_ID_Claim = ID_token.getClaim("oid");
                UUID object_ID = UUID.fromString(object_ID_Claim.asString());
                loginActivityCliams.setId(object_ID);
                loginActivityCliams.setFirst_Name(first_Name_Claim.asString());
                loginActivityCliams.setLast_Name(last_Name_Claim.asString());
                loginActivityCliams.setRole(job_Title_Claim.asString());
                //loginActivityCliams.setuserAccounts(authResult.getAccount());


                /* call graph */
                //callWebAPI();

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
                }
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Set the UI for signed out account */
    private void updateSignedOutUI() {
        //this.startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        //logged_In = false;
    }

    public void SignOut(View view) {
        //* Attempt to get a account and remove their cookies from cache *//*
        List<IAccount> accounts = null;

        try {
            accounts = LoginActivity.userAccounts;

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
