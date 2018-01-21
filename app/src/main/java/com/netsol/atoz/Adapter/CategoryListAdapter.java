package com.netsol.atoz.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.HomeProduct;
import com.netsol.atoz.Model.Product;
import com.netsol.atoz.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by macmini on 12/21/17.
 */

public class CategoryListAdapter extends BaseAdapter {

    private final Context mContext;
    private final DatabaseManager dbManager;
    private ArrayList<Product> products;
    private ArrayList<HomeProduct> homeProductsList;

    public CategoryListAdapter(Context context, ArrayList<Product> products, ArrayList<HomeProduct> homeProductsList) {
        this.mContext = context;
        this.products = products;
        this.homeProductsList = homeProductsList;
        dbManager = new DatabaseManager(mContext);
    }

    @Override
    public int getCount() {
        if (products.size() > 0) {
            return products.size();
        } else if (homeProductsList.size() > 0) {
            return homeProductsList.size();
        } else {
            return 0;
        }
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
            convertView = layoutInflater.inflate(R.layout.category_list_view, null);

            ImageView productImage = (ImageView)convertView.findViewById(R.id.category_product_image_list);
            TextView productName = (TextView)convertView.findViewById(R.id.category_product_name_list);
            TextView deprecatedVlaue = (TextView)convertView.findViewById(R.id.category_deprecated_price_list);
            TextView originalVlaue = (TextView)convertView.findViewById(R.id.category_price_list);
            TextView percentOff = (TextView)convertView.findViewById(R.id.category_percent_off_list);

            final ViewHolder viewHolder = new ViewHolder(productImage, productName, deprecatedVlaue, originalVlaue, percentOff);
            convertView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();

        if (products.size() > 0) {
            Product products1  = products.get(position);
            Glide.with(mContext).load(products1.getProductImage())
                    .fitCenter()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(null)
                    .into(viewHolder.productImage);

            viewHolder.productName.setText(products1.getTilte());
            viewHolder.deprecatedVlaue.setText(String.format("%s AED", products1.getActualPrice()));
            viewHolder.deprecatedVlaue.setPaintFlags(viewHolder.deprecatedVlaue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.originalVlaue.setText(String.format("%s AED", products1.getSellingPrice()));
            viewHolder.percentOff.setText("-" + products1.getDiscountPercent() + "%");
        } else {
            HomeProduct products1  = homeProductsList.get(position);
            Glide.with(mContext).load(products1.getImage())
                    .fitCenter()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(null)
                    .into(viewHolder.productImage);

            viewHolder.productName.setText(products1.getProductTitle());
            viewHolder.deprecatedVlaue.setText(String.format("%s AED", products1.getActual_price()));
            viewHolder.deprecatedVlaue.setPaintFlags(viewHolder.deprecatedVlaue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.originalVlaue.setText(String.format("%s AED", products1.getSelling_price()));
            viewHolder.percentOff.setText("-" + products1.getDiscount_percent() + "%");

        }
        return convertView;
    }

    public void updateCategoryList() {
        if (products.size() > 0) {
            Collections.reverse(products);
        } else {
            Collections.reverse(homeProductsList);
        }
        this.notifyDataSetChanged();
    }

    private class ViewHolder {
        private ImageView productImage;
        private TextView productName;
        private TextView deprecatedVlaue;
        private TextView originalVlaue;
        private TextView percentOff;

        ViewHolder(ImageView productImage, TextView productName, TextView deprecatedVlaue,
                   TextView originalVlaue, TextView percentOff) {
            this.productImage = productImage;
            this.productName = productName;
            this.deprecatedVlaue = deprecatedVlaue;
            this.originalVlaue = originalVlaue;
            this.percentOff = percentOff;
        }
    }
}
