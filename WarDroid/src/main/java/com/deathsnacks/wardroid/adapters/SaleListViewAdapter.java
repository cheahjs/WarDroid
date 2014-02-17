package com.deathsnacks.wardroid.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.deathsnacks.wardroid.R;

import java.util.List;

/**
 * Created by Admin on 25/01/14.
 */
public class SaleListViewAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<String> mSales;
    private LayoutInflater mInflater;

    public SaleListViewAdapter(Activity act, List<String> data) {
        mActivity = act;
        mSales = data;
        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mSales.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = mInflater.inflate(R.layout.list_item_alert, null);

        String sale = mSales.get(position);
        return view;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
}
