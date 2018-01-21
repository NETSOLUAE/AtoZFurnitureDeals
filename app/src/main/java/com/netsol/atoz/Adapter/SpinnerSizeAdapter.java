package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netsol.atoz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 11/7/17.
 */

public class SpinnerSizeAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> optionList;
    private SpinnerSizeAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerSizeAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.optionList = (ArrayList<String>) objects;
        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(final int position, View convertView,
                               ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item_size, null);
            holder = new ViewHolder();
            holder.option = (TextView) convertView
                    .findViewById(R.id.option);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.option.setText(optionList.get(position));
        return convertView;
    }

    private class ViewHolder {
        private TextView option;
    }
}
