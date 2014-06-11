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
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.deathsnacks.wardroid.Constants;
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
    private List<String> mCustomFilters;
    private Boolean mCustomFilered;
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
    private Boolean mOngoing;
    private long mForceUpdateTime;
    private int mStreamType;
    private String mAlerts;
    private String mInvasions;
    private String mAlertsPS4;
    private String mInvasionsPS4;
    private Boolean mAllowAlerts;
    private Boolean mAllowInvasions;
    private int mLedColour;
    private boolean mForce;
    private boolean mPc;
    private boolean mPs4;
    private boolean mPcUpdate;

    @Override
    public void onReceive(Context context, Intent intent) {
        gcm = GoogleCloudMessaging.getInstance(context);
        Log.d(TAG, "GCM BROADCAST RECEIVED!");
        String messageType = gcm.getMessageType(intent);
        if ((messageType != null && messageType.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE))
                || intent.getBooleanExtra("mForce", false)) {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Log.i(TAG, "We've received a gcm message.");
            String alerts = intent.getStringExtra("alerts");
            String invasions = intent.getStringExtra("invasions");
            String alertsPs4 = intent.getStringExtra("alerts_ps4");
            String invasionsPs4 = intent.getStringExtra("invasions_ps4");
            mForce = intent.getBooleanExtra("mForce", false);
            Log.d(TAG, "gcm alerts:" + alerts);
            Log.d(TAG, "gcm invasions:" + invasions);
            Log.d(TAG, "gcm ps4 alerts:" + alertsPs4);
            Log.d(TAG, "gcm ps4 invasions:" + invasionsPs4);
            if ((alerts == null || invasions == null) && (alertsPs4 != null && invasionsPs4 != null)) {
                Log.i(TAG, "PS4 update, grabbing PC cache");
                mPcUpdate = false;
                alerts = mPreferences.getString(Constants.PREF_GCM_ALERTS, "");
                invasions = mPreferences.getString(Constants.PREF_GCM_INVASIONS, "");
            } else if ((alerts != null && invasions != null) && (alertsPs4 == null || invasionsPs4 == null)) {
                Log.i(TAG, "PC update, grabbing PS4 cache");
                alertsPs4 = mPreferences.getString(Constants.PREF_GCM_ALERTS_PS4, "");
                invasionsPs4 = mPreferences.getString(Constants.PREF_GCM_INVASIONS_PS4, "");
                mPcUpdate = true;
            }
            if ((alerts == null || invasions == null) && (alertsPs4 == null || invasionsPs4 == null) && !mForce) {
                Log.w(TAG, "Somehow gcm data is null, and we aren't forcing an update. ABORT ABORT ABORT!");
                return;
            }
            String platformPref = mPreferences.getString(Constants.PREF_PLATFORM_NOTIFICATIONS, "pc|ps4");
            mPc = platformPref.contains("pc");
            mPs4 = platformPref.contains("ps4");
            if (mForce && (alerts == null || invasions == null) && (alertsPs4 == null || invasionsPs4 == null)) {
                Log.d(TAG, "We are forcing, so we are grabbing cached stuff.");
                alerts = mPreferences.getString(Constants.PREF_GCM_ALERTS, "");
                invasions = mPreferences.getString(Constants.PREF_GCM_INVASIONS, "");
                alertsPs4 = mPreferences.getString(Constants.PREF_GCM_ALERTS_PS4, "");
                invasionsPs4 = mPreferences.getString(Constants.PREF_GCM_INVASIONS_PS4, "");
            }
            mAlerts = alerts;
            mInvasions = invasions;
            mAlertsPS4 = alertsPs4;
            mInvasionsPS4 = invasionsPs4;
            mContext = context;
            SharedPreferences.Editor editor = mPreferences.edit();
            if (!mForce) {
                if (mPcUpdate) {
                    editor.putString(Constants.PREF_GCM_ALERTS, alerts);
                    editor.putString(Constants.PREF_GCM_INVASIONS, invasions);
                } else {
                    editor.putString(Constants.PREF_GCM_ALERTS_PS4, alertsPs4);
                    editor.putString(Constants.PREF_GCM_INVASIONS_PS4, invasionsPs4);
                }
            }
            mVibrate = false;
            mNotifications = new ArrayList<String>();
            if (!mPreferences.getBoolean(Constants.PREF_ALERT_ENABLED, false) || !mPreferences.getBoolean(Constants.PREF_PUSH, false)) {
                Log.i(TAG, "Aborting notification update since we don't have any of these enabled.");
                return;
            }
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

            mForceUpdateTime = 0;
            if (mAllowAlerts) {
                parseAlerts(alerts);
                parseAlertsPS4(alertsPs4);
            } else {
                mAlertSuccess = true;
            }
            if (mAllowInvasions) {
                parseInvasions(invasions);
                parseInvasionsPS4(invasionsPs4);
            } else {
                mInvasionSuccess = true;
            }
            if (mAllowAlerts || mAllowInvasions)
                addNotifications();
            else {
                if (!mAllowAlerts && !mAllowInvasions) {
                    Log.d(TAG, "removing gcm notifications since we don't have anything");
                    mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(1);
                }
            }
        }
    }

    private void parseAlerts(String response) {
        if (response.length() < 15) {
            mAlertSuccess = true;
            Log.i(TAG, "Alert response < 15, tagging success and continuing");
            Log.i(TAG, response);
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
            if (!mPc) {
                Log.i(TAG, "PC notifications not enabled, ignoring");
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
                mNotifications.add(String.format("Alert (PC): <b>%s</b>",
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
            if (!mPc) {
                Log.i(TAG, "PC notifications not enabled, ignoring");
                continue;
            }
            String[] rewards = invasion.getRewards();
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText("PC"));
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText("PC"));
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.commit();
        mInvasionSuccess = true;
    }

    private void parseAlertsPS4(String response) {
        if (response.length() < 15) {
            mAlertSuccess = true;
            Log.i(TAG, "Alert response < 15, tagging success and continuing");
            Log.i(TAG, response);
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
            if (!mPs4) {
                Log.i(TAG, "PS4 notifications not enabled, ignoring");
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
                mNotifications.add(String.format("Alert (PS4): <b>%s</b>",
                        TextUtils.join(" - ", alert.getRewards())));
                continue;
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.commit();
        mAlertSuccess = true;
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
            if (!mPs4) {
                Log.i(TAG, "PS4 notifications not enabled, ignoring");
                continue;
            }
            String[] rewards = invasion.getRewards();
            if (isInvasionFiltered(invasion)) {
                Log.d(TAG, "Accepted invasion: " + invasion.getNotificationText("PS4"));
                if (mNew)
                    mVibrate = true;
                mNotifications.add(invasion.getNotificationText("PS4"));
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
                style.addLine(Html.fromHtml(mNotifications.get(i)));
            }
            if (size > 0) {
                mBuilder.setNumber(size);
                mBuilder.setStyle(style);
            }
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("drawer_position", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            if (!mOngoing && !mForce) {
                Log.d(TAG, "we abandoning this, since we've set dismissible and we aren't forcing");
                return;
            }
        }
        Notification notification = mBuilder.build();
        if (mVibrate && mInsistent) {
            notification.flags |= Notification.FLAG_INSISTENT;
        }
        Intent alarmIntent = new Intent(mContext, NotificationsUpdateReceiver.class);
        alarmIntent.putExtra("gcm", true);
        alarmIntent.putExtra("alerts", mAlerts);
        alarmIntent.putExtra("invasions", mInvasions);
        alarmIntent.putExtra("alerts_ps4", mAlertsPS4);
        alarmIntent.putExtra("invasions_ps4", mInvasionsPS4);
        PendingIntent pendingForceIntent = PendingIntent.getBroadcast(mContext, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        ((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).cancel(pendingForceIntent);
        if (!mEmptyIcon && mNotifications.size() == 0) {
            mNotificationManager.cancel(1);
        } else {
            mNotificationManager.notify(1, notification);
            mForceUpdateTime = mForceUpdateTime - (System.currentTimeMillis() / 1000);
            Log.d(TAG, "mForce update time: " + mForceUpdateTime);
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
                Log.d(TAG, "we've set a mForce update in " + mForceUpdateTime);
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
            }
        }
        return false;
    }

    private Boolean isInvasionFiltered(Invasion invasion) {
        if (!mItemFiltered && !mCreditFiltered && !mPlanetFiltered && !mTypeFiltered && !mCustomFilered)
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
}
