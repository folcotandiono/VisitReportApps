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

/**
 * Created by Folco Tandiono on 09/02/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {



    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE Customer (idCustomer integer primary key , name text, address text, customerArea text," +
                "photoExterior blob, location text, qrCode text);");
        sqLiteDatabase.execSQL("CREATE TABLE Sales (idSales integer primary key, name text);");
        sqLiteDatabase.execSQL("CREATE TABLE HistoryLocation (date datetime, idCustomer integer, " +
                "idSales integer, idVisitPlan integer, idCircle integer);");
        sqLiteDatabase.execSQL("CREATE TABLE Circle (idCircle integer primary key, name text, idSalesManager integer);");
        sqLiteDatabase.execSQL("CREATE TABLE VisitPlan (idVisitPlan integer primary key, statusVerified integer, " +
                "date datetime, listOfCustomer text, idSales integer, idSalesManager integer, timeCheckIn datetime, timeCheckOut datetime, " +
                "discussion text);");
        sqLiteDatabase.execSQL("CREATE TABLE SalesManager (idSalesManager integer not null, name text);");
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
    }

    public Customer getCustomer(long idCustomer) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM customer where idCustomer = " + idCustomer;

        Cursor c = db.rawQuery(query, null);

        Customer customer = new Customer();

        if (c != null) {
            c.moveToFirst();

            customer.setIdCustomer(c.getLong(c.getColumnIndex("idCustomer")));
            customer.setName(c.getString(c.getColumnIndex("name")));
            customer.setAddress(c.getString(c.getColumnIndex("address")));
            customer.setCustomerArea(c.getString(c.getColumnIndex("customerArea")));
            customer.setPhotoExterior(c.getBlob(c.getColumnIndex("photoExterior")));
            customer.setLocation(c.getString(c.getColumnIndex("location")));
            customer.setQrCode(c.getString(c.getColumnIndex("qrCode")));
        }

        return customer;
    }

    public void insertSales(Sales sales) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idSales", sales.getIdSales());
        values.put("name", sales.getName());

        db.insert("Sales", null, values);
    }

    public Sales getSales(long idSales) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM sales where idSales = " + idSales;

        Cursor c = db.rawQuery(query, null);

        Sales sales = new Sales();

        if (c != null) {
            c.moveToFirst();

            sales.setIdSales(c.getInt(c.getColumnIndex("idSales")));
            sales.setName(c.getString(c.getColumnIndex("name")));
        }

        return sales;
    }

    public void insertHistoryLocation(HistoryLocation historyLocation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", String.valueOf(historyLocation.getDate()));
        values.put("idCustomer", historyLocation.getIdCustomer());
        values.put("idSales", historyLocation.getIdSales());
        values.put("idVisitPlan", historyLocation.getIdVisitPlan());
        values.put("idCircle", historyLocation.getIdCircle());

        db.insert("HistoryLocation", null, values);
    }

    public HistoryLocation getHistoryLocation(long idSales, Date date) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM historyLocation where idSales = " + idSales + " and date = " + date;

        Cursor c = db.rawQuery(query, null);

        HistoryLocation historyLocation = new HistoryLocation();

        if (c != null) {
            c.moveToFirst();

            historyLocation.setDate(date);
            historyLocation.setIdCustomer(c.getLong(c.getColumnIndex("idCustomer")));
            historyLocation.setIdSales(c.getLong(c.getColumnIndex("idSales")));
            historyLocation.setIdVisitPlan(c.getLong(c.getColumnIndex("idVisitPlan")));
            historyLocation.setIdCircle(c.getLong(c.getColumnIndex("idCircle")));
        }

        return historyLocation;
    }

    public void insertCircle(Circle circle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idCircle", circle.getIdCircle());
        values.put("name", circle.getName());
        values.put("idSalesManager", circle.getIdSalesManager());

        db.insert("Circle", null, values);
    }

    public Circle getCircle(long idCircle) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM circle where idCircle = " + idCircle;

        Cursor c = db.rawQuery(query, null);

        Circle circle = new Circle();

        if (c != null) {
            c.moveToFirst();

            circle.setIdCircle(c.getLong(c.getColumnIndex("idCircle")));
            circle.setName(c.getString(c.getColumnIndex("name")));
            circle.setIdSalesManager(c.getLong(c.getColumnIndex("idSalesManager")));
        }

        return circle;
    }

    public void insertVisitPlan(VisitPlan visitPlan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idVisitPlan", visitPlan.getIdVisitPlan());
        values.put("statusVerified", visitPlan.getStatusVerified());
        values.put("date", visitPlan.getDate().toString());
        values.put("listOfCustomer", visitPlan.getListOfCustomer());
        values.put("idSales", visitPlan.getIdSales());
        values.put("idSalesManager", visitPlan.getIdSalesManager());
        values.put("timeCheckIn", visitPlan.getTimeCheckIn().toString());
        values.put("timeCheckOut", visitPlan.getTimeCheckOut().toString());
        values.put("discussion", visitPlan.getDiscussion());

        db.insert("VisitPlan", null, values);
    }

    public VisitPlan getVisitPlan(long idVisitPlan) throws ParseException {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM VisitPlan where idVisitPlan = " + idVisitPlan;

        Cursor c = db.rawQuery(query, null);

        VisitPlan visitPlan = new VisitPlan();

        if (c != null) {
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

        return visitPlan;
    }

    public void insertSalesManager(SalesManager salesManager) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("idSalesManager", salesManager.getIdSalesManager());
        values.put("name", salesManager.getName());

        db.insert("SalesManager", null, values);
    }

    public SalesManager getSalesManager(long idSalesManager) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM SalesManager where idSalesManager = " + idSalesManager;

        Cursor c = db.rawQuery(query, null);

        SalesManager salesManager = new SalesManager();

        if (c != null) {
            c.moveToFirst();

            salesManager.setIdSalesManager(c.getLong(c.getColumnIndex("idSalesManager")));
            salesManager.setName(c.getString(c.getColumnIndex("name")));
        }

        return salesManager;
    }
}
