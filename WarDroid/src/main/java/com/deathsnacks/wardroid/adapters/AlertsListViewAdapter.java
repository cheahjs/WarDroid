package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.Alert;
import com.deathsnacks.wardroid.utils.Names;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class AlertsListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Alert> mAlerts;
    private LayoutInflater mInflater;

    public AlertsListViewAdapter(Activity act, List<Alert> data) {
        mActivity = act;
        mAlerts = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mAlerts.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_alert, null);
        TextView node = (TextView) view.findViewById(R.id.alert_title);
        TextView desc = (TextView) view.findViewById(R.id.alert_desc);
        TextView duration = (TextView) view.findViewById(R.id.alert_duration);
        TextView rewards = (TextView) view.findViewById(R.id.alert_rewards);

        Alert alert = mAlerts.get(position);
        node.setText(String.format("%s (%s)", Names.getNode(mActivity, alert.getMissionInfo().getLocation()),
                Names.getRegion(mActivity, alert.getMissionInfo().getLocation())));
        String descTxt = String.format("%s | %s (%s)", Names.getString(mActivity, alert.getMissionInfo().getDescText()),
                Names.getFaction(alert.getMissionInfo().getFaction()),
                Names.getMissionType(alert.getMissionInfo().getMissionType()));
        desc.setText(Names.getString(mActivity, descTxt));
        rewards.setText(alert.getMissionInfo().getMissionReward().getRewardString());
        long now = (long) (System.currentTimeMillis() / 1000);
        long expiry = alert.getExpiry().getSec();
        long activation = alert.getActivation().getSec();
        String format = mActivity.getString(R.string.alert_starting);
        duration.setTextColor(Color.parseColor("#343434"));
        long diff = activation - now;
        if (diff < 0) {
            diff = expiry - now;
            format = "%dh %dm %ds";
            duration.setTextColor(Color.parseColor("#10bcc9"));
            if (diff < 0) {
                diff = now - expiry;
                format = mActivity.getString(R.string.alert_expired);
                duration.setTextColor(Color.parseColor("#d9534f"));
            }
        }
        duration.setText(String.format(format, (long) Math.floor(diff / 3600), (diff / 60 % 60), diff % 60));
        view.setTag(alert);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
