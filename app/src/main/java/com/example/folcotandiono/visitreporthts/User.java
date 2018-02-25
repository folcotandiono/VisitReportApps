package com.example.folcotandiono.visitreporthts;

import java.util.ArrayList;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class User {
    String name, email, phonenumber, password;
    int role;
    double lat, lng;
    ArrayList<String> circle;
    Boolean checkInStatus;
    String checkInCircleName, checkInDate;

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
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

    public ArrayList<String> getCircle() {
        return circle;
    }

    public void setCircle(ArrayList<String> circle) {
        this.circle = circle;
    }

    public Boolean getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(Boolean checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public String getCheckInCircleName() {
        return checkInCircleName;
    }

    public void setCheckInCircleName(String checkInCircleName) {
        this.checkInCircleName = checkInCircleName;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }
}
