package com.example.folcotandiono.visitreporthts;

import android.graphics.Bitmap;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class Customer {
    long idCustomer;
    String name, address, customerArea;
    byte[] photoExterior;
    String location, qrCode;

    public Customer() {

    }

    public long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public byte[] getPhotoExterior() {
        return photoExterior;
    }

    public void setPhotoExterior(byte[] photoExterior) {
        this.photoExterior = photoExterior;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
