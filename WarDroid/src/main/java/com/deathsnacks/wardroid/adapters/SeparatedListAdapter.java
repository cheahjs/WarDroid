package com.deathsnacks.wardroid.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deathsnacks.wardroid.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class SeparatedListAdapter extends BaseAdapter {

    public final Map<String, BaseAdapter> sections = new LinkedHashMap<String, BaseAdapter>();
    public final ArrayAdapter<String> headers;
    public final View emptyView;
    public final static int TYPE_SECTION_HEADER = 0;
    public final LayoutInflater mInflator;
    public final Context mContext;

    public SeparatedListAdapter(Context context, View noneView) {
        mContext = context;
        headers = new ArrayAdapter<String>(context, R.layout.list_header_others);
        emptyView = noneView;
        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addSection(String section, BaseAdapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    public BaseAdapter getSectionAdapter(String section){
        for (Object section2 : this.sections.keySet()) {
            if (section2.equals(section)) {
                return sections.get(section2);
            }
        }
        return null;
    }

    public Object getItem(int position) {
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) return section;
            if (position < size) return adapter.getItem(position - 1);

            // otherwise jump into next section
            position -= size;
        }
        return null;
    }

    @Override
    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for (Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;
        return total;
    }

    public int getAdapterCount() {
        // total together all sections
        int total = 0;
        for (Adapter adapter : this.sections.values())
            total += adapter.getCount();
        return total;
    }

    @Override
    public void notifyDataSetChanged() {
        for (BaseAdapter adapter : this.sections.values())
            adapter.notifyDataSetChanged();
        super.notifyDataSetChanged();
        if (getAdapterCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for (Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();
        return total;
    }

    public int getItemViewType(int position) {
        int type = 1;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) return TYPE_SECTION_HEADER;
            if (position < size) return type + adapter.getItemViewType(position - 1);

            // otherwise jump into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionnum = 0;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {return getHeaderView(sectionnum, convertView, parent);}
            if (position < size) {
                return adapter.getView(position - 1, convertView, parent);
            }

            // otherwise jump into next section
            position -= size;
            sectionnum++;
        }
        return null;
    }

    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        view = mInflator.inflate(R.layout.list_header_others, null);
        if (position == 0) {
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, mContext.getResources().getDisplayMetrics());
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, px));
        } else {
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 36, mContext.getResources().getDisplayMetrics());
            view.setMinimumHeight(px);
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, px));
        }
        view.invalidate();
        TextView text = (TextView) view.findViewById(R.id.list_header_title);
        text.setText(headers.getItem(position));
        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

