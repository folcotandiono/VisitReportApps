package com.example.folcotandiono.visitreportapps;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class Customer {
    String address, url;
    double lat, lng;

    public Customer() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
