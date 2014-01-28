package com.deathsnacks.wardroid.activities;

import android.content.Context;
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
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.fragments.AlertsFragment;
import com.deathsnacks.wardroid.fragments.DronesFragment;
import com.deathsnacks.wardroid.fragments.FoundryFragment;
import com.deathsnacks.wardroid.fragments.InvasionFragment;
import com.deathsnacks.wardroid.fragments.LoginFragment;
import com.deathsnacks.wardroid.fragments.NewsFragment;
import com.deathsnacks.wardroid.utils.GlobalApplication;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 23/01/14.
 */
public class MainActivity extends SherlockFragmentActivity {
    private ActionBar mActionBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mDrawerTitles = new String[]{"News", "Alerts", "Invasions", "Foundry", "Extractors", "Clan", "Log Out"};
    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_drawer, mDrawerTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //preload string data
        Names.getName(this, "a");
        Names.getNode(this, "a");
        Names.getString(this, "a");
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        SherlockFragment fragment = null;

        switch (position) {
            case 0: //news
                fragment = new NewsFragment();
                break;
            case 1: //alerts
                fragment = new AlertsFragment();
                break;
            case 2: //invasions
                fragment = new InvasionFragment();
                break;
            case 3: //foundry
                if (((GlobalApplication) getApplication()).getDisplayName() == null)
                    fragment = new LoginFragment(new FoundryFragment());
                else
                    fragment = new FoundryFragment();
                break;
            case 4: //drones
                if (((GlobalApplication) getApplication()).getDisplayName() == null)
                    fragment = new LoginFragment(new DronesFragment());
                else
                    fragment = new DronesFragment();
                break;
            case 5: //clan
                if (((GlobalApplication) getApplication()).getDisplayName() == null)
                    fragment = new LoginFragment(new DronesFragment());
                else
                    fragment = new DronesFragment();
                break;
            case 6: //logout
                GlobalApplication app = (GlobalApplication) getApplication();
                if (app.getDisplayName() != null) {
                    app.setNonce(0);
                    app.setDisplayName(null);
                    app.setAccountId(null);
                    Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
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
}
