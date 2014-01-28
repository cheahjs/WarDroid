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
import com.deathsnacks.wardroid.gson.PendingRecipe;
import com.deathsnacks.wardroid.gson.PendingRecipes;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 25/01/14.
 */
public class FoundryListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private PendingRecipes pendingRecipes;
    private LayoutInflater mInflater;

    public FoundryListViewAdapter(Activity act, PendingRecipes data) {
        mActivity = act;
        pendingRecipes = data;
        mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return pendingRecipes.getPendingRecipes().size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_foundry, null);
        TextView item = (TextView)view.findViewById(R.id.foundry_item);
        TextView duration = (TextView)view.findViewById(R.id.foundry_duration);

        PendingRecipe recipe = pendingRecipes.getPendingRecipes().get(position);
        String itemname = Names.getName(mActivity, recipe.getItemType()).replace(" Blueprint", "");
        item.setText(itemname);
        long now = System.currentTimeMillis()/1000;
        long diff = recipe.getCompletionDate().getSec() - now;
        if (diff < 0) {
            duration.setText("COMPLETED");
            duration.setTextColor(Color.parseColor("#5cb85c"));
        }
        else {
            long days = (long)Math.floor(diff / 86400);
            if (days > 0)
                duration.setText(String.format("%dd %dh %dm %ds", (long)Math.floor(diff / 86400), (long)Math.floor(diff / 3600) % 24, (diff/60 % 60), diff % 60));
            else
                duration.setText(String.format("%dh %dm %ds", (long) Math.floor(diff / 3600) % 24, (diff / 60 % 60), diff % 60));
            duration.setTextColor(Color.parseColor("#10bcc9"));
        }

        view.setTag(recipe);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
