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

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        List<Member> members = mGuild.getMembers();
        Collections.sort(members, new Comparator<Member>() {
            @Override
            public int compare(Member member, Member member2) {
                return member.getDisplayName().compareTo(member2.getDisplayName());
            }
        });
        mGuild.setMembers(members);
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mGuild.getMembers().size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_member, null);
        TextView name = (TextView) view.findViewById(R.id.member_name);
        TextView rank = (TextView) view.findViewById(R.id.member_rank);
        TextView last = (TextView) view.findViewById(R.id.member_last_login);

        Member member = mGuild.getMembers().get(position);
        long now = System.currentTimeMillis() / 1000;
        long diff = now - member.getLastLogin().getSec();
        long days = (long) Math.floor(diff / 86400);
        if (days > 0)
            last.setText(String.format("%dd %dh ago", (long) Math.floor(diff / 86400), (long) Math.floor(diff / 3600) % 24));
        else
            last.setText(String.format("%dh %dm ago", (long) Math.floor(diff / 3600) % 24, (diff / 60 % 60)));
        name.setText(member.getDisplayName());
        String rankTxt = mGuild.getRanks().get(member.getRank()).getName();
        if (member.getStatus() == 1)
            rankTxt += " (Pending)";
        rank.setText(rankTxt);
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
