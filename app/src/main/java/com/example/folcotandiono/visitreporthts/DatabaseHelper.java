package com.example.folcotandiono.visitreporthts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Folco Tandiono on 09/02/2018.
 * http://www.androidtutorialshub.com/android-login-and-register-with-sqlite-database-tutorial/
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "VisitReport", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Customer (idCustomer integer primary key autoincrement, name text, address text, customerArea text," +
                "photoExterior blob, location text, qrCode text);");
        sqLiteDatabase.execSQL("CREATE TABLE Sales (idSales integer primary key autoincrement, name text, email text, phonenumber text, password text);");
        sqLiteDatabase.execSQL("CREATE TABLE HistoryLocation (date datetime, idCustomer integer, " +
                "idSales integer, idVisitPlan integer, idCircle integer);");
        sqLiteDatabase.execSQL("CREATE TABLE Circle (idCircle integer primary key autoincrement, name text, idSalesManager integer);");
        sqLiteDatabase.execSQL("CREATE TABLE VisitPlan (idVisitPlan integer primary key autoincrement, statusVerified integer, " +
                "date datetime, listOfCustomer text, idSales integer, idSalesManager integer, timeCheckIn datetime, timeCheckOut datetime, " +
                "discussion text);");
        sqLiteDatabase.execSQL("CREATE TABLE SalesManager (idSalesManager integer primary key autoincrement not null, name text, email text, phonenumber text, password text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Customer");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Sales");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS HistoryLocation");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Circle");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS VisitPlan");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS SalesManager");

        onCreate(sqLiteDatabase);
    }

    // untuk memasukkan record customer
    public void insertCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", customer.getName());
        values.put("address", customer.getAddress());
        values.put("customerArea", customer.getCustomerArea());
        values.put("photoExterior", customer.getPhotoExterior());
        values.put("location", customer.getLocation());
        values.put("qrCode", customer.getQrCode());

        db.insert("Customer", null, values);
        db.close();
    }

    //untuk mendapatkan record customer berdasarkan id
    public Customer getCustomer(long idCustomer) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Customer where idCustomer = ?";

        Cursor c = db.rawQuery(query, new String[] {String.valueOf(idCustomer)});

        Customer customer = new Customer();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            customer.setIdCustomer(c.getLong(c.getColumnIndex("idCustomer")));
            customer.setName(c.getString(c.getColumnIndex("name")));
            customer.setAddress(c.getString(c.getColumnIndex("address")));
            customer.setCustomerArea(c.getString(c.getColumnIndex("customerArea")));
            customer.setPhotoExterior(c.getBlob(c.getColumnIndex("photoExterior")));
            customer.setLocation(c.getString(c.getColumnIndex("location")));
            customer.setQrCode(c.getString(c.getColumnIndex("qrCode")));
        }

        c.close();
        db.close();

        return customer;
    }

    public void deleteSales() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Sales");
    }

    // untuk memasukkan record sales
    public void insertSales(Sales sales) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", sales.getName());
        values.put("email", sales.getEmail());
        values.put("phonenumber", sales.getPhonenumber());
        values.put("password", sales.getPassword());

        db.insert("Sales", null, values);
        db.close();
    }

    // untuk mendapatkan record sales berdasarkan email
    public Sales getSalesByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        Sales sales;
        String query;

        query = "SELECT * FROM Sales where email = ?";

        c = db.rawQuery(query, new String[] {email});

        sales = new Sales();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            sales.setIdSales(c.getInt(c.getColumnIndex("idSales")));
            sales.setName(c.getString(c.getColumnIndex("name")));
            sales.setEmail(c.getString(c.getColumnIndex("email")));
            sales.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            sales.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return sales;
    }

    // untuk mendapatkan record sales berdasarkan phonenumber
    public Sales getSalesByPhonenumber(String phonenumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        Sales sales;
        String query;

        query = "SELECT * FROM Sales where phonenumber = ?";

        c = db.rawQuery(query, new String[] {phonenumber});

        sales = new Sales();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            sales.setIdSales(c.getInt(c.getColumnIndex("idSales")));
            sales.setName(c.getString(c.getColumnIndex("name")));
            sales.setEmail(c.getString(c.getColumnIndex("email")));
            sales.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            sales.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return sales;
    }

    // untuk mendapatkan record sales berdasarkan email, phonenumber, password
    public Sales getSales(String emailPhonenumber, String password, Boolean email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        Sales sales;
        String query;

        if (email) {
            query = "SELECT * FROM Sales where email = ? and password = ? ";
        }
        else {
            query = "SELECT * FROM Sales where phonenumber = ? and password = ? ";
        }

        c = db.rawQuery(query, new String[]{emailPhonenumber, password});

        sales = new Sales();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            sales.setIdSales(c.getInt(c.getColumnIndex("idSales")));
            sales.setName(c.getString(c.getColumnIndex("name")));
            sales.setEmail(c.getString(c.getColumnIndex("email")));
            sales.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            sales.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return sales;
    }

    // untuk memasukkan record history location
    public void insertHistoryLocation(HistoryLocation historyLocation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", String.valueOf(historyLocation.getDate()));
        values.put("idCustomer", historyLocation.getIdCustomer());
        values.put("idSales", historyLocation.getIdSales());
        values.put("idVisitPlan", historyLocation.getIdVisitPlan());
        values.put("idCircle", historyLocation.getIdCircle());

        db.insert("HistoryLocation", null, values);
        db.close();
    }

    // untuk mendapatkan record history location berdasarkan id sales, dan tanggal
    public HistoryLocation getHistoryLocation(long idSales, Date date) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM HistoryLocation where idSales = ? and date = ?";

        Cursor c = db.rawQuery(query, new String[] {String.valueOf(idSales), String.valueOf(date)});

        HistoryLocation historyLocation = new HistoryLocation();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            historyLocation.setDate(date);
            historyLocation.setIdCustomer(c.getLong(c.getColumnIndex("idCustomer")));
            historyLocation.setIdSales(c.getLong(c.getColumnIndex("idSales")));
            historyLocation.setIdVisitPlan(c.getLong(c.getColumnIndex("idVisitPlan")));
            historyLocation.setIdCircle(c.getLong(c.getColumnIndex("idCircle")));
        }

        c.close();
        db.close();

        return historyLocation;
    }

    // untuk memasukkan record circle
    public void insertCircle(Circle circle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", circle.getName());
        values.put("idSalesManager", circle.getIdSalesManager());

        db.insert("Circle", null, values);
        db.close();
    }

    // untuk mendapatkan record circle berdasarkan id circle
    public Circle getCircle(long idCircle) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Circle where idCircle = ?";

        Cursor c = db.rawQuery(query, new String[] {String.valueOf(idCircle)});

        Circle circle = new Circle();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            circle.setIdCircle(c.getLong(c.getColumnIndex("idCircle")));
            circle.setName(c.getString(c.getColumnIndex("name")));
            circle.setIdSalesManager(c.getLong(c.getColumnIndex("idSalesManager")));
        }

        c.close();
        db.close();

        return circle;
    }

    // untuk memasukkan record visit plan
    public void insertVisitPlan(VisitPlan visitPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statusVerified", visitPlan.getStatusVerified());
        values.put("date", visitPlan.getDate().toString());
        values.put("listOfCustomer", visitPlan.getListOfCustomer());
        values.put("idSales", visitPlan.getIdSales());
        values.put("idSalesManager", visitPlan.getIdSalesManager());
        values.put("timeCheckIn", visitPlan.getTimeCheckIn().toString());
        values.put("timeCheckOut", visitPlan.getTimeCheckOut().toString());
        values.put("discussion", visitPlan.getDiscussion());

        db.insert("VisitPlan", null, values);
        db.close();
    }

    // untuk mendapatkan record visit plan berdasarkan id
    public VisitPlan getVisitPlan(long idVisitPlan) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM VisitPlan where idVisitPlan = ?";

        Cursor c = db.rawQuery(query, new String[] {String.valueOf(idVisitPlan)});

        VisitPlan visitPlan = new VisitPlan();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            visitPlan.setIdVisitPlan(c.getLong(c.getColumnIndex("idVisitPlan")));
            visitPlan.setStatusVerified(c.getLong(c.getColumnIndex("statusVerified")));

            String strDate = c.getString(c.getColumnIndex("date"));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date) format.parse(strDate);

            visitPlan.setDate(date);
            visitPlan.setListOfCustomer(c.getString(c.getColumnIndex("listOfCustomer")));
            visitPlan.setIdSales(c.getLong(c.getColumnIndex("idSales")));
            visitPlan.setIdSalesManager(c.getLong(c.getColumnIndex("idSalesManager")));

            String strTimeCheckIn = c.getString(c.getColumnIndex("timeCheckIn"));
            Date timeCheckIn = (Date) format.parse(strTimeCheckIn);

            visitPlan.setTimeCheckIn(timeCheckIn);

            String strTimeCheckOut = c.getString(c.getColumnIndex("timeCheckOut"));
            Date timeCheckOut = (Date) format.parse(strTimeCheckOut);

            visitPlan.setTimeCheckOut(timeCheckOut);
            visitPlan.setDiscussion(c.getString(c.getColumnIndex("discussion")));
        }

        c.close();
        db.close();

        return visitPlan;
    }

    // untuk memasukkan record sales manager
    public void insertSalesManager(SalesManager salesManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", salesManager.getName());
        values.put("email", salesManager.getEmail());
        values.put("phonenumber", salesManager.getPhonenumber());
        values.put("password", salesManager.getPassword());

        db.insert("SalesManager", null, values);
        db.close();
    }

    // untuk mendapatkan record sales manager berdasarkan email, phone number, password
    public SalesManager getSalesManager(String emailPhonenumber, String password, Boolean email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        SalesManager salesManager;
        String query;

        if (email) {
            query = "SELECT * FROM SalesManager where email = ? and password = ?";
        }
        else {
            query = "SELECT * FROM SalesManager where phonenumber = ? and password = ?";
        }

        c = db.rawQuery(query, new String[] {emailPhonenumber, password});

        salesManager = new SalesManager();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            salesManager.setIdSalesManager(c.getLong(c.getColumnIndex("idSalesManager")));
            salesManager.setName(c.getString(c.getColumnIndex("name")));
            salesManager.setEmail(c.getString(c.getColumnIndex("email")));
            salesManager.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            salesManager.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return salesManager;
    }

    // untuk mendapatkan record sales manager berdasarkan email
    public SalesManager getSalesManagerByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        SalesManager salesManager;
        String query;

        query = "SELECT * FROM SalesManager where email = ?";

        c = db.rawQuery(query, new String[] {email});

        salesManager = new SalesManager();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            salesManager.setIdSalesManager(c.getInt(c.getColumnIndex("idSalesManager")));
            salesManager.setName(c.getString(c.getColumnIndex("name")));
            salesManager.setEmail(c.getString(c.getColumnIndex("email")));
            salesManager.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            salesManager.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return salesManager;
    }

    // untuk mendapatkan record sales manager berdasarkan email
    public SalesManager getSalesManagerByPhonenumber(String phonenumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        SalesManager salesManager;
        String query;

        query = "SELECT * FROM SalesManager where phonenumber = ?";

        c = db.rawQuery(query, new String[] {phonenumber});

        salesManager = new SalesManager();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            salesManager.setIdSalesManager(c.getInt(c.getColumnIndex("idSalesManager")));
            salesManager.setName(c.getString(c.getColumnIndex("name")));
            salesManager.setEmail(c.getString(c.getColumnIndex("email")));
            salesManager.setPhonenumber(c.getString(c.getColumnIndex("phonenumber")));
            salesManager.setPassword(c.getString(c.getColumnIndex("password")));
        }

        c.close();
        db.close();

        return salesManager;
    }
}
