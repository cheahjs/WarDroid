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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 03/02/14.
 */
public class PollingAlarmManager extends BroadcastReceiver {
    private Context mContext;
    private SharedPreferences mPreferences;
    private OkHttpClient client;
    private SharedPreferences.Editor mEditor;
    private List<String> mNotifications;
    private NotificationManager mNotificationManager;
    private Boolean mVibrate;
    private List<String> mItemFilters;
    private List<String> mPlanetFilters;
    private int mCreditFilter;
    private Boolean mItemFiltered;
    private Boolean mPlanetFiltered;
    private Boolean mCreditFiltered;
    private PowerManager.WakeLock mWakeLock;
    private Boolean mAlertSuccess;
    private Boolean mInvasionSuccess;
    private Boolean mEnableVibrate;
    private Boolean mEnableLED;
    private Boolean mInsistent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        client = new OkHttpClient();
        mVibrate = false;
        mNotifications = new ArrayList<String>();
        Log.d("deathsnacks", "Received broadcast for alarm, starting polling.");
        if (mPreferences.getBoolean("alert_enabled", false)) {
            ArrayList<String> aura = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("aura_filters", ""))));
            ArrayList<String> bp = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("bp_filters", ""))));
            ArrayList<String> mod = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("mod_filters", ""))));
            ArrayList<String> resource = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("resource_filters", ""))));
            mItemFilters = new ArrayList<String>();
            mItemFilters.addAll(aura);
            mItemFilters.addAll(bp);
            mItemFilters.addAll(mod);
            mItemFilters.addAll(resource);
            mItemFiltered = mPreferences.getBoolean("filter_enabled", false);

            mPlanetFiltered = mPreferences.getBoolean("planet_enabled", false);
            mPlanetFilters = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("planet_filters", ""))));

            mCreditFiltered = mPreferences.getBoolean("credit_enabled", false);
            mCreditFilter = mPreferences.getInt("credit_filter", 0);

            mInsistent = mPreferences.getBoolean("insistent", false);
            mEnableVibrate = mPreferences.getBoolean("vibrate", true);
            mEnableLED = mPreferences.getBoolean("light", true);

            mAlertSuccess = false;
            mInvasionSuccess = false;

            (new RefreshTask()).execute();
            mWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "WarDroid Notifications");
            mWakeLock.acquire();
        } else {
            Log.d("deathsnacks", "cancelling alarm since we didn't enable alarm");
            mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
            ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                    context, 0, new Intent(context, PollingAlarmManager.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
    }

    public class RefreshTask extends AsyncTask<Void, Void, Boolean> {

        public RefreshTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            doAlerts();
            doInvasions();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
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
            long mod = mPreferences.getLong("alerts_modified", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("deathsnacks", "we received something other than 201 for alerts: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        String cache = mPreferences.getString("alerts_cache", "");
                        if (cache.length() > 2) {
                            Log.d("deathsnacks", "we received NOT_MODIFIED, processing cache for alerts");
                            Log.d("deathsnacks", cache);
                            parseAlerts(cache);
                        }
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseAlerts(resp);
                mEditor = mPreferences.edit();
                mEditor.putLong("alerts_modified", connection.getLastModified());
                mEditor.putString("alerts_cache", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e("deathsnacks", "Error occurred during alerts alarm");
            e.printStackTrace();
        }
    }

    private void parseAlerts(String response) {
        if (response.length() < 10)
            return;
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        Log.d("deathsnacks", mPreferences.getString("alert_completed_ids", ""));
        String[] rawAlerts = response.split("\\n");
        Boolean mNew = false;
        for (String rawAlert : rawAlerts) {
            if (rawAlert.split("\\|").length != 11)
                continue;
            Alert alert = new Alert(rawAlert);
            mNew = false;
            if (!ids.contains(alert.getId())) {
                mNew = true;
                ids.add(alert.getId());
            }
            Log.d("deathsnacks", "found alert: " + alert.getNode() + " - " + TextUtils.join(" - ", alert.getRewards())
                    + " - new: " + mNew);
            if (alert.getExpiry() < System.currentTimeMillis()/1000) {
                Log.d("deathsnacks", "alert: " + alert.getNode() + " has expired, ignore");
                continue;
            }
            if (completedIds.contains(alert.getId())) {
                Log.i("deathsnacks", "alert: " + alert.getNode() + " has been completed, ignore");
                continue;
            }
            if (isAlertFiltered(alert)) {
                Log.d("deathsnacks", "accepted alert: " + alert.getNode());
                if (mNew)
                    mVibrate = true;
                mNotifications.add(String.format("Alert: <b>%s</b>",
                        TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.commit();
        mAlertSuccess = true;
    }

    private void doInvasions() {
        try {
            URL url = new URL("http://deathsnacks.com/wf/data/invasion_mini.txt");
            HttpURLConnection connection = client.open(url);
            System.setProperty("http.agent", "");
            connection.setRequestProperty("User-Agent", "WarDroid/Android/");
            long mod = mPreferences.getLong("invasion_modified", 0);
            if (mod != 0) {
                connection.setIfModifiedSince(mod);
            }
            InputStream in = null;
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("deathsnacks", "we received something other than 201 for invasions: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                        String cache = mPreferences.getString("invasion_cache", "");
                        if (cache.length() > 2) {
                            Log.d("deathsnacks", "we received NOT_MODIFIED, processing cache for invasion");
                            Log.d("deathsnacks", cache);
                            parseInvasions(cache);
                        }
                    }
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseInvasions(resp);
                mEditor = mPreferences.edit();
                mEditor.putLong("invasion_modified", connection.getLastModified());
                mEditor.putString("invasion_cache", resp);
                mEditor.commit();
            } finally {
                if (in != null) in.close();
            }
        } catch (Exception e) {
            Log.e("deathsnacks", "Error occurred during invasions alarm");
            e.printStackTrace();
        }
    }

    private void parseInvasions(String response) {
        if (response.length() < 10)
            return;
        List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_ids", ""))));
        List<String> completedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        Log.d("deathsnacks", mPreferences.getString("invasion_completed_ids", ""));
        String[] rawInvasions = response.split("\\n");
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            if (rawInvasion.split("\\|").length != 11)
                continue;
            InvasionMini invasion = new InvasionMini(rawInvasion);
            mNew = false;
            if (!ids.contains(invasion.getId())) {
                mNew = true;
                ids.add(invasion.getId());
            }
            Log.d("deathsnacks", "found invasion: " + invasion.getNode() + " - " + TextUtils.join(" - ", invasion.getRewards())
                    + " - new: " + mNew);
            if (completedIds.contains(invasion.getId())) {
                Log.i("deathsnacks", "invasion: " + invasion.getNode() + " has been marked completed, ignore");
                continue;
            }
            String[] rewards = invasion.getRewards();
            if (isInvasionFiltered(invasion)) {
                Log.d("deathsnacks", "Accepted invasion: " + invasion.getNotificationText());
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText());
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Warframe Tracker")
                .setContentText(String.format("%d filtered alerts/invasions", mNotifications.size()))
                .setOngoing(true);
        if (!mAlertSuccess || !mInvasionSuccess) {
            //mBuilder.setContentText("Connection error");
            return;
        } else {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            if (size > 5)
                style.setSummaryText(String.format("+%s more", size - 5));
            for (int i = 0; i < 5 && i < size; i++) {
                style.addLine(Html.fromHtml(mNotifications.get(i)));
            }
            if (size > 0) {
                mBuilder.setNumber(size);
                mBuilder.setStyle(style);
            }
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("drawer_position", 2);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        if (mVibrate) {
            int defaults = 0;
            if (mEnableVibrate) {
                mBuilder.setVibrate(new long[] {0, 300});
            }
            if (mEnableLED) {
                defaults |= Notification.DEFAULT_LIGHTS;
            }
            mBuilder.setSound(Uri.parse(mPreferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())));
            mBuilder.setDefaults(defaults);
        }
        Notification notification = mBuilder.build();
        if (mVibrate && mInsistent) {
            notification.flags |= Notification.FLAG_INSISTENT;
        }
        mNotificationManager.notify(1, notification);
    }

    private Boolean isAlertFiltered(Alert alert) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(alert.getRegion()))
            return false;
        for (String reward : alert.getRewards()) {
            if (reward.contains("cr")) {
                if (mCreditFiltered) {
                    int credits =  Integer.parseInt(reward.replace(",", "").replace("cr", ""));
                    if (credits >= mCreditFilter)
                        return true;
                }
            } else {
                if (mItemFiltered) {
                    if (mItemFilters.contains(reward.replace(" Blueprint", "")))
                        return true;
                }
            }
        }
        return false;
    }

    private Boolean isInvasionFiltered(InvasionMini invasion) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(invasion.getRegion()))
            return false;
        for (String reward : invasion.getRewards()) {
            if (reward.contains("cr")) {
                if (mCreditFiltered) {
                    int credits =  Integer.parseInt(reward.replace(",", "").replace("cr", ""));
                    if (credits >= mCreditFilter)
                        return true;
                }
            } else {
                if (mItemFiltered) {
                    if (mItemFilters.contains(reward.replace(" Blueprint", "")))
                        return true;
                }
            }
        }
        return false;
    }
}
