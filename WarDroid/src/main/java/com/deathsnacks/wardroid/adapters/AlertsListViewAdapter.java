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
import com.deathsnacks.wardroid.gson.Alert;
import com.deathsnacks.wardroid.utils.Names;
import com.deathsnacks.wardroid.utils.PreferenceUtils;

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
    private LayoutInflater mInflater;
    private View mEmptyView;

    public AlertsListViewAdapter(Activity act, List<Alert> data, View emptyView) {
        mActivity = act;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        mAlerts = data;
        if (mPreferences.getBoolean("hide_completed", false)) {
            List<Alert> newList = new ArrayList<Alert>();
            for (int i = 0; i < mAlerts.size(); i++) {
                Alert alert = mAlerts.get(i);
                if (mCompletedIds.contains(alert.get_id().get$id())) {
                    Log.d(TAG, "marking alert GONE. " + alert.getMissionInfo().getLocation());
                } else {
                    newList.add(alert);
                }
            }
            mAlerts = newList;
        }
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmptyView = emptyView;
        if (mAlerts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("alert_completed_ids", ""))));
        if (mPreferences.getBoolean("hide_completed", false)) {
            List<Alert> newList = new ArrayList<Alert>();
            for (int i = 0; i < mAlerts.size(); i++) {
                Alert alert = mAlerts.get(i);
                if (mCompletedIds.contains(alert.get_id().get$id())) {
                    Log.d(TAG, "marking alert GONE. " + alert.getMissionInfo().getLocation());
                } else {
                    newList.add(alert);
                }
            }
            mAlerts = newList;
        }
        if (mAlerts.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChangedLight() {
        super.notifyDataSetChanged();
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
                mAlerts.remove(position);
                notifyDataSetChanged();
                view.setVisibility(View.GONE);
                Log.d(TAG, "marking alert GONE." + alert.getMissionInfo().getLocation());
                return view;
            } else {
                holder.completed.setVisibility(View.VISIBLE);
            }
        }
        holder.node.setText(String.format("%s (%s)", Names.getNode(mActivity, alert.getMissionInfo().getLocation()),
                Names.getRegion(mActivity, alert.getMissionInfo().getLocation())));
        String descTxt = String.format("%s | %s (%s)", Names.getString(mActivity, alert.getMissionInfo().getDescText()),
                Names.getFaction(alert.getMissionInfo().getFaction()),
                Names.getMissionType(alert.getMissionInfo().getMissionType()));
        holder.desc.setText(Names.getString(mActivity, descTxt));
        holder.rewards.setText(alert.getMissionInfo().getMissionReward().getRewardString());
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
}
