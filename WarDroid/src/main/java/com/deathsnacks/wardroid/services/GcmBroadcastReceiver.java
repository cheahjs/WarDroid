package com.deathsnacks.wardroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Admin on 23/02/14.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GcmBroadcastReceiver";
    private GoogleCloudMessaging gcm;

    @Override
    public void onReceive(Context context, Intent intent) {
        gcm = GoogleCloudMessaging.getInstance(context);
        Log.d(TAG, "GCM BROADCAST RECEIVED!");
        if (gcm.getMessageType(intent).equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
            Log.i(TAG, "We've received a gcm message.");
            Log.d(TAG, intent.getStringExtra("alerts"));
            Log.d(TAG, intent.getStringExtra("invasions"));
        }
    }
}
