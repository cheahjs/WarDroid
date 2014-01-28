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
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.*;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.fragments.FoundryFragment;
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

public class LoginActivity extends SherlockFragment {
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

    private SherlockFragment mFinishFragment;

    private GlobalApplication mApplication;

    public LoginActivity(SherlockFragment frag) {
        mFinishFragment = frag;
    }

    public LoginActivity() {
        mFinishFragment = new FoundryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_login, container, false);
        mApplication = (GlobalApplication)getActivity().getApplication();
        // Set up the login form.
        mEmailView = (EditText) rootView.findViewById(R.id.email);

        mPasswordView = (EditText) rootView.findViewById(R.id.password);
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

        mSavePasswordView = (CheckBox) rootView.findViewById(R.id.savePwd);

        if (mApplication.getEmail() != null) {
            mEmailView.setText(mApplication.getEmail());
            mPasswordView.setText("123456789");
            mHash = mApplication.getHashedPassword();
            mSavePasswordView.setChecked(true);
            mSavedEmail = mApplication.getEmail();
        }

        mLoginFormView = rootView.findViewById(R.id.login_form);
        mLoginStatusView = rootView.findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) rootView.findViewById(R.id.login_status_message);

        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        return rootView;
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
        mPassword = mPasswordView.getText().toString();
        //invalidate hash if a) email does not match or b) password has changed
        if (!mSavedEmail.equals(mEmail)) {
            mHash = null;
            Log.d("deathsnacks", "password hash has been invalidated because email doesn't match saved.");
        }
        if (!mPassword.equals("123456789")) {
            mHash = null;
            Log.d("deathsnacks", "password hash has been invalidated because password has been modified.");
        }

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
            InputMethodManager imm = ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
            try {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask(getActivity());
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (!isAdded())
            return;
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
                        mApplication.setEmail(null);
                        mApplication.setHashedPassword(null);
                        break;
                    case SUCCESS:
                        if (mSavePasswordView.isChecked()) {
                            mApplication.setEmail(mEmail);
                            mApplication.setHashedPassword(mHash == null ? Login.getPasswordHash(mPassword) : mHash);
                        } else {
                            mApplication.setEmail(null);
                            mApplication.setHashedPassword(null);
                        }
                        Toast.makeText(activity.getApplicationContext(), getString(R.string.notification_welcome) +
                                " " + ((GlobalApplication)getActivity().getApplication()).getDisplayName(), Toast.LENGTH_LONG).show();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, mFinishFragment).commit();
                        break;
                    case ERROR:
                        Toast.makeText(activity.getApplicationContext(), R.string.error_error_occurred, Toast.LENGTH_LONG).show();
                        break;
                    case UNKNOWNUSER:
                        mEmailView.setError(getString(R.string.error_user_does_not_exist));
                        mEmailView.requestFocus();
                        mApplication.setEmail(null);
                        mApplication.setHashedPassword(null);
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
