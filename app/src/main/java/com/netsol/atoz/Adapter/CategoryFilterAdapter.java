package com.netsol.atoz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.netsol.atoz.Model.FilterChild;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 12/14/17.
 */

public class CategoryFilterAdapter extends BaseAdapter {
    private ArrayList<FilterChild> filterChildArrayList;
    private OnCheckBoxChanged onCheckBoxChanged;
    private Context context;
    private static LayoutInflater inflater = null;

    public CategoryFilterAdapter(Context context1, ArrayList<FilterChild> filterChildArrayList1) {
        // TODO Auto-generated constructor stub
        filterChildArrayList = filterChildArrayList1;
        context = context1;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnAddressEditListener(OnCheckBoxChanged onCheckBoxChanged) {
        this.onCheckBoxChanged = onCheckBoxChanged;
    }

    public interface OnCheckBoxChanged {
        public void OnCheckBoxChangedListener(ArrayList<FilterChild> listStateSelected);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return filterChildArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return filterChildArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

//    public void updateAddressList(ArrayList<FilterChild> addressArrayList) {
//        addressList.clear();
//        addressList.addAll(addressArrayList);
//        this.notifyDataSetChanged();
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.category_filter_child, null);
        final FilterChild filterChild = filterChildArrayList.get(position);
        TextView descriptionText = (TextView) rowView.findViewById(R.id.category_filter_child_name);
        CheckBox categoryFilterCheckbox = (CheckBox) rowView.findViewById(R.id.category_filter_checkbox);

        String child = filterChild.getTitle();
        if ((child == null) || (child.equals("")) || (child.equals("null"))) {
            descriptionText.setText("");
        } else {
            descriptionText.setText(child);
        }

        String childSelected = filterChild.getIsSelected();
        if ((childSelected == null) || (childSelected.equals("")) || (childSelected.equals("null"))) {
            categoryFilterCheckbox.setChecked(false);
        } else {
            if (childSelected.equalsIgnoreCase("true")) {
                categoryFilterCheckbox.setChecked(true);
            } else if (childSelected.equalsIgnoreCase("false")) {
                categoryFilterCheckbox.setChecked(false);
            }  else {
                categoryFilterCheckbox.setChecked(false);
            }
            descriptionText.setText(child);
        }

        categoryFilterCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    filterChild.setIsSelected("true");
                } else {
                    filterChild.setIsSelected("false");
                }
                onCheckBoxChanged.OnCheckBoxChangedListener(filterChildArrayList);
            }
        });

        return rowView;
    }
}

