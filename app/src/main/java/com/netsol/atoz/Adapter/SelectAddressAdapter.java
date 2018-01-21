package com.netsol.atoz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 12/23/17.
 */

public class SelectAddressAdapter extends BaseAdapter {
    private final DatabaseManager dbManager;
    private ArrayList<Address> addressList;
    private Context context;
    private static LayoutInflater inflater = null;

    public SelectAddressAdapter(Context context1, ArrayList<Address> addressArrayList) {
        // TODO Auto-generated constructor stub
        addressList = addressArrayList;
        context = context1;
        dbManager = new DatabaseManager(context);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private class Holder {
        TextView name;
        TextView addressLine;
        TextView landmark;
        TextView mobile;
        TextView phone;
        TextView delivery;
        TextView note;
        TextView location;
        LinearLayout editAddress;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.select_address_view, null);
        final Address address = (Address) getItem(position);
        holder.name = (TextView) rowView.findViewById(R.id.select_address_name);
        holder.addressLine = (TextView) rowView.findViewById(R.id.select_address_line);
        holder.landmark = (TextView) rowView.findViewById(R.id.select_address_landmark);
        holder.mobile = (TextView) rowView.findViewById(R.id.select_address_mobile);
        holder.phone = (TextView) rowView.findViewById(R.id.select_address_phone);
        holder.delivery = (TextView) rowView.findViewById(R.id.select_address_delivery);
        holder.note = (TextView) rowView.findViewById(R.id.select_address_note);
        holder.location = (TextView) rowView.findViewById(R.id.select_address_location);

        if (address.getFullname().equals("null") || address.getFullname().equals("")) {
            holder.name.setText("");
        } else {
            holder.name.setText(address.getFullname());
        }

        String addressLine = "";
        if (!address.getFloor().equals("null") && !address.getFloor().equals("")) {
            addressLine = address.getFloor() + ", ";
        }
        if (!address.getApartment().equals("null") && !address.getApartment().equals("")){
            addressLine = addressLine + address.getApartment() + ", ";
        }
        if (!address.getBuilding().equals("null") && !address.getBuilding().equals("")){
            addressLine = addressLine + address.getBuilding() + ", ";
        }
        if (!address.getStreet().equals("null") && !address.getStreet().equals("")){
            addressLine = addressLine + address.getStreet() + ", ";
        }
        if (!address.getArea().equals("null") && !address.getArea().equals("")){
            addressLine = addressLine + address.getArea() + ", ";
        }
        if (!address.getCity().equals("null") && !address.getCity().equals("")){
            addressLine = addressLine + address.getCity() + ", ";
        }
        if (!address.getCountry().equals("null") && !address.getCountry().equals("")){
            addressLine = addressLine + address.getCountry() + ". ";
        }
        holder.addressLine.setText(addressLine);

        if (address.getLandmark().equals("null") || address.getLandmark().equals("")) {
            holder.landmark.setText("");
        } else {
            holder.landmark.setText(String.format("Landmark: %s", address.getLandmark()));
        }

        if (address.getMobile().equals("null") || address.getMobile().equals("")) {
            holder.mobile.setText("");
        } else {
            holder.mobile.setText(String.format("Mobile: %s", address.getMobile()));
        }

        if (address.getPhone().equals("null") || address.getPhone().equals("")) {
            holder.phone.setText("");
        } else {
            holder.phone.setText(String.format("Phone: %s", address.getPhone()));
        }

        if (address.getDelivery().equals("null") || address.getDelivery().equals("")) {
            holder.delivery.setText("");
        } else {
            holder.delivery.setText(String.format("Preferable Time: %s", address.getDelivery()));
        }

        if (address.getNote().equals("null") || address.getNote().equals("")) {
            holder.note.setText("");
        } else {
            holder.note.setText(String.format("Note: %s", address.getNote()));
        }

        if (address.getLocation().equals("null") || address.getLocation().equals("")) {
            holder.location.setText("");
        } else {
            holder.location.setText(address.getLocation());
        }

        return rowView;
    }
}