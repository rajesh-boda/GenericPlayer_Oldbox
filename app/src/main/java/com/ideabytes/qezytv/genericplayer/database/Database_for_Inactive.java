package com.ideabytes.qezytv.genericplayer.database;

/**
 * Modified by Viplov on 29/5/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Database_for_Inactive extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Inactive_status_manager";

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";

    public Database_for_Inactive(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding status
    public void addStatus(Inactive_status inactive_status) {
        try{
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, inactive_status.getName()); // inactive_status Name
        values.put(KEY_STATUS, inactive_status.getInactiveStatus()); // inactive_status Phone

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // Getting single Inactive status
    public Inactive_status getStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_STATUS}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        Inactive_status inactive_status = new Inactive_status();
        try {
// your code
        if (cursor != null)
            cursor.moveToFirst();

            inactive_status = new Inactive_status(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));


            cursor.close();
        } finally {
            cursor.close();
            db.close();
        }

        return inactive_status;
    }

    // Getting All Contacts
    public List<Inactive_status> getAllContacts() {
        List<Inactive_status> contactList = new ArrayList<Inactive_status>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Inactive_status contact = new Inactive_status();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setInactiveStatus(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Inactive_status contact) {
        int rows = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {
            Log.v("Datavase_for_Inactive",contact.getName().toString());
            Log.v("Datavase_for_Inactive",contact.getInactiveStatus().toString());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_STATUS, contact.getInactiveStatus());
            rows = db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                    new String[] { String.valueOf(contact.getID()) });
                  //  new String[] { String.valueOf(contact.getID()) });
//            rows = db.update(TABLE_CONTACTS, values,
//                    null, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        // updating row
        return rows;
    }

    // Deleting single contact
    public void deleteContact(Inactive_status contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }


    // Getting contacts Count
    public int getContactsCount() {


            String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
            SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

       // db.close();

        //cursor.close();

        // return count
        return cursor.getCount();
    }

}