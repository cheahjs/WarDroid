package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.badlands.BadlandNode;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class BadlandsListViewAdapter extends BaseAdapter {
    private static final String TAG = "BadlandsListCiewAdapter";
    private Activity mActivity;
    private List<BadlandNode> mBadlands;
    private LayoutInflater mInflater;

    public BadlandsListViewAdapter(Activity act, List<BadlandNode> data) {
        mActivity = act;
        mBadlands = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mBadlands.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_parent_badlands, null);
            holder = new GroupViewHolder();
            holder.node = (TextView) view.findViewById(R.id.bl_node);
            holder.status = (TextView) view.findViewById(R.id.conflict_status);
            holder.time = (TextView) view.findViewById(R.id.conflict_duration);
            holder.mode = (TextView) view.findViewById(R.id.bl_mode);
            holder.atk_pay = (TextView) view.findViewById(R.id.attacker_pay);
            holder.def_pay = (TextView) view.findViewById(R.id.defender_pay);
            holder.pay_holder = view.findViewById(R.id.pay_holder);
            holder.default_color = holder.status.getCurrentTextColor();
        } else {
            holder = (GroupViewHolder) view.getTag();
            if (holder == null) {
                holder = new GroupViewHolder();
                holder.node = (TextView) view.findViewById(R.id.bl_node);
                holder.status = (TextView) view.findViewById(R.id.conflict_status);
                holder.time = (TextView) view.findViewById(R.id.conflict_duration);
                holder.mode = (TextView) view.findViewById(R.id.bl_mode);
                holder.atk_pay = (TextView) view.findViewById(R.id.attacker_pay);
                holder.def_pay = (TextView) view.findViewById(R.id.defender_pay);
                holder.pay_holder = view.findViewById(R.id.pay_holder);
                holder.default_color = holder.status.getCurrentTextColor();
            }
        }
        holder.status.setTextColor(holder.default_color);
        holder.pay_holder.setVisibility(View.GONE);
        BadlandNode node = mBadlands.get(i);
        holder.node.setText(node.getNodeDisplayName() + " (" + node.getNodeRegionName() + ")");
        holder.mode.setText(node.getNodeGameType());
        if (node.getAttackerInfo() == null || (node.getConflictExpiration() != null
                && node.getConflictExpiration().getSec() < System.currentTimeMillis()/1000)) {
            if (node.getPostConflictCooldown() != null && node.getPostConflictCooldown().getSec() > System.currentTimeMillis()/1000) {
                holder.time.setVisibility(View.VISIBLE);
                String time_format = mActivity.getString(R.string.bl_armistice_time);
                long diff = node.getPostConflictCooldown().getSec() - System.currentTimeMillis()/1000;
                holder.time.setText(String.format(time_format, (long) Math.floor(diff / 3600), (diff / 60 % 60), diff % 60));
                holder.status.setText(R.string.armistice);
            } else {
                holder.status.setText("");
                holder.time.setVisibility(View.GONE);
            }
        } else {
            holder.time.setVisibility(View.VISIBLE);
            if (node.getAttackerInfo().getDeploymentActivationTime().getSec() > System.currentTimeMillis()/1000) {
                String time_format = mActivity.getString(R.string.bl_deploying_time);
                long diff = node.getAttackerInfo().getDeploymentActivationTime().getSec() - System.currentTimeMillis()/1000;
                holder.time.setText(String.format(time_format, (long) Math.floor(diff / 3600), (diff / 60 % 60), diff % 60));
                holder.status.setText(R.string.deploying);
            } else {
                String time_format = mActivity.getString(R.string.bl_expiration_time);
                long diff = node.getConflictExpiration().getSec() - System.currentTimeMillis()/1000;
                holder.time.setText(String.format(time_format, (long) Math.floor(diff / 3600), (diff / 60 % 60), diff % 60));
                holder.status.setText(R.string.in_conflict);
                holder.status.setTextColor(ColorStateList.valueOf(Color.parseColor("#d9534f")));
                int defbattlepay = node.getDefenderInfo().getMissionBattlePay();
                holder.def_pay.setText(Html.fromHtml(
                        String.format(mActivity.getString(R.string.defender_pay),
                                defbattlepay, (defbattlepay != 0 ? node.getDefenderInfo().getBattlePayReserve() / defbattlepay : 0))));
                int atkbattlepay = node.getAttackerInfo().getMissionBattlePay();
                holder.atk_pay.setText(Html.fromHtml(
                        String.format(mActivity.getString(R.string.attacker_pay),
                                atkbattlepay, (atkbattlepay != 0 ? node.getAttackerInfo().getBattlePayReserve() / atkbattlepay : 0))));
                holder.pay_holder.setVisibility(View.VISIBLE);
            }
        }
        holder.bl_node = node;
        view.setTag(holder);
        return view;
    }

    public String getOriginalValues() {
        return (new GsonBuilder().create()).toJson(mBadlands);
    }

    public class GroupViewHolder {
        public TextView node;
        public TextView status;
        public TextView mode;
        public View pay_holder;
        public TextView def_pay;
        public TextView atk_pay;
        public ImageView expand;
        public ImageView collapse;
        public TextView time;
        public BadlandNode bl_node;
        public int default_color;
    }
}
