package com.github.donmahallem.heartfit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.donmahallem.heartfit.R;
import com.github.donmahallem.heartfit.fit.FitResourceUtil;

public class ResitanceTypeAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView= (TextView) convertView;
        textView.setText(FitResourceUtil.get(position));
        return convertView;
    }
}
