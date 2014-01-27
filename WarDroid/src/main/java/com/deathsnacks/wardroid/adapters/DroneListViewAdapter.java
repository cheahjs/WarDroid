package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.gson.ActiveDrone;
import com.deathsnacks.wardroid.gson.ActiveDrones;
import com.deathsnacks.wardroid.gson.PendingRecipe;
import com.deathsnacks.wardroid.gson.PendingRecipes;
import com.deathsnacks.wardroid.utils.Names;

/**
 * Created by Admin on 25/01/14.
 */
public class DroneListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private ActiveDrones activeDrones;
    private LayoutInflater mInflater;

    public DroneListViewAdapter(Activity act, ActiveDrones data) {
        mActivity = act;
        activeDrones = data;
        mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return activeDrones.getActiveDrones().size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_drone, null);
        TextView duration = (TextView)view.findViewById(R.id.drone_duration);
        TextView system = (TextView)view.findViewById(R.id.drone_system);
        TextView name = (TextView)view.findViewById(R.id.drone_name);

        ActiveDrone drone = activeDrones.getActiveDrones().get(position);

        String dronename = Names.getName(mActivity, drone.getItemType());
        String itemname = Names.getName(mActivity, drone.getResources().get(0).getItemType());
        system.setText(Names.getRegion(drone.getSystem()));
        name.setText(dronename);
        int count = drone.getResources().get(0).getBinTotal();
        long now = System.currentTimeMillis()/1000;
        long diff = now - drone.getResources().get(0).getStartTime().getSec();
        if (diff > 14400) {
            duration.setText(String.format("%d %s", count, itemname));
        }
        else {
            long durationleft = 14400 - diff;
            duration.setText(String.format("%dh%dm%ds", (long)Math.floor(durationleft / 3600) % 24, (durationleft/60 % 60), durationleft % 60));
        }
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
