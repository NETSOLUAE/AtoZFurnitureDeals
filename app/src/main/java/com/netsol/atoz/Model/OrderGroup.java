package com.netsol.atoz.Model;

import java.util.ArrayList;

/**
 * Created by macmini on 11/15/17.
 */

public class OrderGroup {

    private String no;
    private String total;
    private String date;
    private String address;
    private String addressName;
    private String addressID;
    private String status;
    private String paymentStatus;
    private String levelCompleted;

    public OrderGroup() {

    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getLevelCompleted() {
        return levelCompleted;
    }

    public void setLevelCompleted(String levelCompleted) {
        this.levelCompleted = levelCompleted;
    }
}
