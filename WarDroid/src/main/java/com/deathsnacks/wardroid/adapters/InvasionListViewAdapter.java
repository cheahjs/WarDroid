package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.utils.PreferenceUtils;
import com.deathsnacks.wardroid.utils.Utils;
import com.deathsnacks.wardroid.utils.httpclasses.Invasion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class InvasionListViewAdapter extends BaseAdapter {
    private ArrayList<String> mCompletedIds;
    private SharedPreferences mPreferences;
    private Activity mActivity;
    private List<String> mLines;
    private LayoutInflater mInflater;
    private static String TAG = "InvasionListViewAdapter";
    private View mEmptyView;

    public InvasionListViewAdapter(Activity act, List<String> data, View emptyView) {
        mActivity = act;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(act);
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        mLines = data;
        mLines.remove(0);
        if (mPreferences.getBoolean("hide_completed", false)) {
            List<String> newList = new ArrayList<String>();
            for (int i = 0; i < mLines.size(); i++) {
                String line = mLines.get(i);
                Invasion invasion = new Invasion(line);
                if (mCompletedIds.contains(invasion.getId())) {
                    Log.d(TAG, "marking invasion GONE. " + invasion.getNode());
                } else {
                    newList.add(line);
                }
            }
            mLines = newList;
        }
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEmptyView = emptyView;
        if (mLines.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        mCompletedIds = new ArrayList<String>(Arrays.asList(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("invasion_completed_ids", ""))));
        if (mPreferences.getBoolean("hide_completed", false)) {
            List<String> newList = new ArrayList<String>();
            for (int i = 0; i < mLines.size(); i++) {
                String line = mLines.get(i);
                Invasion invasion = new Invasion(line);
                if (mCompletedIds.contains(invasion.getId())) {
                    Log.d(TAG, "marking invasion GONE. " + invasion.getNode());
                } else {
                    newList.add(line);
                }
            }
            mLines = newList;
        }
        if (mLines.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return mLines.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        //define viewholder
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_invasion, null);
            holder = new ViewHolder();
            holder.completed = view.findViewById(R.id.invasion_completed);
            holder.node = (TextView) view.findViewById(R.id.invasion_node);
            holder.desc = (TextView) view.findViewById(R.id.invasion_desc);
            holder.percent = (TextView) view.findViewById(R.id.invasion_percent);
            holder.invadingfaction = (TextView) view.findViewById(R.id.invasion_invading_faction);
            holder.invadingtype = (TextView) view.findViewById(R.id.invasion_invading_type);
            holder.invadingrewards = (TextView) view.findViewById(R.id.invasion_invading_reward);
            holder.defendingfaction = (TextView) view.findViewById(R.id.invasion_defending_faction);
            holder.defendingtype = (TextView) view.findViewById(R.id.invasion_defending_type);
            holder.defendingrewards = (TextView) view.findViewById(R.id.invasion_defending_reward);
            holder.eta = (TextView) view.findViewById(R.id.invasion_eta);
            holder.bar = (ProgressBar) view.findViewById(R.id.invasion_bar);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        view.setEnabled(true);
        view.setVisibility(View.VISIBLE);
        holder.completed.setVisibility(View.GONE);

        //catch race conditions with hiding items
        if (position >= mLines.size()) {
            Log.e(TAG, "We are above size of invasion array: " + position + " (" + mLines.size() + "), setting GONE");
            view.setVisibility(View.GONE);
            view.setEnabled(false);
            view.setTag(holder);
            return view;
        }

        String line = mLines.get(position);
        String[] parts = line.split("\\|");
        //check if whatever we are parsing is valid
        if (parts.length != 19) {
            view.setVisibility(View.GONE);
            view.setEnabled(false);
            view.setTag(holder);
            return view;
        }

        Invasion invasion = new Invasion(line);
        holder.invasion = invasion;
        if (mCompletedIds.contains(invasion.getId())) {
            if (mPreferences.getBoolean("hide_completed", false)) {
                notifyDataSetChanged();
            } else {
                holder.completed.setVisibility(View.VISIBLE);
            }
        }
        holder.node.setText(String.format("%s (%s)", invasion.getNode(), invasion.getRegion()));
        holder.invadingfaction.setText(invasion.getInvadingFaction());
        holder.invadingtype.setText(invasion.getInvadingFaction().contains("Infestation") ? "" : invasion.getInvadingType());
        String invadingReward = invasion.getInvadingReward();
        if (invadingReward.endsWith("cr"))
            invadingReward = invadingReward.replaceAll("cr$", "*c");
        holder.invadingrewards.setText(invasion.getInvadingFaction().contains("Infestation") ? ""
                : Utils.getImageSpannable(mActivity, invadingReward));
        holder.defendingfaction.setText(invasion.getDefendingFaction());
        holder.defendingtype.setText(invasion.getDefendingType());
        String defendingReward = invasion.getDefendingReward();
        if (defendingReward.endsWith("cr")) {
            defendingReward = defendingReward.replaceAll("cr$", "*c");
        }
        holder.defendingrewards.setText(Utils.getImageSpannable(mActivity, defendingReward));
        holder.percent.setText(invasion.getPercent() + "%");
        holder.desc.setText(invasion.getDescription());
        int percentvalue = (int) Double.parseDouble(invasion.getPercent());
        if (percentvalue > 100)
            percentvalue = 100;
        if (percentvalue < 0)
            percentvalue = 0;
        holder.bar.setProgress(0);
        Rect bounds = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            bounds = holder.bar.getProgressDrawable().getBounds();
            holder.bar.getProgressDrawable().setBounds(bounds);
        }
        if (invasion.getInvadingFaction().contains("Grineer"))
            holder.bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.grineer_corpus_bar));
        else if (invasion.getInvadingFaction().contains("Corpus"))
            holder.bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.corpus_grineer_bar));
        else if (invasion.getInvadingFaction().contains("Infestation")) {
            if (invasion.getDefendingFaction().contains("Corpus"))
                holder.bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_corpus_bar));
            else if (invasion.getDefendingFaction().contains("Grineer"))
                holder.bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_grineer_bar));
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            holder.bar.getProgressDrawable().setBounds(bounds);
        }
        holder.bar.setProgress(percentvalue);
        holder.eta.setText(invasion.getEta());
        view.setTag(holder);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        public View completed;
        public TextView node;
        public TextView desc;
        public TextView percent;
        public TextView invadingfaction;
        public TextView defendingfaction;
        public TextView invadingtype;
        public TextView invadingrewards;
        public TextView defendingtype;
        public TextView defendingrewards;
        public TextView eta;
        public ProgressBar bar;
        public Invasion invasion;
    }
}
