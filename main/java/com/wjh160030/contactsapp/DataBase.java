package com.wjh160030.contactsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

    /*
    This class is the database class. All required database methods are here.
    The database holds only contact information and is made up of only 1 table
    Written by William Hood, Albin Mathew, wjh160030, ajm161130
     */


public class DataBase extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsDB.db";     // name of data base
    private static final String TABLE_NAME = "contacts";      // name of table
    private static final String KEY_ID = "id";
    private static final String COL_FNAME = "first_name";
    private static final String COL_LNAME = "last_name";
    private static final String COL_PHONE = "phone_number";
    private static final String COL_BIRTH = "birth_day";
    private static final String COL_FIRST_CONTACT = "first_contact";
    private static final String COL_ADRRESS_1 = "address_1";
    private static final String COL_ADRRESS_2 = "address_2";
    private static final String COL_CITY = "city";
    private static final String COL_STATE = "state";
    private static final String COL_ZIP = "zip";


    // Reequired constructor for database
    //written by William Hood
    public DataBase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Create the table in the database
    //Written by William
    @Override
    public void onCreate(SQLiteDatabase db) {

        String Create_Table = "CREATE TABLE " + TABLE_NAME + "( " + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FNAME + " TEXT NOT NULL,"
                + COL_LNAME + " TEXT NOT NULL,"
                + COL_BIRTH + " TEXT,"
                + COL_PHONE + " TEXT,"
                +COL_FIRST_CONTACT + " TEXT,"
                +COL_ADRRESS_1 + " TEXT,"
                +COL_ADRRESS_2 + " TEXT,"
                +COL_CITY + " TEXT,"
                +COL_STATE + " TEXT,"
                +COL_ZIP + " TEXT"
                + ")";
        db.execSQL(Create_Table);    // execute SQL statement
    }

    // If the table upgrades
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // execute SQL statement
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //This method will add a contact to the table
    //written by william hood
    public long addContact(ContactClass contact) {

        // SQL writable database
        SQLiteDatabase db = this.getWritableDatabase();
        // new instance of content values
        ContentValues values = new ContentValues();
        // content values
        values.put(COL_FNAME, contact.getFirst_name());
        values.put(COL_LNAME, contact.getLast_name());
        values.put(COL_BIRTH, contact.getB_day());
        values.put(COL_PHONE, contact.getPhone_number());
        values.put(COL_FIRST_CONTACT, contact.getFirst_contact());
        values.put(COL_ADRRESS_1, contact.getAddress_1());
        values.put(COL_ADRRESS_2, contact.getAddress_2());
        values.put(COL_CITY, contact.getCity());
        values.put(COL_STATE, contact.getState());
        values.put(COL_ZIP, contact.getZip());
        // get result of inserting input into the table
        long index = db.insert(TABLE_NAME, null, values);
        db.close();
        return index;

    }

    //Return the contents of the Database
    //Written by William Hood
    public ArrayList<ContactClass> getDBContents() {

        ArrayList<ContactClass> contacts_list = new ArrayList<>();
        //select all Query
        String select = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(select, null);

        if(cursor.moveToFirst()){
            do{

                Long id = cursor.getLong(0);
                String fname = cursor.getString(1);
                String lname = cursor.getString(2);
                String phone = cursor.getString(3);
                String birth = cursor.getString(4);
                String first_contact = cursor.getString(5);
                String address1 = cursor.getString(6);
                String address2 = cursor.getString(7);
                String city = cursor.getString(8);
                String state = cursor.getString(9);
                String zip = cursor.getString(10);

                ContactClass contact = new ContactClass(id, fname, lname, birth, phone, first_contact,
                        address1, address2, city, state, zip);
                contacts_list.add(contact);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return contacts_list;
    }


    // update a contact in the DB
    // Written by Albin Mathew
    public void updateContact(ContactClass contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_FNAME, contact.getFirst_name());
        values.put(COL_LNAME, contact.getLast_name());
        values.put(COL_PHONE, contact.getPhone_number());
        values.put(COL_BIRTH, contact.getB_day());
        values.put(COL_FIRST_CONTACT, contact.getFirst_contact());
        values.put(COL_ADRRESS_1, contact.getAddress_1());
        values.put(COL_ADRRESS_2, contact.getAddress_2());
        values.put(COL_CITY, contact.getCity());
        values.put(COL_STATE, contact.getState());
        values.put(COL_ZIP, contact.getZip());

        db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }


    // delete a contact fromt he database
    //WIlliam Hood
    public void deleteContact(ContactClass contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(contact.getID()) });
        db.close();

    }

    //This will get the count of contacts in the DB
    //Written by William Hood
    public int getContactsCount(){
        String count = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(count, null);
        cursor.close();

        return cursor.getCount();
    }
}