package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.activities.SettingsActivity;
import com.deathsnacks.wardroid.adapters.NewsListViewAdapter;
import com.deathsnacks.wardroid.utils.Http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class NewsFragment extends SherlockFragment {
    private static final String TAG = "NewsFragment";
    private View mRefreshView;
    private ListView mNewsView;
    private NewsRefresh mTask;
    private NewsListViewAdapter mAdapter;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        setRetainInstance(true);
        mRefreshView = rootView.findViewById(R.id.news_refresh);
        mNewsView = (ListView) rootView.findViewById(R.id.list_news);
        mHandler = new Handler();
        mNewsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = ((TextView) view.findViewById(R.id.news_url)).getText().toString();
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getActivity().startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
        menu.removeItem(R.id.settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh(true);
                return true;
            case R.id.settings:
                Intent intent = new Intent(getSherlockActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh(Boolean show) {
        Log.d(TAG, "Starting refresh.");
        showProgress(show);
        if (mTask == null) {
            mTask = new NewsRefresh(getActivity());
            mTask.execute();
        }
    }

    private final Runnable mRefreshTimer = new Runnable() {
        @Override
        public void run() {
            if (mAdapter != null) {
                refresh(false);
                mHandler.postDelayed(this, 60 * 1000);
            }
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
            mNewsView.setVisibility(View.VISIBLE);
            try {
                mRefreshView.animate()
                        .setDuration(shortAnimTime)
                        .alpha(show ? 1 : 0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
                            }
                        });
                mNewsView.animate()
                        .setDuration(shortAnimTime)
                        .alpha(show ? 0 : 1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mNewsView.setVisibility(show ? View.GONE : View.VISIBLE);
                            }
                        });
            } catch (Exception ex) {
                mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
                mNewsView.setVisibility(show ? View.GONE : View.VISIBLE);
                ex.printStackTrace();
            }
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRefreshView.setVisibility(show ? View.VISIBLE : View.GONE);
            mNewsView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class NewsRefresh extends AsyncTask<Void, Void, Boolean> {
        private static final String KEY = "news_raw";
        private Activity activity;
        private List<String> data;
        private boolean error;

        public NewsRefresh(Activity activity) {
            this.activity = activity;
            error = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (activity == null)
                return false;
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                String cache = preferences.getString(KEY + "_cache", "_ded");
                String response;
                try {
                    response = Http.get("http://deathsnacks.com/wf/data/news_raw.txt",
                            preferences.getLong(KEY + "_modified", 0), cache, preferences.edit(), KEY);
                } catch (IOException ex) {
                    //We failed to update, but we still have a cache, hopefully.
                    ex.printStackTrace();
                    //If no cache, proceed to normally handling an exception.
                    if (cache.equals("_ded"))
                        throw ex;
                    response = cache;
                    error = true;
                }
                response = response.trim();
                data = Arrays.asList(response.split("\\n"));
                if (response.length() < 2)
                    data = new ArrayList<String>();
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
                    mAdapter = new NewsListViewAdapter(activity, data);
                    mNewsView.setAdapter(mAdapter);
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
}

