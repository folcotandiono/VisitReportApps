package com.example.folcotandiono.visitreporthts;

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class SalesManager {
    long idSalesManager;
    String name, email, phonenumber, password;

    public SalesManager() {

    }

    public long getIdSalesManager() {
        return idSalesManager;
    }

    public void setIdSalesManager(long idSalesManager) {
        this.idSalesManager = idSalesManager;
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
}
