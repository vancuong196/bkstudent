package com.kuon.bkstudent.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kuon.bkstudent.R;
import com.kuon.bkstudent.api.API;
import com.kuon.bkstudent.exceptions.LoginFailedException;
import com.kuon.bkstudent.exceptions.MissingApiParametersException;
import com.kuon.bkstudent.ultils.SettingManager;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEdtID;
    private EditText mEdtPassword;
    private View mProgressView;
    private SettingManager settingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        settingManager = SettingManager.getInstance(this);
        mEdtID = (AutoCompleteTextView) findViewById(R.id.edt_id);
        //populateAutoComplete();
        String user = settingManager.getUser();
        if (user!=""){
            mEdtID.setText(user);
        }

        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        mEdtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.btn_sign_in);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mProgressView = findViewById(R.id.login_progress);

    }

    private void populateAutoComplete() {


    }





    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEdtID.setError(null);
        mEdtPassword.setError(null);

        // Store values at the time of the login attempt.
        String userID = mEdtID.getText().toString();
        String password = mEdtPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mEdtPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEdtPassword;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mEdtPassword.setError("This password cant be empty");
            focusView = mEdtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userID)) {
            mEdtID.setError(getString(R.string.error_field_required));
            focusView = mEdtID;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userID, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }



    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEdtID.setAdapter(adapter);
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mPassword;
        private String token;
        int errorCode;
        private static final int CONNECTION_FAILED=1;
        private static final int LOGIN_FAILED=2;
        private static final int API_FAILED=3;
        private static final int JSON_FAILED=4;

        UserLoginTask(String id, String password) {
            mId = id;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            try {
                token = API.auth(mId,mPassword);
                if (token==null){
                    errorCode = 1;
                    return false;
                }
                else {
                    return true;
                }
            } catch (IOException e) {
                errorCode = 1;
                return false;

            } catch (JSONException e) {
                errorCode =4;
                e.printStackTrace();
                return false;

            } catch (LoginFailedException e) {
                errorCode = 2;
                return false;

            } catch (MissingApiParametersException e) {
               errorCode = 3;
               return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                settingManager.setToken(token);
                settingManager.setUser(this.mId);
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(myIntent);
                LoginActivity.this.finish();
            } else {
                switch (errorCode){
                    case API_FAILED:
                        Toast.makeText(LoginActivity.this,"Missing api parameter",Toast.LENGTH_SHORT).show();
                        break;
                    case JSON_FAILED:
                        Toast.makeText(LoginActivity.this,"Unknown respond from server",Toast.LENGTH_SHORT).show();

                        break;
                    case CONNECTION_FAILED:
                        Toast.makeText(LoginActivity.this,"Khong the ket noi den server, kiem tra cai dat mang",Toast.LENGTH_SHORT).show();
                        break;
                    case LOGIN_FAILED:
                        mEdtPassword.setError(getString(R.string.error_incorrect_password));
                        mEdtPassword.requestFocus();
                        break;
                }

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

