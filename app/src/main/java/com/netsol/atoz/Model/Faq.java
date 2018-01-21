package com.netsol.atoz.Model;

/**
 * Created by macmini on 1/8/18.
 */

public class Faq {
    private String groupID;
    private String groupName;
    private String groupTotal;
    private String childId;
    private String title;
    private String description;
    private String order;

    public Faq () {

    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupTotal() {
        return groupTotal;
    }

    public void setGroupTotal(String groupTotal) {
        this.groupTotal = groupTotal;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
