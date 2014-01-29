package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.AlertsListViewAdapter;
import com.deathsnacks.wardroid.gson.Alert;
import com.deathsnacks.wardroid.utils.Http;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class AlertsFragment extends SherlockFragment {
    private View mRefreshView;
    private ListView mAlertView;
    private AlertsRefresh mTask;
    private AlertsListViewAdapter mAdapter;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        mRefreshView = rootView.findViewById(R.id.alert_refresh);
        mAlertView = (ListView) rootView.findViewById(R.id.list_alerts);
        mHandler = new Handler();
        return rootView;
    }

    private void refresh(Boolean show) {
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
                mAdapter.notifyDataSetChanged();
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    private final Runnable mRefreshTimer = new Runnable() {
        @Override
        public void run() {
            refresh(true);
            mHandler.postDelayed(this, 60 * 1000);
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

            mAlertView.setVisibility(View.VISIBLE);
            mAlertView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAlertView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAlertView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRefreshView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class AlertsRefresh extends AsyncTask<Void, Void, Boolean> {
        private Activity activity;
        private List<Alert> data;

        public AlertsRefresh(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = Http.get(activity, "http://deathsnacks.com/wf/data/last15alerts.json");
                Type collectionType = new TypeToken<List<Alert>>() {
                }.getType();
                data = (new GsonBuilder().create()).fromJson(response, collectionType);
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
            if (success) {
                try {
                    mAdapter = new AlertsListViewAdapter(activity, data);
                    mAlertView.setAdapter(mAdapter);
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

