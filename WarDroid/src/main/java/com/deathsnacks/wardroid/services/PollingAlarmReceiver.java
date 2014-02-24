package com.deathsnacks.wardroid.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
public class PollingAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "PollingAlarmReceiver";
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
    private List<String> mTypeFilters;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        client = new OkHttpClient();
        mVibrate = false;
        mNotifications = new ArrayList<String>();
        Log.d(TAG, "Received broadcast for alarm, starting polling.");
        if (mPreferences.getBoolean("push", false) && intent != null) {
            Log.i(TAG, "We are abandoning the alarm, since push is on and this isn't forced.");
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                    context, 0, new Intent(context, PollingAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
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

            mTypeFiltered = mPreferences.getBoolean("type_enabled", false);
            String lowerCase = mPreferences.getString("type_filters", "").toLowerCase();
            mTypeFilters = new ArrayList<String>(Arrays.asList(
                    PreferenceUtils.fromPersistedPreferenceValue(lowerCase)));
            Log.d(TAG, lowerCase);

            mInsistent = mPreferences.getBoolean("insistent", false);
            mEnableVibrate = mPreferences.getBoolean("vibrate", true);
            mEnableLED = mPreferences.getBoolean("light", true);

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

            mForceUpdateTime = 0;

            mForceUpdate = intent != null && intent.getBooleanExtra("force", false);

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
                    Log.d(TAG, "we received something other than 201 for alerts: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mPreferences.getString("alerts_cache", "");
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
                mEditor = mPreferences.edit();
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
                mNotifications.add(String.format("Alert: <b>%s</b>",
                        TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString("gcm_alerts", gcmAlerts);
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
                    Log.d(TAG, "we received something other than 201 for invasions: " + connection.getResponseCode());
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED || mForceUpdate) {
                        String cache = mPreferences.getString("invasion_cache", "");
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
                mEditor = mPreferences.edit();
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
            String[] rewards = invasion.getRewards();
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText());
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText());
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.putString("gcm_invasions", gcmInvasions);
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
                .setContentTitle(mContext.getString(R.string.notification_title))
                .setContentText(String.format(mContext.getString(R.string.notification_filter_count), mNotifications.size()))
                .setOngoing(true);
        if (!mAlertSuccess || !mInvasionSuccess) {
            //mBuilder.setContentText("Connection error");
            return;
        } else {
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            if (size > 5)
                style.setSummaryText(String.format(mContext.getString(R.string.notification_more), size - 5));
            for (int i = 0; i < 5 && i < size; i++) {
                style.addLine(Html.fromHtml(mNotifications.get(i)));
            }
            if (size > 0) {
                mBuilder.setNumber(size);
                mBuilder.setStyle(style);
            }
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("drawer_position", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        if (mVibrate) {
            int defaults = 0;
            if (mEnableVibrate) {
                mBuilder.setVibrate(new long[]{0, 300});
            }
            if (mEnableLED) {
                defaults |= Notification.DEFAULT_LIGHTS;
            }
            mBuilder.setSound(
                    Uri.parse(mPreferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())),
                    mStreamType);
            mBuilder.setDefaults(defaults);
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
                            SystemClock.elapsedRealtime() + (mForceUpdateTime * 1000) + 1000, 2 * 60 * 1000,
                            pendingForceIntent);
                }
                Log.d(TAG, "we've set a force update in " + mForceUpdateTime);
            }
        }
    }

    private Boolean isAlertFiltered(Alert alert) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered && !mTypeFiltered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(alert.getRegion()))
            return false;
        if (mTypeFiltered && !mTypeFilters.contains(alert.getMission().toLowerCase()))
            return false;

        if (!mItemFiltered && !mCreditFiltered)
            return true;

        for (String reward : alert.getRewards()) {
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
            }
        }
        return false;
    }

    private Boolean isInvasionFiltered(InvasionMini invasion) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered && !mTypeFiltered)
            return true;
        if (mPlanetFiltered && !mPlanetFilters.contains(invasion.getRegion()))
            return false;
        if (mTypeFiltered) {
            Log.d(TAG, invasion.getDefendingType().toLowerCase() + " " + invasion.getInvadingType().toLowerCase());
            if (!mTypeFilters.contains(invasion.getDefendingType().toLowerCase())) {
                if (!mTypeFilters.contains(invasion.getInvadingType().toLowerCase())) {
                    return false;
                }
            }
        }

        if (!mItemFiltered && !mCreditFiltered)
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
            }
        }
        return false;
    }
}
