package com.deathsnacks.wardroid.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.fragments.AlertsFragment;
import com.deathsnacks.wardroid.fragments.InvasionFragment;
import com.deathsnacks.wardroid.fragments.NewsFragment;
import com.deathsnacks.wardroid.services.PollingAlarmReceiver;
import com.deathsnacks.wardroid.utils.Names;

import java.util.ArrayList;

/**
 * Created by Admin on 23/01/14.
 */
public class MainActivity extends SherlockFragmentActivity {
    private static final String TAG = "MainActivity";
    private ActionBar mActionBar;
    private String[] mDrawerTitles = new String[]{"", "News", "Alerts", "Invasions"};
    private SharedPreferences mPreferences;
    private ViewPager mPager;
    private FragmentManager mFragmentManager;
    private TabsAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_titles);
        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new TabsAdapter(this, mPager);
        mPagerAdapter.addTab(mActionBar.newTab().setText(mDrawerTitles[0]), NewsFragment.class, null);
        mPagerAdapter.addTab(mActionBar.newTab().setText(mDrawerTitles[1]), AlertsFragment.class, null);
        mPagerAdapter.addTab(mActionBar.newTab().setText(mDrawerTitles[2]), InvasionFragment.class, null);

        (new PreloadData(this)).execute();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            Log.d(TAG, "no saved instance state");
            if (mPreferences.getBoolean("alert_enabled", false)) {
                Log.d(TAG, "starting alarm");
                Intent alarmIntent = new Intent(this, PollingAlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (pendingIntent != null) {
                    try {
                        Log.d(TAG, "forcing start of alarm");
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(getString(R.string.notification_title))
                                .setContentText(getString(R.string.notification_starting))
                                .setOngoing(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("drawer_position", 2);
                        PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(pendingIntent2);
                        mNotificationManager.notify(1, mBuilder.build());
                        (new PollingAlarmReceiver()).onReceive(this.getApplicationContext(), null);
                        if (!mPreferences.getBoolean("push", false)) {
                            ((AlarmManager) getSystemService(ALARM_SERVICE)).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                                    SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = getIntent();
            int startPos = intent.getIntExtra("drawer_position", 0);
            mActionBar.setSelectedNavigationItem(startPos);
        } else {
            mActionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
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

    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final SherlockFragmentActivity mActivity;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActivity = activity;
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActivity.supportInvalidateOptionsMenu();
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    if (mViewPager.getCurrentItem() != i)
                        mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }
    }
}
