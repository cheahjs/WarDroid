package com.deathsnacks.wardroid.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.utils.MultiSelectListPreference;

import java.lang.annotation.Target;
import java.util.List;

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

        if (!mPreferences.contains("set_defaults")) {
            String[] strings = getResources().getStringArray(R.array.aura_filter_entries);
            mEditor.putString("aura_filters", MultiSelectListPreference.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.bp_filter_entries);
            mEditor.putString("blueprint_filters", MultiSelectListPreference.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.misc_filter_entries);
            mEditor.putString("resource_filters", MultiSelectListPreference.toPersistedPreferenceValue(strings));
            strings = getResources().getStringArray(R.array.mod_filter_entries);
            mEditor.putString("mod_filters", MultiSelectListPreference.toPersistedPreferenceValue(strings));
            mEditor.putBoolean("set_defaults", true);
            mEditor.commit();
        }
        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        addPreferencesFromResource(R.xml.preference);
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.abs__home:
            case R.id.homeAsUp:
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
