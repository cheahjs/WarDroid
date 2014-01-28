package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class InvasionListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mLines;
    private LayoutInflater mInflater;

    public InvasionListViewAdapter(Activity act, List<String> data) {
        mActivity = act;
        mLines = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mLines.size() - 1;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_invasion, null);
        TextView node = (TextView) view.findViewById(R.id.invasion_node);
        TextView desc = (TextView) view.findViewById(R.id.invasion_desc);
        TextView percent = (TextView) view.findViewById(R.id.invasion_percent);
        TextView invadingfaction = (TextView) view.findViewById(R.id.invasion_invading_faction);
        TextView invadingtype = (TextView) view.findViewById(R.id.invasion_invading_type);
        TextView invadingrewards = (TextView) view.findViewById(R.id.invasion_invading_reward);
        TextView defendingfaction = (TextView) view.findViewById(R.id.invasion_defending_faction);
        TextView defendingtype = (TextView) view.findViewById(R.id.invasion_defending_type);
        TextView defendingrewards = (TextView) view.findViewById(R.id.invasion_defending_reward);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.invasion_bar);

        String line = mLines.get(position + 1);
        String[] parts = line.split("\\|");
        node.setText(String.format("%s (%s)", parts[1], parts[2]));
        invadingfaction.setText(parts[3]);
        invadingtype.setText(parts[3].contains("Infestation") ? "" : parts[4]);
        invadingrewards.setText(parts[3].contains("Infestation") ? "" : parts[5]);
        defendingfaction.setText(parts[8]);
        defendingtype.setText(parts[9]);
        defendingrewards.setText(parts[10]);
        percent.setText(parts[16] + "%");
        desc.setText(parts[18]);
        int percentvalue = (int) Double.parseDouble(parts[16]);
        if (percentvalue > 100)
            percentvalue = 100;
        if (percentvalue < 0)
            percentvalue = 0;
        bar.setProgress(0);
        Rect bounds = bar.getProgressDrawable().getBounds();
        bar.getProgressDrawable().setBounds(bounds);
        if (parts[3].contains("Grineer"))
            bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.grineer_corpus_bar));
        else if (parts[3].contains("Corpus"))
            bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.corpus_grineer_bar));
        else if (parts[3].contains("Infestation")) {
            if (parts[8].contains("Corpus"))
                bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_corpus_bar));
            else if (parts[8].contains("Grineer"))
                bar.setProgressDrawable(mActivity.getResources().getDrawable(R.drawable.infestation_grineer_bar));
        }
        bar.getProgressDrawable().setBounds(bounds);
        bar.setProgress(percentvalue);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
