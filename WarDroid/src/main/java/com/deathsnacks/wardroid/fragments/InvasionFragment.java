package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.deathsnacks.wardroid.adapters.InvasionListViewAdapter;
import com.deathsnacks.wardroid.gson.Alert;
import com.deathsnacks.wardroid.utils.Http;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.httpclasses.Invasion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class InvasionFragment extends SherlockFragment {
    private View mRefreshView;
    private ListView mInvasionView;
    private InvasionRefresh mTask;
    private InvasionListViewAdapter mAdapter;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        setRetainInstance(true);
        mRefreshView = rootView.findViewById(R.id.alert_refresh);
        mInvasionView = (ListView) rootView.findViewById(R.id.list_alerts);
        mInvasionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Invasion invasion = (Invasion) view.getTag();
                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                List<String> ids =
                        new ArrayList<String>(Arrays.asList(PreferenceUtils
                                .fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
                if (ids.contains(invasion.getId()))
                    return;
                new AlertDialog.Builder(getActivity()).setMessage("Do you want to mark this invasion as completed?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils
                                        .fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
                                ids.add(invasion.getId());
                                SharedPreferences.Editor mEditor = mPreferences.edit();
                                mEditor.putString("invasion_completed_ids", PreferenceUtils.toPersistedPreferenceValue(ids.toArray(new String[ids.size()])));
                                mEditor.commit();
                                dialogInterface.cancel();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).show();
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
        showProgress(show);
        if (mTask == null) {
            mTask = new InvasionRefresh(getActivity());
            mTask.execute();
        }
    }

    private final Runnable mRefreshTimer = new Runnable() {
        @Override
        public void run() {
            refresh(false);
            mHandler.postDelayed(this, 60 * 1000);
        }
    };

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(mRefreshTimer);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacksAndMessages(mRefreshTimer);
        super.onPause();
    }

    @Override
    public void onResume() {
        getSherlockActivity().getSupportActionBar().setTitle("Invasions");
        mHandler.postDelayed(mRefreshTimer, 60 * 1000);
        super.onResume();
    }

    @Override
    public void onStart() {
        refresh(true);
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

            mInvasionView.setVisibility(View.VISIBLE);
            mInvasionView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mInvasionView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
            mInvasionView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class InvasionRefresh extends AsyncTask<Void, Void, Boolean> {
        private Activity activity;
        private List<String> data;

        public InvasionRefresh(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = Http.get("http://deathsnacks.com/wf/data/invasion_raw.txt");
                data = Arrays.asList(response.split("\\n"));
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
                List<String> ids = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_ids", ""))));
                List<String> newids = new ArrayList<String>();
                List<String> nowids = new ArrayList<String>();
                for (int i = 1; i < data.size(); i++) {
                    nowids.add(data.get(i).split("\\|")[0]);
                }
                for (String id : ids) {
                    if (nowids.contains(id)) {
                        newids.add(id);
                    }
                }
                SharedPreferences.Editor mEditor = mPreferences.edit();
                mEditor.putString("invasion_ids", PreferenceUtils.toPersistedPreferenceValue(newids.toArray(new String[newids.size()])));
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
                    mAdapter = new InvasionListViewAdapter(activity, data);
                    mInvasionView.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), R.string.error_error_occurred, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
        }
    }
}

