package com.netsol.atoz.Model;

/**
 * Created by macmini on 12/17/17.
 */

public class Area {
    private String id;
    private String name;
    private String shipRate;
    private String cityId;

    public Area() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShipRate() {
        return shipRate;
    }

    public void setShipRate(String shipRate) {
        this.shipRate = shipRate;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
