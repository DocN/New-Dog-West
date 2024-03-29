package com.drnserver.newdogwest.Models;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.*;

import java.util.ArrayList;

/**
 * Created by DrN on 3/5/2018.
 */

public class PlaceProperties {
    private String strName;
    private String strNum;
    private String parkName;
    private String category;
    private String neighbourhood;
    private String zoning;
    private String zone_category;
    private String surveyed;
    private String siteArea;
    private String lat;
    private String lon;
    private double distance;
    private String imgUrl;
    private String address;
    //type of place
    private String type;
    private int index;
    private Business business;

    /*constructor */
    public PlaceProperties() {
        this.distance = 50;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrNum() {
        return strNum;
    }

    public void setStrNum(String strNum) {
        this.strNum = strNum;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getZoning() {
        return zoning;
    }

    public void setZoning(String zoning) {
        this.zoning = zoning;
    }

    public String getZone_category() {
        return zone_category;
    }

    public void setZone_category(String zone_category) {
        this.zone_category = zone_category;
    }

    public String getSurveyed() {
        return surveyed;
    }

    public void setSurveyed(String surveyed) {
        this.surveyed = surveyed;
    }

    public String getSiteArea() {
        return siteArea;
    }

    public void setSiteArea(String siteArea) {
        this.siteArea = siteArea;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business businesses) {
        this.business = business;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
