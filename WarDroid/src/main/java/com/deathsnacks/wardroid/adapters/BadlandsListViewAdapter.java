package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_parent_badlands, null);
            holder = new GroupViewHolder();
            holder.node = (TextView) view.findViewById(R.id.bl_node);
            holder.status = (TextView) view.findViewById(R.id.conflict_status);
        } else {
            holder = (GroupViewHolder) view.getTag();
            if (holder == null) {
                holder = new GroupViewHolder();
                holder.node = (TextView) view.findViewById(R.id.bl_node);
                holder.status = (TextView) view.findViewById(R.id.conflict_status);
            }
        }
        //holder.status.setVisibility(View.GONE);

        BadlandNode node = mBadlands.get(groupPos);
        holder.node.setText(node.getNodeDisplayName() + " (" + node.getNodeRegionName() + ")");
        view.setTag(holder);
        return view;
    }

    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_parent_badlands, null);
            holder = new GroupViewHolder();
            holder.node = (TextView) view.findViewById(R.id.bl_node);
            holder.status = (TextView) view.findViewById(R.id.conflict_status);
            holder.expand = (ImageView) view.findViewById(R.id.ic_expand);
            holder.collapse = (ImageView) view.findViewById(R.id.ic_collapse);
        } else {
            holder = (GroupViewHolder) view.getTag();
            if (holder == null) {
                holder = new GroupViewHolder();
                holder.node = (TextView) view.findViewById(R.id.bl_node);
                holder.status = (TextView) view.findViewById(R.id.conflict_status);
            }
        }
        //holder.status.setVisibility(View.GONE);
        if (isExpanded) {
            holder.expand.setVisibility(View.GONE);
            holder.collapse.setVisibility(View.VISIBLE);
        } else {
            holder.expand.setVisibility(View.VISIBLE);
            holder.collapse.setVisibility(View.GONE);
        }

        BadlandNode node = mBadlands.get(groupPos);
        holder.node.setText(node.getNodeDisplayName() + " (" + node.getNodeRegionName() + ")");
        view.setTag(holder);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return mBadlands.size();
    }

    @Override
    public int getChildrenCount(int groupPos) {
        return 1;
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
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public class GroupViewHolder {
        public TextView node;
        public TextView status;
        public ImageView expand;
        public ImageView collapse;
    }
}
