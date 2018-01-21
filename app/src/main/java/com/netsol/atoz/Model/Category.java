package com.netsol.atoz.Model;

import java.util.ArrayList;

/**
 * Created by macmini on 11/6/17.
 */

public class Category {

    private String categoryID;
    private String parent;
    private String categoryName;
    private String categoryImage;
    private String total;
    private String path;
    private String enabled;
    private String position;
    private String isHome;

    public Category() {
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getIsHome() {
        return isHome;
    }

    public void setIsHome(String isHome) {
        this.isHome = isHome;
    }
}
