package com.deathsnacks.wardroid.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.deathsnacks.wardroid.R;
import android.support.v4.preference.PreferenceFragment;

/**
 * Created by Admin on 30/01/14.
 */
public class NotificationsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.preference);
    }
}
