package com.netsol.atoz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.netsol.atoz.Model.Faq;
import com.netsol.atoz.R;

import java.util.ArrayList;

/**
 * Created by macmini on 1/8/18.
 */

public class FaqChildAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private ArrayList<Faq> groups;

    public FaqChildAdapter(Activity context, ArrayList<Faq> groups) {
        this.context = context;
        this.groups = groups;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.faq_child_explanation, null);
        }
        Faq groupObj = groups.get(groupPosition);
//        AboutChild child = groupObj.getAboutChildArrayList().get(
//                childPosition);
        WebView descriptionText = (WebView) convertView.findViewById(R.id.faq_explanation);
        String description = groupObj.getDescription();
        if ((description == null) || (description.equals("")) || (description.equals("null"))) {
            descriptionText.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        } else {
            descriptionText.loadDataWithBaseURL(null, description, "text/html", "utf-8", null);
//            descriptionText.setText(Html.fromHtml(description));
        }

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.faq_child_heading,
                    null);
        }

        Faq groupObj = groups.get(groupPosition);
        TextView questionText = (TextView) convertView.findViewById(R.id.faq_child_heading);

        String questions = groupObj.getTitle();
        if ((questions == null) || (questions.equals("")) || (questions.equals("null"))) {
            questionText.setText("");
        } else {
            questionText.setText(questions);
        }

        if (isExpanded) {
            questionText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            questionText.setTextColor(context.getResources().getColor(R.color.blackText));
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
