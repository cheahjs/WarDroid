package com.deathsnacks.wardroid.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.MainActivity;

/**
 * Created by Admin on 22/02/14.
 */
public class NotificationsUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationsUpdateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "We are now forcing an update of notifications, because expiry and stuff.");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Constants.PREF_PUSH, false))
            (new GcmBroadcastReceiver()).onReceive(context, intent.putExtra("force", true));
        else
            (new PollingAlarmReceiver()).onReceive(context, intent.putExtra("force", true));
    }
}
