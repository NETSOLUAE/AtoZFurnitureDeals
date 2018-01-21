package com.netsol.atoz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 12/23/17.
 */

public class CategorySearchAdapter extends BaseAdapter {

    private final Context mContext;
    private ArrayList<String> products;
    DatabaseManager databaseManager;

    public CategorySearchAdapter(Context context, ArrayList<String> products) {
        this.mContext = context;
        this.products = products;
        databaseManager = new DatabaseManager(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.item_category_search, null);

            TextView searchText = (TextView)convertView.findViewById(R.id.categroy_search_item);

            final ViewHolder viewHolder = new ViewHolder(searchText);
            convertView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();

//        Product products1  = products.get(position);
//        String categoryID = products1.getCategoryId();
//        String categoryName = databaseManager.getCategoryName(categoryID);
        viewHolder.searchText.setText(products.get(position));

        return convertView;
    }

    private class ViewHolder {
        private TextView searchText;

        ViewHolder(TextView searchText) {
            this.searchText = searchText;
        }
    }
}
