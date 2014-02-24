package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.SettingsActivity;
import com.deathsnacks.wardroid.adapters.AlertsListViewAdapter;
import com.deathsnacks.wardroid.gson.Alert;
import com.deathsnacks.wardroid.utils.Http;
import com.deathsnacks.wardroid.utils.Names;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class AlertsFragment extends SherlockFragment {
    private static final String TAG = "AlertsFragment";
    private View mRefreshView;
    private ListView mAlertView;
    private AlertsRefresh mTask;
    private UpdateTask mUpdateTask;
    private AlertsListViewAdapter mAdapter;
    private Handler mHandler;
    private View mNoneView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        setRetainInstance(true);
        mRefreshView = rootView.findViewById(R.id.alert_refresh);
        mNoneView = rootView.findViewById(R.id.alerts_none);
        mAlertView = (ListView) rootView.findViewById(R.id.list_alerts);
        mAlertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Alert alert = ((AlertsListViewAdapter.ViewHolder) view.getTag()).alert;
                SharedPreferences mPreferences2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
                List<String> ids =
                        new ArrayList<String>(Arrays.asList(PreferenceUtils
                                .fromPersistedPreferenceValue(mPreferences2.getString("alert_completed_ids", ""))));
                if (ids.contains(alert.get_id().get$id())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(String.format("%s (%s)", Names.getNode(getActivity(), alert.getMissionInfo().getLocation()),
                                    Names.getRegion(getActivity(), alert.getMissionInfo().getLocation())))
                            .setMessage(getActivity().getString(R.string.alert_mark_incomplete))
                            .setPositiveButton(getActivity().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    List<String> ids2 =
                                            new ArrayList<String>(Arrays.asList(PreferenceUtils
                                                    .fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
                                    ids2.remove(alert.get_id().get$id());
                                    SharedPreferences.Editor mEditor = mPreferences.edit();
                                    mEditor.putString("alert_completed_ids", PreferenceUtils.toPersistedPreferenceValue(ids2.toArray(new String[ids2.size()])));
                                    mEditor.commit();
                                    mAdapter.notifyDataSetChanged();
                                    dialogInterface.cancel();
                                }
                            })
                            .setNegativeButton(getActivity().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(String.format("%s (%s)", Names.getNode(getActivity(), alert.getMissionInfo().getLocation()),
                                    Names.getRegion(getActivity(), alert.getMissionInfo().getLocation())))
                            .setMessage(getActivity().getString(R.string.alert_mark_complete))
                            .setPositiveButton(getActivity().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    List<String> ids2 =
                                            new ArrayList<String>(Arrays.asList(PreferenceUtils
                                                    .fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
                                    ids2.add(alert.get_id().get$id());
                                    SharedPreferences.Editor mEditor = mPreferences.edit();
                                    mEditor.putString("alert_completed_ids", PreferenceUtils.toPersistedPreferenceValue(ids2.toArray(new String[ids2.size()])));
                                    mEditor.commit();
                                    mAdapter.notifyDataSetChanged();
                                    dialogInterface.cancel();
                                }
                            })
                            .setNegativeButton(getActivity().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();
                }
            }
        });
        mHandler = new Handler();
        setHasOptionsMenu(true);
        return rootView;
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
        Log.d(TAG, "Starting refresh.");
        showProgress(show);
        if (mTask == null) {
            mTask = new AlertsRefresh(getActivity());
            mTask.execute();
        }
    }

    private final Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChangedLight();
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable mRefreshTimer = new Runnable() {
        @Override
        public void run() {
            refresh(false);
            mHandler.postDelayed(this, 60 * 1000);
        }
    };

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "we called ondestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "we called onpause");
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
        refresh(true);
        if (mUpdateTask == null) {
            mUpdateTask = new UpdateTask(getActivity());
            mUpdateTask.execute();
        }
        super.onStart();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final Boolean show) {
        if (!isAdded())
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mRefreshView.setVisibility(View.VISIBLE);
            mAlertView.setVisibility(View.VISIBLE);
            try {
            mAlertView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAlertView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
            mRefreshView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
            } catch (Exception ex) {
                mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
                mAlertView.setVisibility(show ? View.GONE : View.VISIBLE);
                ex.printStackTrace();
            }
            mNoneView.setVisibility(View.GONE);
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAlertView.setVisibility(show ? View.GONE : View.VISIBLE);
            mNoneView.setVisibility(View.GONE);
        }
    }

    public class AlertsRefresh extends AsyncTask<Void, Void, Boolean> {
        private static final String KEY = "alerts_json";
        private Activity activity;
        private List<Alert> data;
        private Boolean error;

        public AlertsRefresh(Activity activity) {
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
                    response = Http.get("http://deathsnacks.com/wf/data/last15alerts.json", preferences, KEY);
                } catch (IOException ex) {
                    //We failed to update, but we still have a cache, hopefully.
                    ex.printStackTrace();
                    //If no cache, proceed to normally handling an exception.
                    if (cache.equals("_ded"))
                        throw ex;
                    response = cache;
                    error = true;
                }
                Type collectionType = new TypeToken<List<Alert>>() {
                }.getType();
                data = (new GsonBuilder().create()).fromJson(response, collectionType);
                clearIds();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        private void clearIds() {
            if (activity == null)
                return;
            try {
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
                List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_ids", ""))));
                List<String> newids = new ArrayList<String>();
                List<String> nowids = new ArrayList<String>();
                for (Alert alert : data) {
                    nowids.add(alert.get_id().get$id());
                }
                for (String id : ids) {
                    if (nowids.contains(id)) {
                        newids.add(id);
                    }
                }
                SharedPreferences.Editor mEditor = mPreferences.edit();
                mEditor.putString("alert_ids", PreferenceUtils.toPersistedPreferenceValue(newids.toArray(new String[newids.size()])));
                mEditor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTask = null;
            if (activity == null)
                return;
            showProgress(false);
            if (success) {
                try {
                    mAdapter = new AlertsListViewAdapter(activity, data, mNoneView);
                    mAlertView.setAdapter(mAdapter);
                    if (error) {
                        Toast.makeText(activity, R.string.error_error_occurred, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, R.string.error_error_occurred, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }

    public class UpdateTask extends AsyncTask<Void, Void, Boolean> {
        private Activity activity;
        private int version;
        private String[] list;
        //MAGIC_NUMBER|planet|aura|bp|mod|misc

        public UpdateTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String response = Http.get("http://deathsnacks.com/wf/game_data/current_rewards.txt");
                version = Integer.parseInt(response.trim());
                if (version > preferences.getInt("reward_version", getResources().getInteger(R.integer.reward_version))) {
                    String newEntries = Http.get("http://deathsnacks.com/wf/game_data/current_rewards_list.txt");
                    if (!newEntries.contains("MAGIC_NUMBER"))
                        return false;
                    list = newEntries.split("\\n");
                    if (list.length < 6)
                        return false;
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mUpdateTask = null;
            if (activity == null)
                return;
            if (success) {
                try {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("reward_version", version);
                    editor.putString("planet_entries", list[1]);
                    editor.putString("aura_entries", list[2]);
                    editor.putString("bp_entries", list[3]);
                    editor.putString("mod_entries", list[4]);
                    editor.putString("misc_entries", list[5]);
                    editor.commit();
                    Toast.makeText(activity, "Item filters have been updated.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateTask = null;
        }
    }
}

