package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class AlertsListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mLines;
    private LayoutInflater mInflater;

    public AlertsListViewAdapter(Activity act, List<String> data) {
        mActivity = act;
        mLines = data;
        mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mLines.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_alert, null);
        TextView node = (TextView)view.findViewById(R.id.alert_title);
        TextView desc = (TextView)view.findViewById(R.id.alert_desc);
        TextView duration = (TextView)view.findViewById(R.id.alert_duration);
        TextView rewards = (TextView)view.findViewById(R.id.alert_rewards);

        String line = mLines.get(position);
        String[] parts = line.split("\\|");
        node.setText(String.format("%s (%s)", parts[1], parts[2]));
        desc.setText(String.format("%s | %s (%s)", parts[10], parts[3], parts[4]));
        rewards.setText(parts[9]);
        int activation = Integer.parseInt(parts[7]);
        int expiry = Integer.parseInt(parts[8]);
        long now = (long)(System.currentTimeMillis()/1000);
        long diff = expiry - now;
        //TODO: use an actual timer
        duration.setText(String.format("%dh %dm %ds", (long)Math.floor(diff / 3600), (diff/60 % 60), diff % 60));
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
