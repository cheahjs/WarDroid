package com.deathsnacks.wardroid.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.deathsnacks.wardroid.fragments.NotificationsFragment;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 23/01/14.
 */
public class MainActivity extends SherlockFragmentActivity {
    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerTitles = new String[]{"", "News", "Alerts", "Invasions", "Notifications",};
    private String[] mTrackerTitles = new String[]{"News", "Alerts", "Invasions", "Notifications"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private SeparatedListAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new SeparatedListAdapter(this);
        mDrawerAdapter.addSection("Trackers", new ArrayAdapter<String>(this, R.layout.list_item_drawer, mTrackerTitles));
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
        if (savedInstanceState == null) {
            selectItem(1);
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
        //these are headers
        if (position == 0 || position == 5)
            return;
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
            case 4: //notification
                fragment = new NotificationsFragment();
                break;
            default: //wat?
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
