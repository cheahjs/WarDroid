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
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.MainActivity;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.gcmclasses.Alert;
import com.deathsnacks.wardroid.utils.gcmclasses.Invasion;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/02/14.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GcmBroadcastReceiver";
    private GoogleCloudMessaging gcm;
    private Context mContext;
    private SharedPreferences mPreferences;
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
    private Boolean mAlertSuccess;
    private Boolean mInvasionSuccess;
    private Boolean mEnableVibrate;
    private Boolean mEnableLED;
    private Boolean mInsistent;
    private Boolean mEmptyIcon;
    private long mForceUpdateTime;
    private int mStreamType;
    private String mAlerts;
    private String mInvasions;

    @Override
    public void onReceive(Context context, Intent intent) {
        gcm = GoogleCloudMessaging.getInstance(context);
        Log.d(TAG, "GCM BROADCAST RECEIVED!");
        String messageType = gcm.getMessageType(intent);
        if ((messageType != null && messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE))
                || intent.getBooleanExtra("gcm", false)) {
            Log.i(TAG, "We've received a gcm message.");
            String alerts = intent.getStringExtra("alerts");
            String invasions = intent.getStringExtra("invasions");
            boolean force = intent.getBooleanExtra("force", false) || intent.getBooleanExtra("tickle", false);
            Log.d(TAG, alerts);
            Log.d(TAG, invasions);
            if ((alerts == null || invasions == null) && !force) {
                Log.w(TAG, "Somehow gcm data is null, and we aren't forcing an update. ABORT ABORT ABORT!");
                return;
            }
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (force) {
                alerts = mPreferences.getString("gcm_alerts", "");
                invasions = mPreferences.getString("gcm_invasions", "");
            }
            mAlerts = alerts;
            mInvasions = invasions;
            mContext = context;
            SharedPreferences.Editor editor = mPreferences.edit();
            if (!force) {
                editor.putString("gcm_alerts", alerts);
                editor.putString("gcm_invasions", invasions);
            }
            mVibrate = false;
            mNotifications = new ArrayList<String>();
            if (!mPreferences.getBoolean("alert_enabled", false) || !mPreferences.getBoolean("push", false)) {
                Log.i(TAG, "Aborting notification update since we don't have any of these enabled.");
                return;
            }
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
            parseAlerts(alerts);
            parseInvasions(invasions);
            addNotifications();
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
        for (String rawAlert : rawAlerts) {
            if (rawAlert.split("\\|").length != 8)
                continue;
            Alert alert = new Alert(rawAlert);
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
        mEditor.commit();
        mAlertSuccess = true;
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
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            if (rawInvasion.split("\\|").length != 9)
                continue;
            Invasion invasion = new Invasion(rawInvasion);
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
        mEditor.commit();
        mInvasionSuccess = true;
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
        alarmIntent.putExtra("gcm", true);
        alarmIntent.putExtra("alerts", mAlerts);
        alarmIntent.putExtra("invasions", mInvasions);
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

    private Boolean isInvasionFiltered(Invasion invasion) {
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
