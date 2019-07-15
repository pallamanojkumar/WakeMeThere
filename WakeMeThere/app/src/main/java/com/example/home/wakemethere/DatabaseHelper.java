package com.example.home.wakemethere;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "Alarm.db";
    public static String TABLE_NAME = "alarmdata";

    public static String COL_ID = "id";
    public static String COL_NAME = "name";
    public static String COL_LATLNG = "latlng";
    public static String COL_DIST = "distance";
    public static String COL_ISENABLED = "isenabled";
    public static String COL_MESSAGE = "message";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        Log.e("mmessage", "create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("mmessage", "create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertDataToTable(String name, String latlng, String distance, String isEnabled, String mesage) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");

        Log.e("mmessage", "create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");


        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_LATLNG, latlng);
        contentValues.put(COL_DIST, distance);
        contentValues.put(COL_ISENABLED, isEnabled);
        contentValues.put(COL_MESSAGE, mesage);
        long status = db.insert(TABLE_NAME, null, contentValues);
        return status != -1;
    }

    public boolean updateDataInTable(String id, String name, String latlng, String distance, String isEnabled, String mesage) {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("create table IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");
//
//        Log.e("mmessage","create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance TEXT, isenabled TEXT, message TEXT)");


        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_LATLNG, latlng);
        contentValues.put(COL_DIST, distance);
        contentValues.put(COL_ISENABLED, isEnabled);
        contentValues.put(COL_MESSAGE, mesage);
        long rowCount = db.update(TABLE_NAME, contentValues, "id = " + id, null);
//        long status =  db.insert(TABLE_NAME, null, contentValues);
        return rowCount > 0;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);

        return res;
    }
}
