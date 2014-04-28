package com.deathsnacks.wardroid.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.adapters.BadlandsListViewAdapter;
import com.deathsnacks.wardroid.gson.badlands.BadlandNode;
import com.google.gson.Gson;

/**
 * Created by Admin on 4/28/14.
 */
public class BadlandDialogFragment extends DialogFragment {
    private BadlandNode node;

    public BadlandDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        node = (new Gson()).fromJson(getArguments().getString("node"), BadlandNode.class);
        getDialog().setCanceledOnTouchOutside(true);
        View view = inflater.inflate(R.layout.dialog_fragment_badlands, container);
        ChildViewHolder holder;
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

        getDialog().setTitle(String.format("%s (%s)", node.getNodeDisplayName(), node.getNodeRegionName()));

        holder.defending_holder.name.setText(Html.fromHtml("<u>" + TextUtils.htmlEncode(node.getDefenderInfo().getName()) + "</u>"));
        String motd = node.getDefenderInfo().getMOTD();
        if (TextUtils.isEmpty(motd))
            motd = getActivity().getString(R.string.nomessage);
        holder.defending_holder.message.setText(motd);
        holder.defending_holder.public_tax.setText(
                Html.fromHtml(
                        String.format(getActivity().getString(R.string.public_tax),
                                node.getDefenderInfo().getCreditsTaxRate(), node.getDefenderInfo().getItemsTaxRate()).replace("%%", "%")));
        holder.defending_holder.member_tax.setText(
                Html.fromHtml(
                        String.format(getActivity().getString(R.string.member_tax),
                                node.getDefenderInfo().getMemberCreditsTaxRate(), node.getDefenderInfo().getMemberItemsTaxRate()).replace("%%", "%")));
        double defhealth = ((double)node.getDefenderInfo().getStrengthRemaining() / (double)node.getDefenderInfo().getMaxStrength())*100;
        holder.defending_holder.health.setText(String.format("%.2f%%", defhealth));
        holder.defending_holder.healthbar.setProgress((int)defhealth);
        int defbattlepay = node.getDefenderInfo().getMissionBattlePay();
        holder.defending_holder.battlepay.setText(Html.fromHtml(
                String.format(getActivity().getString(R.string.battle_pay),
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
            holder.defending_holder.battlepay.setVisibility(View.VISIBLE);
            holder.attacking_holder.name.setText(Html.fromHtml("<u>" + TextUtils.htmlEncode(node.getAttackerInfo().getName()) + "</u>"));
            motd = node.getAttackerInfo().getMOTD();
            if (TextUtils.isEmpty(motd))
                motd = getActivity().getString(R.string.nomessage);
            holder.attacking_holder.message.setText(motd);
            holder.attacking_holder.public_tax.setText(
                    Html.fromHtml(
                            String.format(getActivity().getString(R.string.public_tax),
                                    node.getAttackerInfo().getCreditsTaxRate(), node.getAttackerInfo().getItemsTaxRate()).replace("%%", "%")));
            holder.attacking_holder.member_tax.setText(
                    Html.fromHtml(
                            String.format(getActivity().getString(R.string.member_tax),
                                    node.getAttackerInfo().getMemberCreditsTaxRate(), node.getAttackerInfo().getMemberItemsTaxRate()).replace("%%", "%")));
            double atkhealth = ((double)node.getAttackerInfo().getStrengthRemaining() / (double)node.getAttackerInfo().getMaxStrength())*100;
            holder.attacking_holder.health.setText(String.format("%.2f%%", atkhealth));
            holder.attacking_holder.healthbar.setProgress((int)atkhealth);
            int atkbattlepay = node.getAttackerInfo().getMissionBattlePay();
            holder.attacking_holder.battlepay.setText(Html.fromHtml(
                    String.format(getActivity().getString(R.string.battle_pay),
                            atkbattlepay, (atkbattlepay != 0 ? node.getAttackerInfo().getBattlePayReserve() / atkbattlepay : 0))));
        }
        return view;
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
