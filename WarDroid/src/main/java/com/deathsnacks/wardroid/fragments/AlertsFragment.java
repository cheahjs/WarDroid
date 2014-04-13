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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.deathsnacks.wardroid.Constants;
import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.AlertsListViewAdapter;
import com.deathsnacks.wardroid.gson.alert.Alert;
import com.deathsnacks.wardroid.services.PollingAlarmReceiver;
import com.deathsnacks.wardroid.utils.Http;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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
public class AlertsFragment extends SherlockFragment {
    private static final String TAG = "AlertsFragment";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private View mRefreshView;
    private ListView mAlertView;
    private AlertsRefresh mTask;
    private UpdateTask mUpdateTask;
    private AlertsListViewAdapter mAdapter;
    private Handler mHandler;
    private View mNoneView;
    private View mFooterView;
    private boolean mUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        //setRetainInstance(true);
        mRefreshView = rootView.findViewById(R.id.alert_refresh);
        mNoneView = rootView.findViewById(R.id.alerts_none);
        mFooterView = View.inflate(getSherlockActivity(), R.layout.list_item_custom_footer, null);
        mAlertView = (ListView) rootView.findViewById(R.id.list_alerts);
        mAlertView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view.getTag() == null)
                    return;
                final Alert alert = ((AlertsListViewAdapter.ViewHolder) view.getTag()).alert;
                SharedPreferences mPreferences2 = PreferenceManager.getDefaultSharedPreferences(getActivity());
                List<String> ids =
                        new ArrayList<String>(Arrays.asList(PreferenceUtils
                                .fromPersistedPreferenceValue(mPreferences2.getString("alert_completed_ids", ""))));
                if (ids.contains(alert.get_id().get$id())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(alert.getMissionInfo().getLocation())
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
                            .setTitle(alert.getMissionInfo().getLocation())
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
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        TextView footerText = (TextView) mFooterView.findViewById(R.id.footer_text);
        footerText.setText(R.string.hide_expired_alerts);
        mAlertView.addFooterView(mFooterView);
        View buttonView = mAlertView.findViewById(R.id.footer_text);
        buttonView.setOnClickListener(showHiddenListener);
        mHandler = new Handler();
        setHasOptionsMenu(true);
        mUpdate = true;
        if (savedInstanceState != null) {
            String alerts = savedInstanceState.getString("alerts");
            long time = savedInstanceState.getLong("time");
            if (alerts != null) {
                mUpdate = false;
                Type collectionType = new TypeToken<List<Alert>>() {
                }.getType();
                List<Alert> data = (new GsonBuilder().create()).fromJson(alerts, collectionType);
                mAdapter = new AlertsListViewAdapter(getSherlockActivity(), data, mNoneView, savedInstanceState.getBoolean("alerts_hidden"), mFooterView);
                mAlertView.setAdapter(mAdapter);
                mAlertView.onRestoreInstanceState(savedInstanceState.getParcelable("alert_lv"));
                if (System.currentTimeMillis() - time > 120) {
                    refresh(false);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAdapter == null)
            return;
        outState.putParcelable("alert_lv", mAlertView.onSaveInstanceState());
        outState.putString("alerts", mAdapter.getOriginalValues());
        outState.putBoolean("alerts_hidden", mAdapter.getShowHidden());
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
        Log.d(TAG, "Starting refresh.");
        showProgress(show);
        if (mTask == null) {
            mTask = new AlertsRefresh(getActivity());
            mTask.execute();
        }
    }

    //region Runnables/Anonymous stuff that can't be collapsed
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

    private final View.OnClickListener showHiddenListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextView textView = (TextView) view;
            if (mAdapter.getShowHidden()) {
                mAdapter.setShowHidden(false);
                mAdapter.notifyDataSetChanged();
                textView.setText(R.string.show_expired_alerts);
            } else {
                mAdapter.setShowHidden(true);
                mAdapter.notifyDataSetChanged();
                textView.setText(R.string.hide_expired_alerts);
            }
        }
    };
    //endregion

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
        Log.d(TAG, "onStart was called.");
        if (mUpdate) {
            refresh(true);
            try {
                if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Constants.PREF_PUSH, false)) {
                    if (getRegistrationId(getSherlockActivity()).length() == 0) {
                        Log.i(TAG, "Push is enabled, and we just got an empty registration id, registering again.");
                        registerInBackground();
                    } else {
                        Log.i(TAG, "Push is enabled, and we just want to register again, registering again. Hopefully we don't kill the server.");
                        registerInBackground();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "some how we crashed while trying to do stuff.");
            }
        }
        mUpdate = true;
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
        private List<Alert> ps4data;
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
                    response = Http.get("http://deathsnacks.com/wf/data/last15alerts_localized.json", preferences, KEY);
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
                List<Alert> newList = new ArrayList<Alert>();
                for (int i = 0; i < data.size(); i++) {
                    Alert alert = data.get(i);
                    alert.setPc(true);
                    newList.add(alert);
                }
                data = newList;
                String cache2 = preferences.getString(KEY + "_ps4" + "_cache", "_ded");
                String ps4response;
                try {
                    ps4response = Http.get("http://deathsnacks.com/wf/data/ps4/last15alerts_localized.json", preferences, KEY + "_ps4");
                } catch (IOException ex) {
                    //We failed to update, but we still have a cache, hopefully.
                    ex.printStackTrace();
                    //If no cache, proceed to normally handling an exception.
                    if (cache2.equals("_ded"))
                        throw ex;
                    ps4response = cache2;
                    error = true;
                }
                ps4data = (new GsonBuilder().create()).fromJson(ps4response, collectionType);
                List<Alert> newList2 = new ArrayList<Alert>();
                for (int i = 0; i < ps4data.size(); i++) {
                    Alert alert = ps4data.get(i);
                    alert.setPc(false);
                    newList2.add(alert);
                }
                ps4data = newList2;
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
                for (Alert alert : ps4data) {
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
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            if (success) {
                try {
                    List<Alert> merged = new ArrayList<Alert>();
                    String pref = mPreferences.getString("platform", "pc");
                    if (pref.contains("pc"))
                        merged.addAll(data);
                    if (pref.contains("ps4"))
                        merged.addAll(ps4data);
                    Collections.sort(merged,
                            new Comparator<Alert>() {
                                @Override
                                public int compare(Alert alert, Alert alert2) {
                                    int expire2 = alert2.getExpiry().getSec();
                                    int expire1 = alert.getExpiry().getSec();
                                    return expire2 - expire1;
                                }
                            });
                    Log.d(TAG, merged.size() + " - size of merged array");
                    boolean hidden = !mPreferences.getBoolean("hide_expired", false);
                    if (mAdapter != null)
                        hidden = mAdapter.getShowHidden();
                    mAdapter = new AlertsListViewAdapter(activity, merged, mNoneView, hidden
                            , mFooterView);
                    mAlertView.setAdapter(mAdapter);
                    if (error) {
                        Toast.makeText(activity, R.string.error_error_occurred, Toast.LENGTH_SHORT).show();
                    } else {
                        (new PollingAlarmReceiver()).onReceive(activity, new Intent().putExtra("force", true));
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

    //region GCM Stuff
    private String getRegistrationId(Context context) {
        return getRegistrationId(context, true);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context, boolean versionCheck) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString("gcm_reg_id", "");
        Log.d(TAG, "regId=" + registrationId);
        if (registrationId.trim().length() == 0) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("gcm_app_version", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion && versionCheck) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        if (context == null)
            return null;
        return context.getSharedPreferences("gcm", Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String msg = "";
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getActivity());
                    String regid = gcm.register("338009375920");
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend(regid);

                    storeRegistrationId(getActivity(), regid);
                } catch (Exception ex) {
                    //if we fail and attempt to register again, we get the same id, so no problem.
                    msg = "Error :" + ex.getMessage();
                    ex.printStackTrace();
                    Log.e(TAG, ex.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void voi) {
            }
        }.execute();
    }

    private void storeRegistrationId(Context context, String regId) {
        if (context == null) {
            return;
        }
        final SharedPreferences prefs = getGCMPreferences(context);
        if (prefs == null) {
            return;
        }
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        Log.i(TAG, "regId=" + regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gcm_reg_id", regId);
        editor.putInt("gcm_app_version", appVersion);
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * to send messages to your app.
     */
    private void sendRegistrationIdToBackend(String id) throws IOException {
        String response = Http.get("http://deathsnacks.com/api/wardroid/registerPush.php?id=" + id);
        if (!response.contains("success:") && !response.contains("already exists"))
            throw new IOException("Failed to send gcm id back to server. " + response);
    }
    //endregion
}

