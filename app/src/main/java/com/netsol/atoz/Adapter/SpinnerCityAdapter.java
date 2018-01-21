package com.netsol.atoz.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netsol.atoz.Model.City;
import com.netsol.atoz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macmini on 12/14/17.
 */

public class SpinnerCityAdapter extends ArrayAdapter<City> {
    private Context mContext;
    private ArrayList<City> cityList;
    private SpinnerCityAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerCityAdapter(Context context, int resource, ArrayList<City> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.cityList = (ArrayList<City>) objects;
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
            convertView = layoutInflator.inflate(R.layout.spinner_item_city, null);
            holder = new ViewHolder();
            holder.city = (TextView) convertView
                    .findViewById(R.id.spinner_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.city.setText(cityList.get(position).getName());
        return convertView;
    }

    private class ViewHolder {
        private TextView city;
    }
}
