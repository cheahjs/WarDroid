package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.badlands.BadlandNode;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class BadlandsListViewAdapter extends BaseExpandableListAdapter {
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
    public View getChildView(int groupPos, int childPos, boolean isLastChild, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_child_badlands, null);
            holder = new ChildViewHolder();
            holder.attacking_holder = new ClanViewHolder();
            holder.defending_holder = new ClanViewHolder();
            holder.defending_holder.name = (TextView) view.findViewById(R.id.defending_name);
            holder.defending_holder.message = (TextView) view.findViewById(R.id.defending_message);
            holder.defending_holder.public_tax = (TextView) view.findViewById(R.id.defending_public_tax);
            holder.defending_holder.member_tax = (TextView) view.findViewById(R.id.defending_member_tax);
            holder.defending_holder.battlepay = (TextView) view.findViewById(R.id.defending_battlepay);
            holder.defending_holder.healthbar = (ProgressBar) view.findViewById(R.id.defender_health_bar);
            holder.defending_holder.container = (LinearLayout) view.findViewById(R.id.badlands_defender_container);
            holder.defending_holder.health = (TextView) view.findViewById(R.id.defender_health);
            holder.attacking_holder.name = (TextView) view.findViewById(R.id.attacking_name);
            holder.attacking_holder.message = (TextView) view.findViewById(R.id.attacking_message);
            holder.attacking_holder.public_tax = (TextView) view.findViewById(R.id.attacking_public_tax);
            holder.attacking_holder.member_tax = (TextView) view.findViewById(R.id.attacking_member_tax);
            holder.attacking_holder.battlepay = (TextView) view.findViewById(R.id.attacking_battlepay);
            holder.attacking_holder.healthbar = (ProgressBar) view.findViewById(R.id.attacker_health_bar);
            holder.attacking_holder.health = (TextView) view.findViewById(R.id.attacker_health);
            holder.attacking_holder.container = (LinearLayout) view.findViewById(R.id.badlands_attacker_container);
        } else {
            holder = (ChildViewHolder) view.getTag();
            if (holder == null) {
                holder = new ChildViewHolder();
                holder.attacking_holder = new ClanViewHolder();
                holder.defending_holder = new ClanViewHolder();
                holder.defending_holder.name = (TextView) view.findViewById(R.id.defending_name);
                holder.defending_holder.message = (TextView) view.findViewById(R.id.defending_message);
                holder.defending_holder.public_tax = (TextView) view.findViewById(R.id.defending_public_tax);
                holder.defending_holder.member_tax = (TextView) view.findViewById(R.id.defending_member_tax);
                holder.defending_holder.battlepay = (TextView) view.findViewById(R.id.defending_battlepay);
                holder.defending_holder.healthbar = (ProgressBar) view.findViewById(R.id.defender_health_bar);
                holder.defending_holder.health = (TextView) view.findViewById(R.id.defender_health);
                holder.defending_holder.container = (LinearLayout) view.findViewById(R.id.badlands_defender_container);
                holder.attacking_holder.name = (TextView) view.findViewById(R.id.attacking_name);
                holder.attacking_holder.message = (TextView) view.findViewById(R.id.attacking_message);
                holder.attacking_holder.public_tax = (TextView) view.findViewById(R.id.attacking_public_tax);
                holder.attacking_holder.member_tax = (TextView) view.findViewById(R.id.attacking_member_tax);
                holder.attacking_holder.battlepay = (TextView) view.findViewById(R.id.attacking_battlepay);
                holder.attacking_holder.healthbar = (ProgressBar) view.findViewById(R.id.attacker_health_bar);
                holder.attacking_holder.health = (TextView) view.findViewById(R.id.attacker_health);
                holder.attacking_holder.container = (LinearLayout) view.findViewById(R.id.badlands_attacker_container);
            }
        }
        //holder.status.setVisibility(View.GONE);

        BadlandNode node = mBadlands.get(groupPos);
        holder.defending_holder.name.setText(Html.fromHtml("<u>" + TextUtils.htmlEncode(node.getDefenderInfo().getName()) + "</u>"));
        String motd = node.getDefenderInfo().getMOTD();
        if (TextUtils.isEmpty(motd))
            motd = mActivity.getString(R.string.nomessage);
        holder.defending_holder.message.setText(motd);
        holder.defending_holder.public_tax.setText(
                Html.fromHtml(
                        String.format(mActivity.getString(R.string.public_tax),
                                node.getDefenderInfo().getCreditsTaxRate(), node.getDefenderInfo().getItemsTaxRate()).replace("%%", "%")));
        holder.defending_holder.member_tax.setText(
                Html.fromHtml(
                        String.format(mActivity.getString(R.string.member_tax),
                                node.getDefenderInfo().getMemberCreditsTaxRate(), node.getDefenderInfo().getMemberItemsTaxRate()).replace("%%", "%")));
        double defhealth = ((double)node.getDefenderInfo().getStrengthRemaining() / (double)node.getDefenderInfo().getMaxStrength())*100;
        holder.defending_holder.health.setText(String.format("%.2f%%", defhealth));
        holder.defending_holder.healthbar.setProgress((int)defhealth);
        int defbattlepay = node.getDefenderInfo().getMissionBattlePay();
        holder.defending_holder.battlepay.setText(Html.fromHtml(
                String.format(mActivity.getString(R.string.battle_pay),
                        defbattlepay, (defbattlepay != 0 ? node.getDefenderInfo().getBattlePayReserve() / defbattlepay : 0))));
        // We are not in conflict, hide everything
        if (node.getAttackerInfo() == null || (node.getConflictExpiration() != null
        && node.getConflictExpiration().getSec() < System.currentTimeMillis()/1000)) {
            holder.attacking_holder.container.setVisibility(View.GONE);
            holder.defending_holder.battlepay.setVisibility(View.GONE);
        } else {
            //we are in conflict/deploying, start showing things
            holder.attacking_holder.container.setVisibility(View.VISIBLE);
            holder.attacking_holder.battlepay.setVisibility(View.VISIBLE);
            holder.attacking_holder.name.setText(Html.fromHtml("<u>" + TextUtils.htmlEncode(node.getAttackerInfo().getName()) + "</u>"));
            motd = node.getAttackerInfo().getMOTD();
            if (TextUtils.isEmpty(motd))
                motd = mActivity.getString(R.string.nomessage);
            holder.attacking_holder.message.setText(motd);
            holder.attacking_holder.public_tax.setText(
                    Html.fromHtml(
                            String.format(mActivity.getString(R.string.public_tax),
                                    node.getAttackerInfo().getCreditsTaxRate(), node.getAttackerInfo().getItemsTaxRate()).replace("%%", "%")));
            holder.attacking_holder.member_tax.setText(
                    Html.fromHtml(
                            String.format(mActivity.getString(R.string.member_tax),
                                    node.getAttackerInfo().getMemberCreditsTaxRate(), node.getAttackerInfo().getMemberItemsTaxRate()).replace("%%", "%")));
            double atkhealth = ((double)node.getAttackerInfo().getStrengthRemaining() / (double)node.getAttackerInfo().getMaxStrength())*100;
            holder.attacking_holder.health.setText(String.format("%.2f%%", atkhealth));
            holder.attacking_holder.healthbar.setProgress((int)atkhealth);
            int atkbattlepay = node.getAttackerInfo().getMissionBattlePay();
            holder.attacking_holder.battlepay.setText(Html.fromHtml(
                    String.format(mActivity.getString(R.string.battle_pay),
                            atkbattlepay, (atkbattlepay != 0 ? node.getAttackerInfo().getBattlePayReserve() / atkbattlepay : 0))));
        }
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
            holder.time = (TextView) view.findViewById(R.id.conflict_duration);
        } else {
            holder = (GroupViewHolder) view.getTag();
            if (holder == null) {
                holder = new GroupViewHolder();
                holder.node = (TextView) view.findViewById(R.id.bl_node);
                holder.status = (TextView) view.findViewById(R.id.conflict_status);
                holder.expand = (ImageView) view.findViewById(R.id.ic_expand);
                holder.collapse = (ImageView) view.findViewById(R.id.ic_collapse);
                holder.time = (TextView) view.findViewById(R.id.conflict_duration);
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
        holder.node.setText(node.getNodeDisplayName() + " (" + node.getNodeRegionName() + ")" + " [" + node.getNodeGameType() + "]");
        if (node.getAttackerInfo() == null || (node.getConflictExpiration() != null
                && node.getConflictExpiration().getSec() < System.currentTimeMillis()/1000)) {
            holder.status.setText("");
            holder.time.setVisibility(View.GONE);
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
            }
        }
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

    public String getOriginalValues() {
        return (new GsonBuilder().create()).toJson(mBadlands);
    }

    public class GroupViewHolder {
        public TextView node;
        public TextView status;
        public ImageView expand;
        public ImageView collapse;
        public TextView time;
    }

    public class ChildViewHolder {
        public ClanViewHolder defending_holder;
        public ClanViewHolder attacking_holder;
    }

    public class ClanViewHolder {
        public LinearLayout container;
        public TextView name;
        public TextView message;
        public TextView public_tax;
        public TextView member_tax;
        public TextView battlepay;
        public ProgressBar healthbar;
        public TextView health;
    }
}
