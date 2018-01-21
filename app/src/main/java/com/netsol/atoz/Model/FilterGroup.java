package com.netsol.atoz.Model;

import java.util.ArrayList;

/**
 * Created by macmini on 12/21/17.
 */

public class FilterGroup {
    private String id;
    private String title;
    private ArrayList<FilterChild> filterChildArray;

    public FilterGroup() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<FilterChild> getFilterChildArray() {
        return filterChildArray;
    }

    public void setFilterChildArray(ArrayList<FilterChild> filterChildArray) {
        this.filterChildArray = filterChildArray;
    }
}
