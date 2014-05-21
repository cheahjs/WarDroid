package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.BadlandsListViewAdapter;
import com.deathsnacks.wardroid.adapters.SeparatedListAdapter;
import com.deathsnacks.wardroid.gson.badlands.BadlandNode;
import com.deathsnacks.wardroid.services.PollingAlarmReceiver;
import com.deathsnacks.wardroid.utils.Http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class BadlandsFragment extends SherlockFragment {
    private static final String TAG = "BadlandsFragment";
    private View mRefreshView;
    private ListView mListView;
    private BadlandsRefresh mTask;
    private /*BadlandsListViewAdapter*/SeparatedListAdapter mAdapter;
    private Handler mHandler;
    private boolean mUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_badlands, container, false);
        mRefreshView = rootView.findViewById(R.id.refresh);
        mListView = (ListView) rootView.findViewById(R.id.list);
        mHandler = new Handler();
        setHasOptionsMenu(true);
        mUpdate = true;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getTag() == null)
                    return;
                showDialog(((BadlandsListViewAdapter.GroupViewHolder)view.getTag()).bl_node);
            }
        });
        if (savedInstanceState != null) {
            String pc = savedInstanceState.getString("bl_pc");
            String ps4 = savedInstanceState.getString("bl_ps4");
            long time = savedInstanceState.getLong("time");
            if (pc != null || ps4 != null) {
                mUpdate = false;
                Log.d(TAG, "saved instance");
                Type collectionType = new TypeToken<List<BadlandNode>>() {
                }.getType();
                mAdapter = new SeparatedListAdapter(getSherlockActivity(), null);
                if (pc != null) {
                    List<BadlandNode> data = (new GsonBuilder().create()).fromJson(pc, collectionType);
                    mAdapter.addSection("PC", new BadlandsListViewAdapter(getSherlockActivity(), data));
                }
                if (ps4 != null) {
                    List<BadlandNode> data = (new GsonBuilder().create()).fromJson(ps4, collectionType);
                    mAdapter.addSection("PS4", new BadlandsListViewAdapter(getSherlockActivity(), data));
                }
                mListView.setAdapter(mAdapter);
                mListView.onRestoreInstanceState(savedInstanceState.getParcelable("bl_lv"));
                if (System.currentTimeMillis() - time > 120 * 1000) {
                    refresh(false);
                }
            }
        }
        return rootView;
    }

    private void showDialog(BadlandNode node) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        BadlandDialogFragment blDialog = new BadlandDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("node", (new Gson()).toJson(node));
        blDialog.setArguments(bundle);
        blDialog.show(fm, "dialog");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter == null)
            return;
        outState.putParcelable("bl_lv", mListView.onSaveInstanceState());
        BadlandsListViewAdapter pc = (BadlandsListViewAdapter) mAdapter.getSectionAdapter("PC");
        BadlandsListViewAdapter ps4 = (BadlandsListViewAdapter) mAdapter.getSectionAdapter("PS4");
        if (pc != null)
            outState.putString("bl_pc", pc.getOriginalValues());
        if (ps4 != null)
            outState.putString("bl_ps4", ps4.getOriginalValues());
        outState.putLong("time", System.currentTimeMillis());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(Boolean show) {
        showProgress(show);
        if (mTask == null) {
            mTask = new BadlandsRefresh(getActivity());
            mTask.execute();
        }
    }

    private final Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable mRefreshTimer = new Runnable() {
        @Override
        public void run() {
            refresh(false);
            mHandler.postDelayed(this, 5 * 60 * 1000);
        }
    };

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(mTimer);
        mHandler.removeCallbacksAndMessages(mRefreshTimer);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacksAndMessages(mTimer);
        mHandler.removeCallbacksAndMessages(mRefreshTimer);
        super.onPause();
    }

    @Override
    public void onResume() {
        mHandler.postDelayed(mRefreshTimer, 60 * 1000);
        mTimer.run();
        super.onResume();
    }

    @Override
    public void onStart() {
        if (mUpdate)
            refresh(true);
        mUpdate = true;
        super.onStart();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final Boolean show) {
        if (!isAdded())
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mRefreshView.setVisibility(View.VISIBLE);
            mRefreshView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mListView.setVisibility(View.VISIBLE);
            mListView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
            mListView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class BadlandsRefresh extends AsyncTask<Void, Void, Boolean> {
        private static final String KEY = "BADLANDS";
        private Activity activity;
        private List<BadlandNode> data;
        private List<BadlandNode> data_ps4;
        private boolean error;

        public BadlandsRefresh(Activity activity) {
            this.activity = activity;
            error = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (activity == null)
                return false;
            try {
                SharedPreferences preferences = activity.getSharedPreferences(KEY, Context.MODE_PRIVATE);
                String cache = preferences.getString(KEY + "_cache", "_ded");
                String response;
                try {
                    response = Http.get("http://deathsnacks.com/wf/data/currentbadlands_2.json", preferences, KEY);
                } catch (IOException ex) {
                    //We failed to update, but we still have a cache, hopefully.
                    ex.printStackTrace();
                    //If no cache, proceed to normally handling an exception.
                    if (cache.equals("_ded"))
                        throw ex;
                    response = cache;
                    error = true;
                }
                Type collectionType = new TypeToken<List<BadlandNode>>() {
                }.getType();
                data = (new GsonBuilder().create()).fromJson(response, collectionType);
                String cache2 = preferences.getString(KEY + "_ps4_cache", "_ded");
                String response2;
                try {
                    response2 = Http.get("http://deathsnacks.com/wf/data/ps4/currentbadlands_2.json", preferences, KEY);
                } catch (IOException ex) {
                    //We failed to update, but we still have a cache, hopefully.
                    ex.printStackTrace();
                    //If no cache, proceed to normally handling an exception.
                    if (cache.equals("_ded"))
                        throw ex;
                    response2 = cache;
                    error = true;
                }
                data_ps4 = (new GsonBuilder().create()).fromJson(response2, collectionType);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTask = null;
            if (activity == null)
                return;
            showProgress(false);
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            if (success) {
                mAdapter = new SeparatedListAdapter(activity, null);
                if (mPreferences.getString("platform", "pc").contains("pc")) {
                    Collections.sort(data,
                            new Comparator<BadlandNode>() {
                                @Override
                                public int compare(BadlandNode bl1, BadlandNode bl2) {
                                    String a = bl1.getNodeRegionName() + bl1.getNode();
                                    String b = bl2.getNodeRegionName() + bl2.getNode();
                                    return a.compareTo(b);
                                }
                            });
                    mAdapter.addSection("PC", new BadlandsListViewAdapter(activity, data));
                }
                if (mPreferences.getString("platform", "pc").contains("ps4")) {
                    Collections.sort(data_ps4,
                            new Comparator<BadlandNode>() {
                                @Override
                                public int compare(BadlandNode bl1, BadlandNode bl2) {
                                    String a = bl1.getNodeRegionName() + bl1.getNode();
                                    String b = bl2.getNodeRegionName() + bl2.getNode();
                                    return a.compareTo(b);
                                }
                            });
                    mAdapter.addSection("PS4", new BadlandsListViewAdapter(activity, data_ps4));
                }
                mListView.setAdapter(mAdapter);
                if (error) {
                    Toast.makeText(activity, R.string.error_error_occurred, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), R.string.error_error_occurred, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }
}

