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
 * Created by macmini on 12/14/17.
 */

public class SpinnerDeliveryAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private ArrayList<String> deliveryList;
    private SpinnerDeliveryAdapter myAdapter;
    private boolean isFromView = false;

    public SpinnerDeliveryAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.deliveryList = (ArrayList<String>) objects;
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
            convertView = layoutInflator.inflate(R.layout.spinner_item_delivery, null);
            holder = new ViewHolder();
            holder.delivery = (TextView) convertView
                    .findViewById(R.id.spinner_item_delivery);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.delivery.setText(deliveryList.get(position));
        return convertView;
    }

    private class ViewHolder {
        private TextView delivery;
    }
}
