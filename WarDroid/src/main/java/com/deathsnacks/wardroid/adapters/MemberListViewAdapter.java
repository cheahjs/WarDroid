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
import com.deathsnacks.wardroid.gson.ActiveDrone;
import com.deathsnacks.wardroid.gson.ActiveDrones;
import com.deathsnacks.wardroid.gson.Guild;
import com.deathsnacks.wardroid.gson.Member;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 25/01/14.
 */
public class MemberListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private Guild mGuild;
    private LayoutInflater mInflater;

    public MemberListViewAdapter(Activity act, Guild data) {
        mActivity = act;
        mGuild = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mGuild.getMembers().size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_drone, null);
        TextView duration = (TextView) view.findViewById(R.id.drone_duration);

        Member member = mGuild.getMembers().get(position);
        view.setTag(member);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
