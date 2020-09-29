package com.wjh160030.contactsapp;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
//**********************************************************************************
//This class hold all the contact information and has multiple contrusctors
//to allow for variety of creation.
//This class is also parcelable
//Written by William Hood and Albin Mathew
// wjh160030, ajm161130
//**********************************************************************************

public class ContactClass implements Parcelable, Comparable<ContactClass> {

    String first_name;
    String last_name;
    String b_day;
    String first_contact;
    String phone_number;
    String address_1;
    String address_2;
    String city;
    String state;
    String zip;
    long ID;
    final String DEFAULT_NAME = String.format("%-25s", " ");
    final String DEFAULT_NUMBER = String.format("%-10s", " ");
    final String DEFAULT_DATE = String.format("%-10s", " ");


    //very default constructor
    //WRITTEN BY WILLIAM HOOD
    ContactClass(){
        first_name = last_name = DEFAULT_NAME;
        b_day = first_contact =  DEFAULT_DATE;
        phone_number = DEFAULT_NUMBER;
        state = city = address_1 = address_2 = zip = null;
        ID = -1;

    }

    //default cosntructor depending on what is passed
    //WRITTEN BY WILLIAM HOOD
    ContactClass(String s){
        first_name = s.substring(0,25);
        last_name = s.substring(25,50);
        phone_number = s.substring(50,60);
        b_day = s.substring(60, 68);
        first_contact=s.substring(68);
    }

    ContactClass(Long id, String fn, String ln, String pn, String bd, String fc, String a1,
                 String a2, String city, String state, String zipCode){
        ID = id;
        first_name= String.format("%-25s", fn);
        last_name=String.format("%-25s", ln);
        phone_number=String.format("%-10s", pn);
        b_day=bd;
        first_contact = fc;
        address_1 =a1;
        address_2 = a2;
        this.state = state;
        this.city=city;
        zip = zipCode;
        //buffer();

    }

    //default constructor
    //WRITTEN BY WILLIAM HOOD
    ContactClass(String fn, String ln, String pn, String bd, String fc, String a1,
                 String a2, String city, String state, String zipCode){
        first_name= String.format("%-25s", fn);
        last_name=String.format("%-25s", ln);
        phone_number=String.format("%-10s", pn);
        b_day=bd;
        first_contact = fc;
        address_1 =a1;
        address_2 = a2;
        this.state = state;
        this.city=city;
        zip = zipCode;
        //buffer();

    }

    void setID(long id){
        ID = id;
    }
    //buffering names if they arent 25 length
    //WRITTEN BY WILLIAM HOOD
    void buffer(){
        for(int i = 25-first_name.length(); i < 25; i++){
            first_name+=" ";
        }
        for(int i = 25-last_name.length(); i < 25; i++){
            last_name+=" ";
        }
        if(phone_number.length() < 10)  phone_number= DEFAULT_NUMBER;

        if(first_contact.length() < 8) first_contact = DEFAULT_DATE;

        if ( b_day .length() < 8) b_day=DEFAULT_DATE;
    }


    //Getters automatically generated
    public String getLast_name() {
        return last_name;
    }
    public String getFirst_name() {
        return first_name;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public String getFirst_contact() {
        return first_contact;
    }
    public String getB_day() {
        return b_day;
    }
    public String getLastName(){
        return last_name;
    }
    public Long getID() { return ID;}
    public String getState() {
        return state;
    }
    public String getCity() {
        return city;
    }
    public String getAddress_2() {
        return address_2;
    }
    public String getAddress_1() {
        return address_1;
    }
    public String getZip() {
        return zip;
    }


    //ToString method for ease of writing
    //WRITTEN BY WILLIAM HOOD
    public String toString(){
       String full =  Long.toString(ID)+first_name+last_name+phone_number+b_day+first_contact+
               address_1+address_2+city+state+zip;
       return full;
    }


    //this apparently isnt important
    //WRITTEN BY WILLIAM HOOD
    @Override
    public int describeContents() {
        return 0;
    }


    //This telsl how the object is passed and read
    //WRITTEN BY WILLIAM HOOD
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ID);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(phone_number);
        dest.writeString(b_day);
        dest.writeString(first_contact);
        dest.writeString(address_1);
        dest.writeString(address_2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zip);
    }

    //reading from parceable
    //WRITTEN BY WILLIAM HOOD
    public ContactClass(Parcel parcel){
        ID = parcel.readLong();
        first_name = parcel.readString();
        last_name = parcel.readString();
        phone_number = parcel.readString();
        b_day =parcel.readString();
        first_contact=parcel.readString();
        address_1 = parcel.readString();
        address_2 = parcel.readString();
        city = parcel.readString();
        state = parcel.readString();
        zip = parcel.readString();
    }

    //creator - used when un-parceling our parcle (creating the object)
    //refrenced from https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/
    public static final Creator<ContactClass> CREATOR = new Creator<ContactClass>(){

        @Override
        public ContactClass createFromParcel(Parcel parcel) {
            return new ContactClass(parcel);
        }

        @Override
        public ContactClass[] newArray(int size) {
            return new ContactClass[0];
        }
    };

    //Sorting method
    //Written by William Hood
    @Override
    public int compareTo(ContactClass o) {
        return this.getLast_name().compareToIgnoreCase(o.getLastName());
    }
}
