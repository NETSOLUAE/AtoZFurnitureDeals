package com.netsol.atoz.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.netsol.atoz.Controller.DatabaseManager;
import com.netsol.atoz.Model.Faq;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 1/8/18.
 */

public class FaqGroupAdapter extends BaseAdapter {

    private final Context mContext;
    private final DatabaseManager dbManager;
    private ArrayList<Faq> faqArrayList;

    public FaqGroupAdapter(Context context, ArrayList<Faq> faqArrayList) {
        this.mContext = context;
        this.faqArrayList = faqArrayList;
        dbManager = new DatabaseManager(mContext);
    }

    @Override
    public int getCount() {
        return faqArrayList.size();
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
            convertView = layoutInflater.inflate(R.layout.faq_group, null);

            TextView heading = (TextView)convertView.findViewById(R.id.faq_group_name);

            final ViewHolder viewHolder = new ViewHolder(heading);
            convertView.setTag(viewHolder);
        }
        final ViewHolder viewHolder = (ViewHolder)convertView.getTag();
        Faq faq  = faqArrayList.get(position);

        String faqHeading = dbManager.getFaqName(faq.getGroupID());
        viewHolder.heading.setText(faqHeading);
        return convertView;
    }

    private class ViewHolder {
        private TextView heading;

        ViewHolder(TextView heading) {
            this.heading = heading;
        }
    }
}
