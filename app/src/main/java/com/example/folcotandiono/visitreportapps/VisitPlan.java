package com.example.folcotandiono.visitreportapps;

import java.sql.Date;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class VisitPlan {
    long idVisitPlan, statusVerified;
    Date date;
    String listOfCustomer;
    long idSales, idSalesManager;
    Date timeCheckIn, timeCheckOut;
    String discussion;

    public VisitPlan() {

    }

    public long getIdVisitPlan() {
        return idVisitPlan;
    }

    public void setIdVisitPlan(long idVisitPlan) {
        this.idVisitPlan = idVisitPlan;
    }

    public long getStatusVerified() {
        return statusVerified;
    }

    public void setStatusVerified(long statusVerified) {
        this.statusVerified = statusVerified;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getListOfCustomer() {
        return listOfCustomer;
    }

    public void setListOfCustomer(String listOfCustomer) {
        this.listOfCustomer = listOfCustomer;
    }

    public long getIdSales() {
        return idSales;
    }

    public void setIdSales(long idSales) {
        this.idSales = idSales;
    }

    public long getIdSalesManager() {
        return idSalesManager;
    }

    public void setIdSalesManager(long idSalesManager) {
        this.idSalesManager = idSalesManager;
    }

    public Date getTimeCheckIn() {
        return timeCheckIn;
    }

    public void setTimeCheckIn(Date timeCheckIn) {
        this.timeCheckIn = timeCheckIn;
    }

    public Date getTimeCheckOut() {
        return timeCheckOut;
    }

    public void setTimeCheckOut(Date timeCheckOut) {
        this.timeCheckOut = timeCheckOut;
    }

    public String getDiscussion() {
        return discussion;
    }

    public void setDiscussion(String discussion) {
        this.discussion = discussion;
    }
}
