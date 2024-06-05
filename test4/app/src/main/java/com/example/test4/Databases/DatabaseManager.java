package com.example.test4.Databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.test4.Models.Record;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Context context;
    DatabaseHelper databaseHelper;
    SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context ctxt) {
        context = ctxt;
    }

    public DatabaseManager open() throws SQLDataException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    //beginning of adulterant table methods
    public int saverecord(String sourceAddress, String adulterant) {
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(DatabaseHelper.LOCATION, sourceAddress);
        contentValues1.put(DatabaseHelper.ADULTERANT, adulterant);

        int i = (int) sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME1, null, contentValues1);
        return i;
    }

    public int updateRecord(long _id, String sourceAddress, String adulterant) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.LOCATION, sourceAddress);
        contentValues.put(DatabaseHelper.ADULTERANT, adulterant);
        int f = sqLiteDatabase.update(DatabaseHelper.TABLE_NAME1, contentValues, DatabaseHelper.ID + "=" + _id, null);
        return f;
    }


    @SuppressLint("Range")
    public List<Record> retrieveAllRecords() {
        List<Record> records = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME1, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ID));
                String sourceAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION));
                String adulterant = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADULTERANT));
                records.add(new Record(id,sourceAddress,adulterant));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;
    }

    @SuppressLint("Range")
    public Record retrieveRecord(long id) {
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME1, null, DatabaseHelper.ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String sourceAddress = cursor.getString(cursor.getColumnIndex(DatabaseHelper.LOCATION));
            String adulterant = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ADULTERANT));
            cursor.close();
            return new Record(id,sourceAddress,adulterant);
        }
        return null;
    }
    //end of adulterant table methods

    //beginning of user table methods
    public int insert(String dealername, String contact, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, dealername);
        contentValues.put(DatabaseHelper.CONTACT, contact);
        contentValues.put(DatabaseHelper.PASSWORD, password);

        int i = (int) sqLiteDatabase.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        return i;
    }

    public Cursor fetch(String dealercontact) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE contact = ? ";
        String[] selectionArgs = {dealercontact};
        Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor fetch() {
        String[] items = new String[]{DatabaseHelper.ID, DatabaseHelper.NAME, DatabaseHelper.CONTACT, DatabaseHelper.PASSWORD};
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME, items, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, String dealername, String contact, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, dealername);
        contentValues.put(DatabaseHelper.CONTACT, contact);
        contentValues.put(DatabaseHelper.PASSWORD, password);
        int f = sqLiteDatabase.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.ID + "=" + _id, null);
        return f;
    }

    public int delete(long id) {
        int r = sqLiteDatabase.delete(DatabaseHelper.TABLE_NAME1, DatabaseHelper.ID + " = " + id, null);
        return r;
    }

    public boolean checkAlreadyExists(String contact, String password) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME + " WHERE contact = ? AND password = ?";
        String[] selectionArgs = {contact, password};
        Cursor cursor1 = sqLiteDatabase.rawQuery(query, selectionArgs);
        if (cursor1.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }
    }
    //end of user table methods

   /*
    public Cursor retrieveRecord(long _id) {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_NAME1 + " WHERE " + DatabaseHelper.ID + " = ?";
        String[] selectionArgs = {String.valueOf(_id)};
        Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
   public Cursor retrieveAllRecords() {
        String[] columns = new String[]{DatabaseHelper.ID, DatabaseHelper.LOCATION, DatabaseHelper.ADULTERANT};
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_NAME1, columns, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }*/
