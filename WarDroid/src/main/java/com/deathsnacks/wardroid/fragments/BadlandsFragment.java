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
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.BadlandsListViewAdapter;
import com.deathsnacks.wardroid.utils.Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class BadlandsFragment extends SherlockFragment {
    private View mRefreshView;
    private ExpandableListView mListView;
    private BadlandsRefresh mTask;
    private BadlandsListViewAdapter mAdapter;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_badlands, container, false);
        setRetainInstance(true);
        mRefreshView = rootView.findViewById(R.id.refresh);
        mListView = (ExpandableListView) rootView.findViewById(R.id.list);
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
        //refresh(true);
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
        private Activity activity;
        private List<String> data;

        public BadlandsRefresh(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = Http.get("http://deathsnacks.com/wf/data/flashsales_raw.txt");
                data = response.length() > 2 ? Arrays.asList(response.split("\\n")) : new ArrayList<String>();
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
                    mAdapter = new BadlandsListViewAdapter(activity, data);
                    mListView.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
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

