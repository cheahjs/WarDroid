package com.deathsnacks.wardroid.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.deathsnacks.wardroid.Constants;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 25/02/14.
 */
public class CustomFilterActivity extends ActionBarActivity {
    private SharedPreferences mPreferences;
    private ListView mList;
    private List<String> mFilters;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFilters = new ArrayList<String>(Arrays.asList(
                PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString(Constants.PREF_CUSTOM_FILTERS, ""))));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mFilters);
        setContentView(R.layout.activity_filters);
        mList = (ListView) findViewById(R.id.listFilters);
        //View footer = View.inflate(this, R.layout.list_item_custom_footer, null);
        //mList.addFooterView(footer);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(clickListener);
        //View addFilter = mList.findViewById(R.id.add_new_filter);
        //addFilter.setOnClickListener(addListener);
        hideSoftKeyboard();
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    ListView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView text = (TextView) view;
            final String filter = text.getText().toString();
            if (mFilters.contains(filter)) {
                new AlertDialog.Builder(CustomFilterActivity.this)
                        .setMessage(String.format(getString(R.string.remove_custom),
                                filter))
                        .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                mFilters.remove(filter);
                                //mAdapter.remove(filter);
                                SharedPreferences.Editor mEditor = mPreferences.edit();
                                mEditor.putString(Constants.PREF_CUSTOM_FILTERS, PreferenceUtils.toPersistedPreferenceValue(
                                        mFilters.toArray(new String[mFilters.size()])));
                                mEditor.commit();
                                mAdapter.notifyDataSetChanged();
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
            }
        }
    };

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog alertDialog = new AlertDialog.Builder(CustomFilterActivity.this)
                    .setTitle("Add new custom filter")
                    .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Dialog dialog = (Dialog) dialogInterface;
                            EditText text = (EditText) dialog.findViewById(R.id.filter_text);
                            mFilters.add(text.getText().toString());
                            //mAdapter.add(text.getText().toString());
                            mAdapter.notifyDataSetChanged();
                            SharedPreferences.Editor editor = mPreferences.edit();
                            editor.putString(Constants.PREF_CUSTOM_FILTERS, PreferenceUtils.toPersistedPreferenceValue(
                                    mFilters.toArray(new String[mFilters.size()])));
                            editor.commit();
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).create();
            alertDialog.setView(View.inflate(getApplicationContext(), R.layout.dialog_custom_filter, null));
            alertDialog.show();
            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
            case R.id.homeAsUp:
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                super.onBackPressed();
                return true;
            case R.id.new_filter:
                AlertDialog alertDialog = new AlertDialog.Builder(CustomFilterActivity.this)
                        .setTitle("Add new custom filter")
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog) dialogInterface;
                                EditText text = (EditText) dialog.findViewById(R.id.filter_text);
                                mFilters.add(text.getText().toString());
                                //mAdapter.add(text.getText().toString());
                                mAdapter.notifyDataSetChanged();
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putString(Constants.PREF_CUSTOM_FILTERS, PreferenceUtils.toPersistedPreferenceValue(
                                        mFilters.toArray(new String[mFilters.size()])));
                                editor.commit();
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create();
                alertDialog.setView(View.inflate(getApplicationContext(), R.layout.dialog_custom_filter, null));
                alertDialog.show();
                alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
