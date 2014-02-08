package com.deathsnacks.wardroid.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.services.PollingAlarmManager;
import com.deathsnacks.wardroid.utils.PreferenceUtils;

import java.text.NumberFormat;

/**
 * Created by Admin on 30/01/14.
 */
public class NotificationsActivity extends SherlockPreferenceActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        mPreferences.registerOnSharedPreferenceChangeListener(prefChanged);
        /*if (!mPreferences.contains("set_defaults")) {
            String[] strings = getResources().getStringArray(R.array.aura_filter_entries);
            mEditor.putString("aura_filters", PreferenceUtils.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.bp_filter_entries);
            mEditor.putString("blueprint_filters", PreferenceUtils.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.misc_filter_entries);
            mEditor.putString("resource_filters", PreferenceUtils.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.mod_filter_entries);
            mEditor.putString("mod_filters", PreferenceUtils.toPersistedPreferenceValue(strings));
            mEditor.putBoolean("set_defaults", true);
            mEditor.commit();
        }*/
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        addPreferencesFromResource(R.xml.preference);
        Preference cred = findPreference("credit_filter");
        if (cred != null)
            try {
                cred.setSummary(NumberFormat.getIntegerInstance().format(mPreferences.getInt("credit_filter", 0)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        Preference uriPref = findPreference("sound");
        if (uriPref != null) {
            try {
                Uri ringtoneUri = Uri.parse(mPreferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
                String name = ringtone.getTitle(getApplicationContext());
                if (mPreferences.getString("sound", "") == "") {
                    uriPref.setSummary("None");
                } else {
                    uriPref.setSummary(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //}
    }

    @Override
    protected void onDestroy() {
        mPreferences.unregisterOnSharedPreferenceChangeListener(prefChanged);
        super.onDestroy();
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Log.d("deathsnacks", "changed pref: " + s);
            if (s.equals("alert_enabled")) {
                if (sharedPreferences.getBoolean("alert_enabled", false)) {
                    Log.d("deathsnacks", "starting alarm since pref was changed");
                    Intent alarmIntent = new Intent(getApplicationContext(), PollingAlarmManager.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Warframe Tracker")
                            .setContentText("Starting background service.")
                            .setOngoing(true);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("drawer_position", 2);
                    PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent2);
                    mNotificationManager.notify(1, mBuilder.build());
                    try {
                        (new PollingAlarmManager()).onReceive(getApplicationContext(), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ((AlarmManager)getSystemService(ALARM_SERVICE)).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                } else {
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(1);
                    ((AlarmManager)getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
                            getApplicationContext(), 0, new Intent(getApplicationContext(), PollingAlarmManager.class), PendingIntent.FLAG_UPDATE_CURRENT));
                }
            } else if (s.equals("credit_filter")) {
                Preference cred = findPreference("credit_filter");
                if (cred != null)
                    cred.setSummary(NumberFormat.getIntegerInstance().format(mPreferences.getInt("credit_filter", 0)));
            } else if (s.equals("sound")) {
                Preference uriPref = findPreference("sound");
                if (uriPref == null)
                    return;
                Uri ringtoneUri = Uri.parse(mPreferences.getString("sound", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
                String name = ringtone.getTitle(getApplicationContext());
                if (mPreferences.getString("sound", "") == "") {
                    uriPref.setSummary("None");
                } else {
                    uriPref.setSummary(name);
                }
            }
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case R.id.homeAsUp:
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
