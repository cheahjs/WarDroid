package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.alert.Alert;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.Utils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class AlertsListViewAdapter extends BaseAdapter {
    private static final String TAG = "AlertsListViewAdapter";
    private SharedPreferences mPreferences;
    private ArrayList<String> mCompletedIds;
    private Activity mActivity;
    private List<Alert> mAlerts;
    private List<Alert> mOriginal;
    private LayoutInflater mInflater;
    private View mEmptyView;
    private View mFooterView;
    private boolean mShowHidden;
    private boolean mUpdate;

    public AlertsListViewAdapter(Activity act, List<Alert> data, View emptyView, boolean showHidden, View footerView) {
        mUpdate = false;
        mActivity = act;
        mShowHidden = showHidden;
        mFooterView = footerView;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        mAlerts = new ArrayList<Alert>();
        mAlerts.addAll(data);
        mOriginal = data;
        Log.d(TAG, "alerts size: " + mOriginal.size());
        List<Alert> newList = new ArrayList<Alert>();
        for (int i = 0; i < mOriginal.size(); i++) {
            Alert alert = mOriginal.get(i);
            if (mPreferences.getBoolean("hide_completed", false)) {
                if (mCompletedIds.contains(alert.get_id().get$id())) {
                    Log.d(TAG, "marking alert GONE. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            if (alert.isPc()) {
                if (!mPreferences.getString("platform", "pc").contains("pc")) {
                    Log.d(TAG, "not showing alert, PC alert, no PC. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            } else {
                if (!mPreferences.getString("platform", "pc").contains("ps4")) {
                    Log.d(TAG, "not showing alert, PS4 alert, no PS4. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            if (!mShowHidden) {
                if (alert.getExpiry().getSec() < (System.currentTimeMillis() / 1000)) {
                    Log.d(TAG, "marking alert expired. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            newList.add(alert);
        }
        mAlerts = newList;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmptyView = emptyView;
        if (mAlerts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            //mFooterView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            //mFooterView.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "alerts size: " + mOriginal.size());
    }

    public String getOriginalValues() {
        return (new GsonBuilder().create()).toJson(mOriginal);
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d(TAG, "show hidden:" + mShowHidden);
        Log.d(TAG, "alerts size: " + mOriginal.size());
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));

        List<Alert> newList = new ArrayList<Alert>();
        for (int i = 0; i < mOriginal.size(); i++) {
            Alert alert = mOriginal.get(i);
            if (mPreferences.getBoolean("hide_completed", false)) {
                if (mCompletedIds.contains(alert.get_id().get$id())) {
                    Log.d(TAG, "marking alert GONE. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            if (alert.isPc()) {
                if (!mPreferences.getString("platform", "pc").contains("pc")) {
                    Log.d(TAG, "not showing alert, PC alert, no PC. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            } else {
                if (!mPreferences.getString("platform", "pc").contains("ps4")) {
                    Log.d(TAG, "not showing alert, PS4 alert, no PS4. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            if (!mShowHidden) {
                if (alert.getExpiry().getSec() < (System.currentTimeMillis() / 1000)) {
                    Log.d(TAG, "marking alert expired. " + alert.getMissionInfo().getLocation());
                    continue;
                }
            }
            newList.add(alert);
        }
        mAlerts = newList;
        if (mAlerts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            //mFooterView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            //mFooterView.setVisibility(View.VISIBLE);
        }
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChangedLight() {
        if (mUpdate) {
            notifyDataSetChanged();
            mUpdate = false;
        } else {
            super.notifyDataSetChanged();
        }
    }

    public int getCount() {
        return mAlerts.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_alert, null);
            holder = new ViewHolder();
            holder.node = (TextView) view.findViewById(R.id.alert_title);
            holder.completed = view.findViewById(R.id.alert_completed);
            holder.desc = (TextView) view.findViewById(R.id.alert_desc);
            holder.duration = (TextView) view.findViewById(R.id.alert_duration);
            holder.rewards = (TextView) view.findViewById(R.id.alert_rewards);
        } else {
            holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.node = (TextView) view.findViewById(R.id.alert_title);
                holder.completed = view.findViewById(R.id.alert_completed);
                holder.desc = (TextView) view.findViewById(R.id.alert_desc);
                holder.duration = (TextView) view.findViewById(R.id.alert_duration);
                holder.rewards = (TextView) view.findViewById(R.id.alert_rewards);
            }
        }
        view.setVisibility(View.VISIBLE);
        view.setEnabled(true);
        holder.completed.setVisibility(View.GONE);

        if (position >= mAlerts.size()) {
            Log.e(TAG, "We are above size of alerts array: " + position + " (" + mAlerts.size() + "), setting GONE");
            view.setVisibility(view.GONE);
            view.setEnabled(false);
            view.setTag(holder);
            return view;
        }

        Alert alert = mAlerts.get(position);
        holder.alert = alert;
        if (mCompletedIds.contains(alert.get_id().get$id())) {
            if (mPreferences.getBoolean("hide_completed", false)) {
                mUpdate = true;
                view.setVisibility(View.GONE);
                Log.d(TAG, "marking alert GONE." + alert.getMissionInfo().getLocation());
                return view;
            } else {
                holder.completed.setVisibility(View.VISIBLE);
            }
        }
        holder.node.setText(alert.getMissionInfo().getLocation());
        String descTxt = String.format("%s | %s (%s)", alert.getMissionInfo().getDescText(),
                alert.getMissionInfo().getFaction(), alert.getMissionInfo().getMissionType());
        holder.desc.setText(descTxt + (alert.isPc() ? " (PC)" : " (PS4)"));
        holder.rewards.setText(Utils.getImageSpannable(mActivity, alert.getMissionInfo().getMissionReward().getRewardString()));
        long now = (long) (System.currentTimeMillis() / 1000);
        long expiry = alert.getExpiry().getSec();
        long activation = alert.getActivation().getSec();
        String format = mActivity.getString(R.string.alert_starting);
        holder.duration.setTextColor(Color.parseColor("#343434"));
        long diff = activation - now;
        if (diff < 0) {
            diff = expiry - now;
            format = "%dh %dm %ds";
            holder.duration.setTextColor(Color.parseColor("#10bcc9"));
            if (diff < 0) {
                if (!mShowHidden) {
                    mUpdate = true;
                    view.setVisibility(View.GONE);
                    Log.d(TAG, "marking alert expired. " + alert.getMissionInfo().getLocation());
                    return view;
                }
                diff = now - expiry;
                format = mActivity.getString(R.string.alert_expired);
                holder.duration.setTextColor(Color.parseColor("#d9534f"));
            }
        }
        holder.duration.setText(String.format(format, (long) Math.floor(diff / 3600), (diff / 60 % 60), diff % 60));
        view.setTag(holder);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public TextView node;
        public TextView desc;
        public TextView duration;
        public TextView rewards;
        public View completed;
        public Alert alert;
    }

    public boolean getShowHidden() {
        return mShowHidden;
    }

    public void setShowHidden(boolean show) {
        mShowHidden = show;
    }
}
