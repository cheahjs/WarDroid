package com.deathsnacks.wardroid.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.*;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.SaveLogin;
import com.deathsnacks.wardroid.utils.GlobalApplication;
import com.deathsnacks.wardroid.utils.Login;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class LoginActivity extends SherlockActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mPassword;
    private String mHash = null;
    private String mSavedEmail = "";

    private ActionBar mActionBar;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private CheckBox mSavePasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(false);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSavePasswordView = (CheckBox) findViewById(R.id.savePwd);

        File loginFile = new File(getFilesDir().getAbsolutePath() + "/login.json");
        if (loginFile.exists()) {
            Gson gson = new GsonBuilder().create();
            try {
                FileInputStream stream = openFileInput("login.json");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                SaveLogin loginData = gson.fromJson(reader, SaveLogin.class);
                mEmailView.setText(loginData.getEmail());
                mSavedEmail = loginData.getEmail();
                mPasswordView.setText("PASSWORDHASHED");
                mHash = loginData.getPasswordhash();
                mSavePasswordView.setChecked(true);
                reader.close();
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        if (mSavedEmail != mEmail)
            mHash = null;
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!mEmail.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            InputMethodManager imm = ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
            try {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask(this);
            mAuthTask.execute((Void) null);
        }
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

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Login.LoginResponse> {
        private Activity activity;

        public UserLoginTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Login.LoginResponse doInBackground(Void... params) {
            try {
                return Login.DoLogin(activity, mEmail, mPassword, mHash);
            } catch (Exception e) {
                e.printStackTrace();
                return Login.LoginResponse.ERROR;
            }
            //return Login.LoginResponse.FAILED;
        }

        @Override
        protected void onPostExecute(final Login.LoginResponse success) {
            mAuthTask = null;
            showProgress(false);
            try {
                switch (success) {
                    case FAILED:
                        mPasswordView.setError(getString(R.string.error_incorrect_credentials));
                        mPasswordView.requestFocus();
                        break;
                    case SUCCESS:
                        if (mSavePasswordView.isChecked()) {
                            Gson gson = new GsonBuilder().create();
                            SaveLogin login = new SaveLogin(mEmail, mHash == null ? Login.getPasswordHash(mPassword) : mHash);
                            String json = gson.toJson(login);
                            try {
                                FileOutputStream stream = openFileOutput("login.json", Context.MODE_PRIVATE);
                                stream.write(json.getBytes());
                                stream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            File loginFile = new File(getFilesDir().getAbsolutePath() + "/login.json");
                            if (loginFile.exists())
                                loginFile.delete();
                        }
                        Toast.makeText(activity.getApplicationContext(), getString(R.string.notification_welcome) +
                                " " + ((GlobalApplication)getApplication()).getDisplayName(), Toast.LENGTH_LONG).show();
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        finish();
                        break;
                    case ERROR:
                        Toast.makeText(activity.getApplicationContext(), R.string.error_error_occurred, Toast.LENGTH_LONG).show();
                        break;
                    case UNKNOWNUSER:
                        mEmailView.setError(getString(R.string.error_user_does_not_exist));
                        mEmailView.requestFocus();
                        break;
                    case DESYNC:
                        Toast.makeText(activity.getApplicationContext(), R.string.error_time_desync, Toast.LENGTH_LONG).show();
                        break;
                    case INACTIVE:
                        mEmailView.setError(getString(R.string.error_not_activated));
                        mEmailView.requestFocus();
                        break;
                    case SUSPENDED:
                        mEmailView.setError(getString(R.string.error_suspended));
                        mEmailView.requestFocus();
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
