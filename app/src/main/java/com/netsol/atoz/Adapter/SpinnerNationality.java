package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netsol.atoz.Model.Nationality;
import com.netsol.atoz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 12/19/17.
 */

public class SpinnerNationality extends ArrayAdapter<Nationality> {
    private Context mContext;
    private ArrayList<Nationality> nationalityList;
    private SpinnerNationality myAdapter;

    public SpinnerNationality(Context context, int resource, List<Nationality> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.nationalityList = (ArrayList<Nationality>) objects;
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
            convertView = layoutInflator.inflate(R.layout.spinner_item_nationality, null);
            holder = new ViewHolder();
            holder.nationality = (TextView) convertView
                    .findViewById(R.id.spinner_nationality);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.nationality.setText(nationalityList.get(position).getName());
        return convertView;
    }

    private class ViewHolder {
        private TextView nationality;
    }
}
