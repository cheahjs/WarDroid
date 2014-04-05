package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
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
public class NewsListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mLines;
    private LayoutInflater mInflater;

    public NewsListViewAdapter(Activity act, List<String> data) {
        mActivity = act;
        mLines = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public String getOriginalValues() {
        return TextUtils.join("\n", mLines);
    }

    public int getCount() {
        return mLines.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_news, null);
        TextView text = (TextView) view.findViewById(R.id.news_text);
        TextView duration = (TextView) view.findViewById(R.id.news_duration);
        TextView url = (TextView) view.findViewById(R.id.news_url);

        String line = mLines.get(position);
        String[] parts = line.split("\\|");
        if (parts.length != 4) {
            View dedView = new View(mActivity);
            dedView.setVisibility(View.GONE);
            return dedView;
        }
        text.setText(parts[3]);
        int activation = Integer.parseInt(parts[2]);
        long now = (long) (System.currentTimeMillis() / 1000);
        long diff = now - activation;
        long days = (long) Math.floor(diff / 86400);
        long hours = (long) Math.floor(diff / 3600) % 24;
        long minutes = (diff / 60 % 60);
        long used = minutes;
        String usedstr = "m";
        if (hours >= 1) {
            used = hours;
            usedstr = "h";
        }
        if (days >= 1) {
            used = days;
            usedstr = "d";
        }
        duration.setText(String.format("[%d%s]", used, usedstr));
        url.setText(parts[1]);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
