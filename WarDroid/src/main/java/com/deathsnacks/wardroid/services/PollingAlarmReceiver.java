package com.deathsnacks.wardroid.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.deathsnacks.wardroid.Constants;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.MainActivity;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.httpclasses.Alert;
import com.deathsnacks.wardroid.utils.httpclasses.InvasionMini;
import com.squareup.okhttp.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 03/02/14.
 */
public class PollingAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "PollingAlarmReceiver";
    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences mHttpPreferences;
    private OkHttpClient client;
    //private SharedPreferences.Editor mEditor;
    private List<String> mNotifications;
    private NotificationManager mNotificationManager;
    private Boolean mVibrate;
    private List<String> mItemFilters;
    private List<String> mPlanetFilters;
    private int mCreditFilter;
    private List<String> mTypeFilters;
    private List<String> mCustomFilters;
    private Boolean mCustomFilered;
    private Boolean mItemFiltered;
    private Boolean mPlanetFiltered;
    private Boolean mCreditFiltered;
    private Boolean mTypeFiltered;
    private PowerManager.WakeLock mWakeLock;
    private Boolean mAlertSuccess;
    private Boolean mInvasionSuccess;
    private Boolean mEnableVibrate;
    private Boolean mEnableLED;
    private Boolean mInsistent;
    private Boolean mEmptyIcon;
    private long mForceUpdateTime;
    private Boolean mForceUpdate;
    private int mStreamType;
    private boolean mOngoing;
    private boolean mAllowAlerts;
    private boolean mAllowInvasions;
    private int mLedColour;
    private boolean mPc;
    private boolean mPs4;
    private boolean mXbox;
    private List<String> mItems;
    private boolean mUnknownFiltered;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHttpPreferences = context.getSharedPreferences(Constants.SHARED_PREF_POLLING, Context.MODE_PRIVATE);
        client = new OkHttpClient();
        mVibrate = false;
        mNotifications = new ArrayList<String>();
        Log.d(TAG, "Received broadcast for alarm, starting polling.");
        if (mPreferences.getBoolean(Constants.PREF_PUSH, false) && intent != null) {
            Log.i(TAG, "We are abandoning the alarm, since push is on and this isn't forced.");
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                    context, 0, new Intent(context, PollingAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        if (mPreferences.getBoolean(Constants.PREF_ALERT_ENABLED, false)) {
            ArrayList<String> aura = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_AURA_FILTERS, ""))));
            ArrayList<String> bp = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_BP_FILTERS, ""))));
            ArrayList<String> mod = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_MOD_FILTERS, ""))));
            ArrayList<String> resource = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_RESOURCE_FILTERS, ""))));
            mItemFilters = new ArrayList<String>();
            mItemFilters.addAll(aura);
            mItemFilters.addAll(bp);
            mItemFilters.addAll(mod);
            mItemFilters.addAll(resource);
            mItemFiltered = mPreferences.getBoolean(Constants.PREF_FILTER_ENABLED, false);

            mCustomFilters = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_CUSTOM_FILTERS, ""))));
            mCustomFilered = mPreferences.getBoolean(Constants.PREF_CUSTOM_ENABLED, false);
            Log.d(TAG, mPreferences.getString(Constants.PREF_CUSTOM_FILTERS, ""));
            Log.d(TAG, mCustomFilered + "");

            mPlanetFiltered = mPreferences.getBoolean(Constants.PREF_PLANET_ENABLED, false);
            mPlanetFilters = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_PLANET_FILTERS, ""))));

            mCreditFiltered = mPreferences.getBoolean(Constants.PREF_CREDIT_ENABLED, false);
            mCreditFilter = mPreferences.getInt(Constants.PREF_CREDIT_FILTER, 0);

            mTypeFiltered = mPreferences.getBoolean(Constants.PREF_TYPE_ENABLED, false);
            String lowerCase = mPreferences.getString(Constants.PREF_TYPE_FILTERS, "").toLowerCase();
            mTypeFilters = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(lowerCase)));
            Log.d(TAG, lowerCase);

            mUnknownFiltered = mPreferences.getBoolean("unknown_enabled", true);
            mInsistent = mPreferences.getBoolean("insistent", false);
            mEnableVibrate = mPreferences.getBoolean("vibrate", true);
            mEnableLED = mPreferences.getBoolean("light", true);
            mLedColour = mPreferences.getInt("led_colour", Color.WHITE);
            mOngoing = !mPreferences.getBoolean("dismissible", false);

            String tempList = mPreferences.getString("alert_or_invasion", "alerts|invasions");
            mAllowAlerts = tempList.contains("alerts");
            mAllowInvasions = tempList.contains("invasions");

            mAlertSuccess = false;
            mInvasionSuccess = false;

            String volumeType = mPreferences.getString("volume", "notification");
            if (volumeType.equals("notification"))
                mStreamType = AudioManager.STREAM_NOTIFICATION;
            else if (volumeType.equals("alarm"))
                mStreamType = AudioManager.STREAM_ALARM;
            else if (volumeType.equals("media"))
                mStreamType = AudioManager.STREAM_MUSIC;

            mEmptyIcon = mPreferences.getBoolean("empty_enabled", true);

            buildItems();

            String tempText = mPreferences.getString(Constants.PREF_PLATFORM_NOTIFICATIONS, "pc|ps4|xbox");
            mPc = tempText.contains("pc");
            mPs4 = tempText.contains("ps4");
            mXbox = tempText.contains("xbox");

            mForceUpdateTime = 0;

            mForceUpdate = intent != null && intent.getBooleanExtra("force", false);

            if (!mAllowAlerts && !mAllowInvasions) {
                Log.d(TAG, "cancelling alarm since we didn't enable any alerts");
                mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);
            }

            (new RefreshTask()).execute();
            mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WarDroid Notifications");
            mWakeLock.acquire();
        } else {
            Log.d(TAG, "cancelling alarm since we didn't enable alarm");
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                    context, 0, new Intent(context, PollingAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    private void buildItems() {
        mItems = new ArrayList<String>();
        if (mPreferences.getInt("reward_version", 0) > mContext.getResources().getInteger(R.integer.reward_version)) {
            Collections.addAll(mItems, PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("aura_entries", "")));
            Collections.addAll(mItems, PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("bp_entries", "")));
            Collections.addAll(mItems, PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("mod_entries", "")));
            Collections.addAll(mItems, PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("misc_entries", "")));
        } else {
            Collections.addAll(mItems, mContext.getResources().getStringArray(R.array.aura_filter_entries));
            Collections.addAll(mItems, mContext.getResources().getStringArray(R.array.bp_filter_entries));
            Collections.addAll(mItems, mContext.getResources().getStringArray(R.array.mod_filter_entries));
            Collections.addAll(mItems, mContext.getResources().getStringArray(R.array.misc_filter_entries));
        }
    }

    public class RefreshTask extends AsyncTask<Void, Void, Boolean> {

        public RefreshTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mAllowAlerts) {
                doAlerts();
                doAlertsPS4();
                doAlertsXbox();
            } else {
                mAlertSuccess = true;
            }
            if (mAllowInvasions) {
                doInvasions();
                doInvasionsPS4();
                doInvasionsXbox();
            } else {
                mInvasionSuccess = true;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (mAllowAlerts || mAllowInvasions)
                addNotifications();
            mWakeLock.release();
        }

        @Override
        protected void onCancelled() {
        }
    }

    private void doAlerts() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/alerts_raw.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("alerts_modified", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for alerts: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("alerts_cache", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for alerts");
                        Log.d(TAG, cache);
                        parseAlerts(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseAlerts(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("alerts_modified", connection.getLastModified());
                mEditor.putString("alerts_cache", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during alerts alarm");
            e.printStackTrace();
        }
    }

    private void parseAlerts(String response) {
        if (response.length() < 15) {
            mAlertSuccess = true;
            Log.i(TAG, "Alert response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("alert_completed_ids", ""));
        String[] rawAlerts = response.split("\\n");
        Boolean mNew = false;
        String gcmAlerts = "";
        for (String rawAlert : rawAlerts) {
            if (rawAlert.split("\\|").length != 11)
                continue;
            Alert alert = new Alert(rawAlert);
            gcmAlerts += TextUtils.join("|", new String[]{alert.getId(), alert.getNode(), alert.getRegion(),
                    alert.getMission(), alert.getFaction(), String.valueOf(alert.getActivation()),
                    String.valueOf(alert.getExpiry()), TextUtils.join(" - ", alert.getRewards())}) + "\n";
            mNew = false;
            if (!ids.contains(alert.getId())) {
                mNew = true;
                ids.add(alert.getId());
            }
            Log.d(TAG, "found alert: " + alert.getNode() + " - " + TextUtils.join(" - ", alert.getRewards())
                    + " - new: " + mNew);
            if (alert.getExpiry() < System.currentTimeMillis() / 1000) {
                Log.d(TAG, "alert: " + alert.getNode() + " has expired, ignore");
                continue;
            }
            if (completedIds.contains(alert.getId())) {
                Log.i(TAG, "alert: " + alert.getNode() + " has been completed, ignore");
                continue;
            }
            if (!mPc) {
                Log.i(TAG, "PC not enabled, aborting.");
                continue;
            }
            if (isAlertFiltered(alert)) {
                Log.d(TAG, "accepted alert: " + alert.getNode());
                if (mNew)
                    mVibrate = true;
                if (mForceUpdateTime == 0)
                    mForceUpdateTime = alert.getExpiry();
                else {
                    if (alert.getExpiry() < mForceUpdateTime) {
                        mForceUpdateTime = alert.getExpiry();
                    }
                }
                mNotifications.add(String.format("PC [%s]: <b>%s</b>",
                        DateFormat.getTimeFormat(mContext).format(new Date(alert.getExpiry()*1000)), TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_ALERTS, gcmAlerts);
        mEditor.commit();
        mAlertSuccess = true;
    }

    private void doAlertsPS4() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/ps4/alerts_raw.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("alerts_modified_ps4", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for alerts ps4: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("alerts_cache_ps4", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for alerts");
                        Log.d(TAG, cache);
                        parseAlertsPS4(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseAlertsPS4(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("alerts_modified_ps4", connection.getLastModified());
                mEditor.putString("alerts_cache_ps4", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during alerts alarm");
            e.printStackTrace();
        }
    }

    private void parseAlertsPS4(String response) {
        if (response.length() < 15) {
            mAlertSuccess = true;
            Log.i(TAG, "Alert response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("alert_completed_ids", ""));
        String[] rawAlerts = response.split("\\n");
        Boolean mNew = false;
        String gcmAlerts = "";
        for (String rawAlert : rawAlerts) {
            if (rawAlert.split("\\|").length != 11)
                continue;
            Alert alert = new Alert(rawAlert);
            gcmAlerts += TextUtils.join("|", new String[]{alert.getId(), alert.getNode(), alert.getRegion(),
                    alert.getMission(), alert.getFaction(), String.valueOf(alert.getActivation()),
                    String.valueOf(alert.getExpiry()), TextUtils.join(" - ", alert.getRewards())}) + "\n";
            mNew = false;
            if (!ids.contains(alert.getId())) {
                mNew = true;
                ids.add(alert.getId());
            }
            Log.d(TAG, "found alert: " + alert.getNode() + " - " + TextUtils.join(" - ", alert.getRewards())
                    + " - new: " + mNew);
            if (alert.getExpiry() < System.currentTimeMillis() / 1000) {
                Log.d(TAG, "alert: " + alert.getNode() + " has expired, ignore");
                continue;
            }
            if (completedIds.contains(alert.getId())) {
                Log.i(TAG, "alert: " + alert.getNode() + " has been completed, ignore");
                continue;
            }
            if (!mPs4) {
                Log.i(TAG, "PS4 not enabled, aborting.");
                continue;
            }
            if (isAlertFiltered(alert)) {
                Log.d(TAG, "accepted alert: " + alert.getNode());
                if (mNew)
                    mVibrate = true;
                if (mForceUpdateTime == 0)
                    mForceUpdateTime = alert.getExpiry();
                else {
                    if (alert.getExpiry() < mForceUpdateTime) {
                        mForceUpdateTime = alert.getExpiry();
                    }
                }
                mNotifications.add(String.format("PS4 [%s]: <b>%s</b>",
                        DateFormat.getTimeFormat(mContext).format(new Date(alert.getExpiry()*1000)), TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_ALERTS_PS4, gcmAlerts);
        mEditor.commit();
        mAlertSuccess = true;
    }

    private void doAlertsXbox() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/xbox/alerts_raw.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("alerts_modified_xb1", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for alerts xb1: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("alerts_cache_xb1", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for alerts");
                        Log.d(TAG, cache);
                        parseAlertsXbox(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseAlertsXbox(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("alerts_modified_xb1", connection.getLastModified());
                mEditor.putString("alerts_cache_xb1", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during alerts alarm");
            e.printStackTrace();
        }
    }

    private void parseAlertsXbox(String response) {
        if (response.length() < 15) {
            mAlertSuccess = true;
            Log.i(TAG, "Alert response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("alert_completed_ids", ""));
        String[] rawAlerts = response.split("\\n");
        Boolean mNew = false;
        String gcmAlerts = "";
        for (String rawAlert : rawAlerts) {
            if (rawAlert.split("\\|").length != 11)
                continue;
            Alert alert = new Alert(rawAlert);
            gcmAlerts += TextUtils.join("|", new String[]{alert.getId(), alert.getNode(), alert.getRegion(),
                    alert.getMission(), alert.getFaction(), String.valueOf(alert.getActivation()),
                    String.valueOf(alert.getExpiry()), TextUtils.join(" - ", alert.getRewards())}) + "\n";
            mNew = false;
            if (!ids.contains(alert.getId())) {
                mNew = true;
                ids.add(alert.getId());
            }
            Log.d(TAG, "found alert: " + alert.getNode() + " - " + TextUtils.join(" - ", alert.getRewards())
                    + " - new: " + mNew);
            if (alert.getExpiry() < System.currentTimeMillis() / 1000) {
                Log.d(TAG, "alert: " + alert.getNode() + " has expired, ignore");
                continue;
            }
            if (completedIds.contains(alert.getId())) {
                Log.i(TAG, "alert: " + alert.getNode() + " has been completed, ignore");
                continue;
            }
            if (!mXbox) {
                Log.i(TAG, "Xbox not enabled, aborting.");
                continue;
            }
            if (isAlertFiltered(alert)) {
                Log.d(TAG, "accepted alert: " + alert.getNode());
                if (mNew)
                    mVibrate = true;
                if (mForceUpdateTime == 0)
                    mForceUpdateTime = alert.getExpiry();
                else {
                    if (alert.getExpiry() < mForceUpdateTime) {
                        mForceUpdateTime = alert.getExpiry();
                    }
                }
                mNotifications.add(String.format("X1 [%s]: <b>%s</b>",
                        DateFormat.getTimeFormat(mContext).format(new Date(alert.getExpiry()*1000)), TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_ALERTS_XBOX, gcmAlerts);
        mEditor.commit();
        mAlertSuccess = true;
    }

    private void doInvasions() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/invasion_mini.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("invasion_modified", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for invasions: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("invasion_cache", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for invasion");
                        Log.d(TAG, cache);
                        parseInvasions(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseInvasions(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("invasion_modified", connection.getLastModified());
                mEditor.putString("invasion_cache", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during invasions alarm");
            e.printStackTrace();
        }
    }

    private void parseInvasions(String response) {
        Log.d(TAG, response.length() + "");
        if (response.length() < 15) {
            mInvasionSuccess = true;
            Log.i(TAG, "Invasion response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("invasion_completed_ids", ""));
        String[] rawInvasions = response.split("\\n");
        String gcmInvasions = "";
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            if (rawInvasion.split("\\|").length != 11)
                continue;
            InvasionMini invasion = new InvasionMini(rawInvasion);
            gcmInvasions += TextUtils.join("|", new String[]{invasion.getId(), invasion.getNode(), invasion.getRegion(),
                    invasion.getInvadingFaction(), invasion.getInvadingType(), invasion.getInvadingReward(),
                    invasion.getDefendingFaction(), invasion.getDefendingType(), invasion.getDefendingReward()}) + "\n";
            mNew = false;
            if (!ids.contains(invasion.getId())) {
                mNew = true;
                ids.add(invasion.getId());
            }
            Log.d(TAG, "found invasion: " + invasion.getNode() + " - " + TextUtils.join(" - ", invasion.getRewards())
                    + " - new: " + mNew);
            if (completedIds.contains(invasion.getId())) {
                Log.i(TAG, "invasion: " + invasion.getNode() + " has been marked completed, ignore");
                continue;
            }
            if (!mPc) {
                Log.i(TAG, "PC not enabled, aborting.");
                continue;
            }
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText("PC"));
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText("PC"));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_INVASIONS, gcmInvasions);
        mEditor.commit();
        mInvasionSuccess = true;
    }

    private void doInvasionsPS4() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/ps4/invasion_mini.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("invasion_modified_ps4", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for invasions: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("invasion_cache_ps4", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for invasion");
                        Log.d(TAG, cache);
                        parseInvasionsPS4(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseInvasionsPS4(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("invasion_modified_ps4", connection.getLastModified());
                mEditor.putString("invasion_cache_ps4", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during invasions alarm");
            e.printStackTrace();
        }
    }

    private void parseInvasionsPS4(String response) {
        Log.d(TAG, response.length() + "");
        if (response.length() < 15) {
            mInvasionSuccess = true;
            Log.i(TAG, "Invasion response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("invasion_completed_ids", ""));
        String[] rawInvasions = response.split("\\n");
        String gcmInvasions = "";
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            if (rawInvasion.split("\\|").length != 11)
                continue;
            InvasionMini invasion = new InvasionMini(rawInvasion);
            gcmInvasions += TextUtils.join("|", new String[]{invasion.getId(), invasion.getNode(), invasion.getRegion(),
                    invasion.getInvadingFaction(), invasion.getInvadingType(), invasion.getInvadingReward(),
                    invasion.getDefendingFaction(), invasion.getDefendingType(), invasion.getDefendingReward()}) + "\n";
            mNew = false;
            if (!ids.contains(invasion.getId())) {
                mNew = true;
                ids.add(invasion.getId());
            }
            Log.d(TAG, "found invasion: " + invasion.getNode() + " - " + TextUtils.join(" - ", invasion.getRewards())
                    + " - new: " + mNew);
            if (completedIds.contains(invasion.getId())) {
                Log.i(TAG, "invasion: " + invasion.getNode() + " has been marked completed, ignore");
                continue;
            }
            if (!mPs4) {
                Log.i(TAG, "PS4 not enabled, aborting.");
                continue;
            }
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText("PS4"));
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText("PS4"));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_INVASIONS_PS4, gcmInvasions);
        mEditor.commit();
        mInvasionSuccess = true;
    }

    private void doInvasionsXbox() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/xbox/invasion_mini.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mHttpPreferences.getLong("invasion_modified_xb1", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "we received something other than 201 for invasions: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mHttpPreferences.getString("invasion_cache_xb1", "");
                        Log.d(TAG, "we received NOT_MODIFIED, processing cache for invasion");
                        Log.d(TAG, cache);
                        parseInvasionsXbox(cache);
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseInvasionsXbox(resp);
                SharedPreferences.Editor mEditor = mHttpPreferences.edit();
                mEditor.putLong("invasion_modified_xb1", connection.getLastModified());
                mEditor.putString("invasion_cache_xb1", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during invasions alarm");
            e.printStackTrace();
        }
    }

    private void parseInvasionsXbox(String response) {
        Log.d(TAG, response.length() + "");
        if (response.length() < 15) {
            mInvasionSuccess = true;
            Log.i(TAG, "Invasion response < 15, tagging success and continuing");
            return;
        }
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        Log.d(TAG, mPreferences.getString("invasion_completed_ids", ""));
        String[] rawInvasions = response.split("\\n");
        String gcmInvasions = "";
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            if (rawInvasion.split("\\|").length != 11)
                continue;
            InvasionMini invasion = new InvasionMini(rawInvasion);
            gcmInvasions += TextUtils.join("|", new String[]{invasion.getId(), invasion.getNode(), invasion.getRegion(),
                    invasion.getInvadingFaction(), invasion.getInvadingType(), invasion.getInvadingReward(),
                    invasion.getDefendingFaction(), invasion.getDefendingType(), invasion.getDefendingReward()}) + "\n";
            mNew = false;
            if (!ids.contains(invasion.getId())) {
                mNew = true;
                ids.add(invasion.getId());
            }
            Log.d(TAG, "found invasion: " + invasion.getNode() + " - " + TextUtils.join(" - ", invasion.getRewards())
                    + " - new: " + mNew);
            if (completedIds.contains(invasion.getId())) {
                Log.i(TAG, "invasion: " + invasion.getNode() + " has been marked completed, ignore");
                continue;
            }
            if (!mXbox) {
                Log.i(TAG, "Xbox not enabled, aborting.");
                continue;
            }
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText("X1"));
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText("X1"));
                continue;
            }
        }
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString(Constants.PREF_GCM_INVASIONS_XBOX, gcmInvasions);
        mEditor.commit();
        mInvasionSuccess = true;
    }

    private static byte[] readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    private void addNotifications() {
        int size = mNotifications.size();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "dismissible:" + mOngoing);
        int id_text = R.string.notification_filter_count;
        if (mAllowAlerts && !mAllowInvasions)
            id_text = R.string.notification_filter_count_alerts;
        else if (mAllowInvasions && !mAllowAlerts)
            id_text = R.string.notification_filter_count_invasions;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(mContext.getString(R.string.notification_title))
                .setContentText(String.format(mContext.getString(id_text), mNotifications.size()))
                .setOngoing(mOngoing)
                .setAutoCancel(!mOngoing);
        if (!mAlertSuccess || !mInvasionSuccess) {
            //mBuilder.setContentText("Connection error");
            return;
        } else {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            if (size > 5)
                style.setSummaryText(String.format(mContext.getString(R.string.notification_more), size - 5));
            for (int i = 0; i < 5 && i < size; i++) {
                String[] lines = mNotifications.get(i).split("<br/>");
                for (String line : lines) {
                    style.addLine(Html.fromHtml(line));
                }
            }
            if (size > 0) {
                mBuilder.setNumber(size);
                mBuilder.setStyle(style);
            }
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("drawer_position", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        if (mVibrate) {
            int defaults = 0;
            if (mEnableVibrate) {
                mBuilder.setVibrate(new long[]{0, 300});
            }
            if (mEnableLED) {
                if (mLedColour == Color.WHITE)
                    defaults |= Notification.DEFAULT_LIGHTS;
                else {
                    mBuilder.setLights(mLedColour, 300, 700);
                }
            }
            mBuilder.setSound(
                    Uri.parse(mPreferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())),
                    mStreamType);
            mBuilder.setDefaults(defaults);
        } else {
            if (!mOngoing && !mForceUpdate) {
                Log.d(TAG, "we abandoning this, since we've set dismissible and we aren't forcing");
                return;
            }
        }
        Notification notification = mBuilder.build();
        if (mVibrate && mInsistent) {
            notification.flags |= Notification.FLAG_INSISTENT;
        }
        Intent alarmIntent = new Intent(mContext, NotificationsUpdateReceiver.class);
        PendingIntent pendingForceIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendingForceIntent);
        if (!mEmptyIcon && mNotifications.size() == 0) {
            mNotificationManager.cancel(1);
        } else {
            mNotificationManager.notify(1, notification);
            mForceUpdateTime = mForceUpdateTime - (System.currentTimeMillis() / 1000);
            Log.d(TAG, "force update time: " + mForceUpdateTime);
            if (mForceUpdateTime > 0) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + (mForceUpdateTime * 1000) + (10 * 1000),
                            pendingForceIntent);
                } else {
                    ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setWindow(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + (mForceUpdateTime * 1000) + 1000, 30 * 1000,
                            pendingForceIntent);
                }
                Log.d(TAG, "we've set a force update in " + mForceUpdateTime);
            }
        }
    }

    private Boolean isAlertFiltered(Alert alert) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered && !mTypeFiltered && !mCustomFilered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(alert.getRegion()))
            return false;
        if (mTypeFiltered && !mTypeFilters.contains(alert.getMission().toLowerCase()))
            return false;

        if (!mItemFiltered && !mCreditFiltered && !mCustomFilered)
            return true;

        for (String reward : alert.getRewardsStripped()) {
            if (reward.contains("cr")) {
                if (mCreditFiltered) {
                    int credits = Integer.parseInt(reward.replace(",", "").replace("cr", ""));
                    if (credits >= mCreditFilter)
                        return true;
                }
            } else {
                if (mItemFiltered) {
                    if (mItemFilters.contains(reward.replace(" Blueprint", "")))
                        return true;
                    if (reward.contains("Arcane ")) {
                        if (isArcaneFiltered(reward)) {
                            return true;
                        }
                    }
                }
                if (mCustomFilered) {
                    for (String filter : mCustomFilters) {
                        if (reward.toLowerCase().contains(filter.toLowerCase()))
                            return true;
                    }
                }
                if (mUnknownFiltered) {
                    if (!isItemKnown(reward.replace(" Blueprint", ""))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean isInvasionFiltered(InvasionMini invasion) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered && !mTypeFiltered && !mCustomFilered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(invasion.getRegion()))
            return false;
        if (mTypeFiltered) {
            if (!mTypeFilters.contains(invasion.getDefendingType().toLowerCase())) {
                if (!mTypeFilters.contains(invasion.getInvadingType().toLowerCase())) {
                    return false;
                }
            }
        }

        if (!mItemFiltered && !mCreditFiltered && !mCustomFilered)
            return true;

        for (String reward : invasion.getRewards()) {
            if (reward.contains("cr")) {
                if (mCreditFiltered) {
                    int credits = Integer.parseInt(reward.replace(",", "").replace("cr", ""));
                    if (credits >= mCreditFilter)
                        return true;
                }
            } else {
                if (mItemFiltered) {
                    if (mItemFilters.contains(reward.replace(" Blueprint", "")))
                        return true;
                }
                if (mCustomFilered) {
                    for (String filter : mCustomFilters) {
                        if (reward.toLowerCase().contains(filter.toLowerCase()))
                            return true;
                    }
                }
                if (mUnknownFiltered) {
                    if (!isItemKnown(reward.replace(" Blueprint", ""))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Boolean isArcaneFiltered(String raw) {
        String name = raw.split(" ")[1];
        for (String filter : mItemFilters) {
            if (filter.contains(name) && filter.contains("Helmet"))
                return true;
        }
        return false;
    }

    private Boolean isItemKnown(String reward) {
        for (String item : mItems) {
            if (reward.toLowerCase().contains(item.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
