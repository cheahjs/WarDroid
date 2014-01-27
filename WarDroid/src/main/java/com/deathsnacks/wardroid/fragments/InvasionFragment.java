package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.InvasionListViewAdapter;
import com.deathsnacks.wardroid.utils.Http;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        mRefreshView = rootView.findViewById(R.id.alert_refresh);
        mInvasionView = (ListView) rootView.findViewById(R.id.list_alerts);
        showProgress(true);
        if (mTask == null) {
            mTask = new InvasionRefresh(getActivity());
            mTask.execute();
        }
        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final Boolean show) {
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
            mInvasionView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRefreshView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                String response = Http.get(activity, "http://deathsnacks.com/wf/data/invasion_raw.txt");
                data = Arrays.asList(response.split("\\n"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTask = null;
            showProgress(false);
            if (success){
                try {
                    mInvasionView.setAdapter(new InvasionListViewAdapter(activity, data));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
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

