package com.deathsnacks.wardroid.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 25/02/14.
 */
public class CustomFilterActivity extends SherlockActivity {
    private SharedPreferences mPreferences;
    private ListView mList;
    private List<String> mFilters;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFilters = new ArrayList<String>(Arrays.asList(
                PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("custom_filters", ""))));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFilters);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mList = (ListView) findViewById(R.id.listFilters);
        View footer = View.inflate(this, R.layout.list_item_custom_footer, null);
        mList.setAdapter(mAdapter);
        mList.addFooterView(footer);
        View addFilter = mList.findViewById(R.id.add_new_filter);
        addFilter.setOnClickListener(addListener);
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO: Show dialog to add new filter
            new AlertDialog.Builder(CustomFilterActivity.this)
                    .setTitle(getString(R.string.menu_exit))
                    .setMessage(getString(R.string.menu_exit_message))
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
        }
    };
}
