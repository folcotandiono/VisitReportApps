package com.example.folcotandiono.visitreporthts;

import java.sql.Date;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class HistoryLocation {
    Date date;
    long idCustomer, idSales, idVisitPlan, idCircle;

    public HistoryLocation() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public long getIdSales() {
        return idSales;
    }

    public void setIdSales(long idSales) {
        this.idSales = idSales;
    }

    public long getIdVisitPlan() {
        return idVisitPlan;
    }

    public void setIdVisitPlan(long idVisitPlan) {
        this.idVisitPlan = idVisitPlan;
    }

    public long getIdCircle() {
        return idCircle;
    }

    public void setIdCircle(long idCircle) {
        this.idCircle = idCircle;
    }
}
