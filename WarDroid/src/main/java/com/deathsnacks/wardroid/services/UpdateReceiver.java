package com.deathsnacks.wardroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.deathsnacks.wardroid.Constants;
import com.deathsnacks.wardroid.utils.Http;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by Admin on 5/21/14.
 */
public class UpdateReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdateReceiver";
    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        ctx = context;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Log.i(TAG, "We got a new update, time to re-register.");
        if (!pref.getBoolean(Constants.PREF_PUSH, false))
            return;
        removeRegistrationId(ctx);
        registerInBackground();
    }

    private String getRegistrationId(Context context) {
        return getRegistrationId(context, true);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context, boolean versionCheck) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString("gcm_reg_id", "");
        Log.d(TAG, "regId=" + registrationId);
        if (registrationId.trim().length() == 0) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("gcm_app_version", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion && versionCheck) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return ctx.getSharedPreferences("gcm", Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx);
                    String regid = gcm.register("338009375920");
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend(regid);

                    storeRegistrationId(ctx, regid);
                } catch (IOException ex) {
                    //if we fail and attempt to register again, we get the same id, so no problem.
                    msg = "Error :" + ex.getMessage();
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, "register msg:" + msg);
            }
        }.execute();
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        Log.i(TAG, "regId=" + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gcm_reg_id", regId);
        editor.putInt("gcm_app_version", appVersion);
        editor.commit();
    }

    private void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Log.i(TAG, "removing reg id.");
        editor.remove("gcm_reg_id");
        editor.remove("gcm_app_version");
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * to send messages to your app.
     */
    private void sendRegistrationIdToBackend(String id) throws IOException {
        String response = Http.get("http://deathsnacks.com/api/wardroid/registerPush.php?id=" + id);
        if (!response.contains("success:") && !response.contains("already exists"))
            throw new IOException("Failed to send gcm id back to server. " + response);
    }
}
