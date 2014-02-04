package com.deathsnacks.wardroid.services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.MainActivity;
import com.deathsnacks.wardroid.utils.Http;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.httpclasses.Alert;
import com.deathsnacks.wardroid.utils.httpclasses.InvasionMini;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.impl.cookie.DateUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
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
    private List<String> mFilters;
    private Boolean mFiltered;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        client = new OkHttpClient();
        mVibrate = false;
        mNotifications = new ArrayList<String>();
        Log.d("deathsnacks", "Received broadcast for alarm, starting polling.");
        /*if (mPreferences.getBoolean("news_enabled", false)) {

        }*/
        if (mPreferences.getBoolean("alert_enabled", false)) {
            mFilters = Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("item_filters", "")));
            mFiltered = mPreferences.getBoolean("filter_enabled", false);
            (new RefreshTask()).execute();
        } else {
            Log.d("deathsnacks", "cancelling alarm since we didn't enable alarm");
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
                    Log.d("deathsnacks", "we received something other than 201 for alerts");
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseAlerts(resp);
                mEditor = mPreferences.edit();
                mEditor.putLong("alerts_modified", connection.getLastModified());
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
        String[] rawAlerts = response.split("\\n");
        Boolean mNew = false;
        for (String rawAlert : rawAlerts) {
            Alert alert = new Alert(rawAlert);
            mNew = false;
            if (!ids.contains(alert.getId())) {
                mNew = true;
                ids.add(alert.getId());
            }
            for (String reward : alert.getRewards()) {
                if (mFilters.contains(reward.replace(" Blueprint", "")) || !mFiltered) {
                    if (mNew)
                        mVibrate = true;
                    mNotifications.add(String.format("Alert: <b>%s</b>",
                            TextUtils.join(" - ", alert.getRewards())));
                    break;
                }
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.commit();
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
                    Log.d("deathsnacks", "we received something other than 201 for invasions");
                    return;
                }
                in = connection.getInputStream();
                byte[] response = readAll(in);
                String resp = new String(response, "UTF-8");
                parseInvasions(resp);
                mEditor = mPreferences.edit();
                mEditor.putLong("invasion_modified", connection.getLastModified());
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
        String[] rawInvasions = response.split("\\n");
        Boolean mNew = false;
        for (String rawInvasion : rawInvasions) {
            InvasionMini invasion = new InvasionMini(rawInvasion);
            mNew = false;
            if (!ids.contains(invasion.getId())) {
                mNew = true;
                ids.add(invasion.getId());
            }
            String[] rewards = invasion.getRewards();
            for (String reward : rewards) {
                if (mFilters.contains(reward) || !mFiltered) {
                   if (mNew)
                       mVibrate = true;
                    mNotifications.add(invasion.getNotificationText());
                    break;
                }
            }
        }
        mEditor = mPreferences.edit();
        mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
        mEditor.commit();
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
        if (size == 0) {
            mNotificationManager.cancel(1);
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Warframe Tracker")
                .setContentText(String.format("%d filtered alerts/invasions", mNotifications.size()))
                .setNumber(size);
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        if (size > 5)
            style.setSummaryText(String.format("+%s more", size - 5));
        for (int i = 0; i < 5 && i < size; i++) {
            style.addLine(Html.fromHtml(mNotifications.get(i)));
        }
        mBuilder.setStyle(style);
        if (mVibrate)
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
