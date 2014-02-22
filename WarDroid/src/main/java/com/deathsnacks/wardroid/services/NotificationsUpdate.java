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
public class NotificationsUpdate extends BroadcastReceiver {
    private static final String TAG = "NotificationsUpdate";

    @Override
    public void onReceive(Context context, Intent intent) {
        Context mContext = context;
        String[] mNotifications = intent.getStringArrayExtra("notifications");
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!mPreferences.getBoolean("alert_enabled", false)) {
            Log.d(TAG, "cancelling alarm since we didn't enable alarm");
            mNotificationManager.cancel(1);
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                    context, 0, new Intent(context, NotificationsUpdate.class), PendingIntent.FLAG_UPDATE_CURRENT));
        }
        boolean mEmptyIcon = mPreferences.getBoolean("empty_enabled", true);
        int size = mNotifications.length;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(mContext.getString(R.string.notification_title))
                .setContentText(String.format(mContext.getString(R.string.notification_filter_count), mNotifications.length))
                .setOngoing(true);
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            if (size > 5)
                style.setSummaryText(String.format(mContext.getString(R.string.notification_more), size - 5));
            for (int i = 0; i < 5 && i < size; i++) {
                style.addLine(Html.fromHtml(mNotifications[i]));
            }
            if (size > 0) {
                mBuilder.setNumber(size);
                mBuilder.setStyle(style);
            }
        Intent intent2 = new Intent(mContext, MainActivity.class);
        intent2.putExtra("drawer_position", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setDefaults(0);
        Notification notification = mBuilder.build();
        if (!mEmptyIcon && mNotifications.length == 0) {
            mNotificationManager.cancel(1);
        } else {
            mNotificationManager.notify(1, notification);
        }
    }
}
