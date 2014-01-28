package com.deathsnacks.wardroid.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.NewsListViewAdapter;
import com.deathsnacks.wardroid.utils.Http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 23/01/14.
 */
public class NewsFragment extends SherlockFragment {
    private View mRefreshView;
    private ListView mNewsView;
    private NewsRefresh mTask;
    private NewsListViewAdapter mAdapter;
    private Handler mHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
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
        refresh(true);
        return rootView;
    }

    private void refresh(Boolean show) {
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final Boolean show) {
        if (isAdded()) {
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

                mNewsView.setVisibility(View.VISIBLE);
                mNewsView.animate()
                        .setDuration(shortAnimTime)
                        .alpha(show ? 0 : 1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mNewsView.setVisibility(show ? View.GONE : View.VISIBLE);
                            }
                        });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mNewsView.setVisibility(show ? View.VISIBLE : View.GONE);
                mRefreshView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

    public class NewsRefresh extends AsyncTask<Void, Void, Boolean> {
        private Activity activity;
        private List<String> data;

        public NewsRefresh(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = Http.get(activity, "http://deathsnacks.com/wf/data/news_raw.txt");
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
            showProgress(false);
            mAdapter = new NewsListViewAdapter(activity, data);
            if (success) {
                try {
                    mNewsView.setAdapter(mAdapter);
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

