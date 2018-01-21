package com.netsol.atoz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.Address;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 12/14/17.
 */

public class AddressAdapter extends BaseAdapter {
    private final DatabaseManager dbManager;
    private ArrayList<Address> addressList;
    private OnAddressEdit addressEdit;
    private OnEnable enable;
    private Context context;
    private static LayoutInflater inflater = null;

    public AddressAdapter(Context context1, ArrayList<Address> addressArrayList) {
        // TODO Auto-generated constructor stub
        addressList = addressArrayList;
        context = context1;
        dbManager = new DatabaseManager(context);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnAddressEditListener(OnAddressEdit addressEdit) {
        this.addressEdit = addressEdit;
    }

    public void setOnEnableListener(OnEnable enable1) {
        this.enable = enable1;
    }

    public interface OnAddressEdit {
        public void OnAddressEditClicked(String addressId);
    }

    public interface OnEnable {
        public void OnEnableClicked(boolean isEnabled, String addressId);
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

    public void updateAddressList(ArrayList<Address> addressArrayList) {
        addressList.clear();
        addressList.addAll(addressArrayList);
        this.notifyDataSetChanged();
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
        RadioGroup addressRadioGroup;
        RadioButton addressEnable;
        RadioButton addressDisable;
        LinearLayout editAddress;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.my_address_view, null);
        final Address address = (Address) getItem(position);
        holder.name = (TextView) rowView.findViewById(R.id.address_name);
        holder.addressLine = (TextView) rowView.findViewById(R.id.address_line);
        holder.landmark = (TextView) rowView.findViewById(R.id.address_landmark);
        holder.mobile = (TextView) rowView.findViewById(R.id.address_mobile);
        holder.phone = (TextView) rowView.findViewById(R.id.address_phone);
        holder.delivery = (TextView) rowView.findViewById(R.id.address_delivery);
        holder.note = (TextView) rowView.findViewById(R.id.address_note);
        holder.location = (TextView) rowView.findViewById(R.id.address_location);
        holder.addressRadioGroup = (RadioGroup) rowView.findViewById(R.id.address_radio_group);
        holder.addressEnable = (RadioButton) rowView.findViewById(R.id.radio_button_enable);
        holder.addressDisable = (RadioButton) rowView.findViewById(R.id.radio_button_disable);
        holder.editAddress = (LinearLayout) rowView.findViewById(R.id.button_edit_location);

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
            String mobileNoText = address.getMobile();
            if (mobileNoText.contains("+971")) {
                mobileNoText = mobileNoText.replace("+971","+971 ");
            } else if (mobileNoText.contains("971")) {
                mobileNoText = mobileNoText.replace("971","971 ");
            }
            holder.mobile.setText(String.format("Mobile: %s", mobileNoText));
        }

        if (address.getPhone().equals("null") || address.getPhone().equals("") || address.getPhone().equals("0")) {
            holder.phone.setText(String.format("Phone: %s", ""));
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

        if (address.getEnable().equals("null") || address.getEnable().equals("")) {
            holder.addressEnable.setChecked(true);
        } else {
            String enable = address.getEnable();
            if (enable.equalsIgnoreCase("Yes")) {
                holder.addressEnable.setChecked(true);
            } else {
                holder.addressDisable.setChecked(true);
            }
        }

        holder.editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressEdit.OnAddressEditClicked(address.getId());
            }
        });

        holder.addressRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.radio_button_enable) {
                    enable.OnEnableClicked(true, address.getId());
                } else if(checkedId == R.id.radio_button_disable) {
                    enable.OnEnableClicked(false, address.getId());
                }
            }
        });

        return rowView;
    }
}
