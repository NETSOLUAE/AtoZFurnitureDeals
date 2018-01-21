package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netsol.atoz.Model.Area;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 12/17/17.
 */

public class SpinnerAreaAdapter extends ArrayAdapter<Area> {
    private Context mContext;
    private ArrayList<Area> areaList;
    private SpinnerAreaAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerAreaAdapter(Context context, int resource, ArrayList<Area> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.areaList = (ArrayList<Area>) objects;
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
            convertView = layoutInflator.inflate(R.layout.spinner_item_area, null);
            holder = new ViewHolder();
            holder.area = (TextView) convertView
                    .findViewById(R.id.spinner_area);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.area.setText(areaList.get(position).getName());
        return convertView;
    }

    private class ViewHolder {
        private TextView area;
    }
}
