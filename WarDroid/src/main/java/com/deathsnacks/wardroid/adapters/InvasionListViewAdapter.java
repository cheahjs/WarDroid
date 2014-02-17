package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
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

    public InvasionListViewAdapter(Activity act, List<String> data) {
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
    }

    @Override
    public void notifyDataSetChanged() {
        if (mPreferences.getBoolean("hide_completed", false))
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
        super.notifyDataSetChanged();
    }

    public int getCount() {
        return mLines.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_invasion, null);
        view.setVisibility(View.VISIBLE);
        TextView node = (TextView) view.findViewById(R.id.invasion_node);
        TextView desc = (TextView) view.findViewById(R.id.invasion_desc);
        TextView percent = (TextView) view.findViewById(R.id.invasion_percent);
        TextView invadingfaction = (TextView) view.findViewById(R.id.invasion_invading_faction);
        TextView invadingtype = (TextView) view.findViewById(R.id.invasion_invading_type);
        TextView invadingrewards = (TextView) view.findViewById(R.id.invasion_invading_reward);
        TextView defendingfaction = (TextView) view.findViewById(R.id.invasion_defending_faction);
        TextView defendingtype = (TextView) view.findViewById(R.id.invasion_defending_type);
        TextView defendingrewards = (TextView) view.findViewById(R.id.invasion_defending_reward);
        TextView eta = (TextView) view.findViewById(R.id.invasion_eta);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.invasion_bar);

        String line = mLines.get(position);
        String[] parts = line.split("\\|");
        if (parts.length != 19) {
            View dedView = new View(mActivity);
            dedView.setVisibility(View.GONE);
            return dedView;
        }
        Invasion invasion = new Invasion(line);
        if (mPreferences.getBoolean("hide_completed", false) && mCompletedIds.contains(invasion.getId())) {
            notifyDataSetChanged();
        }
        node.setText(String.format("%s (%s)", invasion.getNode(), invasion.getRegion()));
        invadingfaction.setText(invasion.getInvadingFaction());
        invadingtype.setText(invasion.getInvadingFaction().contains("Infestation") ? "" : invasion.getInvadingType());
        invadingrewards.setText(invasion.getInvadingFaction().contains("Infestation") ? "" : invasion.getInvadingReward());
        defendingfaction.setText(invasion.getDefendingFaction());
        defendingtype.setText(invasion.getDefendingType());
        defendingrewards.setText(invasion.getDefendingReward());
        percent.setText(invasion.getPercent() + "%");
        desc.setText(invasion.getDescription());
        int percentvalue = (int) Double.parseDouble(invasion.getPercent());
        if (percentvalue > 100)
            percentvalue = 100;
        if (percentvalue < 0)
            percentvalue = 0;
        bar.setProgress(0);
        Rect bounds = bar.getProgressDrawable().getBounds();
        bar.getProgressDrawable().setBounds(bounds);
        if (invasion.getInvadingFaction().contains("Grineer"))
            bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.grineer_corpus_bar));
        else if (invasion.getInvadingFaction().contains("Corpus"))
            bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.corpus_grineer_bar));
        else if (invasion.getInvadingFaction().contains("Infestation")) {
            if (invasion.getDefendingFaction().contains("Corpus"))
                bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_corpus_bar));
            else if (invasion.getDefendingFaction().contains("Grineer"))
                bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_grineer_bar));
        }
        bar.getProgressDrawable().setBounds(bounds);
        bar.setProgress(percentvalue);
        eta.setText(invasion.getEta());
        view.setTag(invasion);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
