package com.deathsnacks.wardroid.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.SeparatedListAdapter;
import com.deathsnacks.wardroid.fragments.AlertsFragment;
import com.deathsnacks.wardroid.fragments.InvasionFragment;
import com.deathsnacks.wardroid.fragments.NewsFragment;
import com.deathsnacks.wardroid.services.PollingAlarmManager;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 23/01/14.
 */
public class MainActivity extends SherlockFragmentActivity {
    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerTitles = new String[]{"", "News", "Alerts", "Invasions", "", "Notifications"};
    private String[] mTrackerTitles = new String[]{"News", "Alerts", "Invasions"};
    private String[] mOptionTitles = new String[]{"Notifications"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private SeparatedListAdapter mDrawerAdapter;
    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new SeparatedListAdapter(this);
        mDrawerAdapter.addSection("Trackers", new ArrayAdapter<String>(this, R.layout.list_item_drawer, mTrackerTitles));
        mDrawerAdapter.addSection("Settings", new ArrayAdapter<String>(this, R.layout.list_item_drawer, mOptionTitles));
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mTitle = mDrawerTitle = getTitle();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */

        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        (new PreloadData(this)).execute();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPreferences.getBoolean("alert_enabled", false)) {
            Log.d("deathsnacks", "starting alarm");
            Intent alarmIntent = new Intent(this, PollingAlarmManager.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (savedInstanceState == null) {
                try {
                    Log.d("deathsnacks", "forcing start of alarm");
                    pendingIntent.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((AlarmManager)getSystemService(ALARM_SERVICE)).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            int startPos = intent.getIntExtra("drawer_position", 1);
            selectItem(startPos);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0 && position != 4)
                selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        SherlockFragment fragment = null;
        switch (position) {
            case 1: //news
                fragment = new NewsFragment();
                break;
            case 2: //alerts
                fragment = new AlertsFragment();
                break;
            case 3: //invasions
                fragment = new InvasionFragment();
                break;
            case 5: //notification settings
                mDrawerLayout.closeDrawer(mDrawerList);
                mDrawerList.setItemChecked(position, false);
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                return;
            default: //wat?
                Log.w("deathsnacks", "we some how fucked up with the drawer positions");
                return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        mActionBar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawer_list = (ListView) findViewById(R.id.left_drawer);
        if (item.getItemId() == android.R.id.home) {

            if (drawer_layout.isDrawerOpen(drawer_list)) {
                drawer_layout.closeDrawer(drawer_list);
            } else {
                drawer_layout.openDrawer(drawer_list);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public class PreloadData extends AsyncTask<Void, Void, Void> {
        private Activity mActivity;

        public PreloadData(Activity act) {
            mActivity = act;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //preload string data
            Names.getName(mActivity, "a");
            Names.getNode(mActivity, "a");
            Names.getString(mActivity, "a");
            return null;
        }

        @Override
        protected void onPostExecute(Void voi) {
        }

        @Override
        protected void onCancelled() {
        }
    }
}
