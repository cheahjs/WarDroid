package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.badlands.BadlandNode;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class BadlandsListViewAdapter extends BaseExpandableListAdapter {
    private Activity mActivity;
    private List<BadlandNode> mBadlands;
    private LayoutInflater mInflater;

    public BadlandsListViewAdapter(Activity act, List<BadlandNode> data) {
        mActivity = act;
        mBadlands = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mBadlands.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i2) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }


    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
