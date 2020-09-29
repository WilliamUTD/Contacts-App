package com.wjh160030.contactsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

//**********************************************************************************
//This is the main screen which displays the contact list and will update when one is added
//Written by William Hood and Albin Mathew,
// wjh160030, ajm161130
//**********************************************************************************
public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //activity values
    fileIOclass output_to_file;
    RecyclerView recycler;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ContactAdapter CA;
    ArrayList<ContactClass> list;
    int LastPostition;
    //sensor values
    Sensor accelerometer;
    SensorManager sm;
    float firstMove;// acceleration value
    float lastMove;// last acceleration value
    float shake;// the difference of acceleration

    DataBase contactDB;     //new Database to replace the fileIO



    //*****************************************************
    //default onCreate, always called when screen is made
    //written by William Hood
    //*****************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get maps permission


        does_DB_Exist();
        contactDB = new DataBase(this);
        list = contactDB.getDBContents();

        output_to_file = new fileIOclass(this.getFilesDir());
        //list = output_to_file.getContactsList();
        if (list.size() > 1) {
            sortList();
        }



        //list.add(new ContactClass("john","adam","123-123-1232","12/12/1234","12/12/1234"));
        recycler = findViewById(R.id.recycle);
        layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        CA = new ContactAdapter(this, list);
        recycler.setAdapter(CA);

        // Gets sensor manager services
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Gets Accelerometer
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register listener for sensor
        sm.registerListener((SensorEventListener) this, accelerometer, sm.SENSOR_DELAY_NORMAL);

        // Default values needed for detecting shaking of phone
        firstMove = SensorManager.GRAVITY_EARTH;
        lastMove = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;


    }

    //*************************************************************************
    //This is the sensor events that detects when the accelerometer is active
    //after a certain threshold, the list changes.
    //Written by William Hood, help from resources
    //************************************************************************
    @Override
    public void onSensorChanged(SensorEvent event) {

        // x, y, z coordinates
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // finding movement
        lastMove = firstMove;
        firstMove = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float movement_change = firstMove - lastMove;
        // change in movement for event to happen
        shake = shake + .9f * movement_change;

        // if phone is moving fast enough. So it wont accidentally happen
        if (shake > 7) {
            //reverse contacts
            Collections.reverse(list);
            CA.notifyDataSetChanged();
        }
    }

    // must be implemented for sensorEvents
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //*****************************************************
    //This class is the onclick listener for menu items/icons
    //Written by william hood
    //*****************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbarmenu, menu);
        return true;
    }


    //**********************************************
    //when an action bar item is pressed
    //written by WILLIAM HOOD
    //**********************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
               launchSecondActivity();
                return true;
            case R.id.back:
                return true;
            case R.id.recreate:
                reinstantiateDB();
                return true;
            case R.id.fileToDB:
                importToDB();
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //**************************************************************
    //Workaround for onActivityResult not being called from options menu
    //wirtten by William Hood
    //**************************************************************
    public void launchSecondActivity() {
        Intent intent = new Intent(this, contactScreen.class);
        startActivityForResult(intent, 1);
    }
    public void launchSecondActivity(ContactClass contact, int position) {
        Intent intent = new Intent(this, contactScreen.class);
        intent.putExtra("contact", contact);
        startActivityForResult(intent, 1);
        LastPostition = position;
    }

    //******************************
    //on return of second activity
    //Written by william hood
    //*******************************
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContactClass contact = new ContactClass();
        //try adn get data from intent return
        try{
            contact = data.getParcelableExtra("contact");
        }catch(Exception e){}
        String code = Integer.toString(resultCode);
        //determine if adding or deleting contact
        switch (resultCode) {
            case 1: insertElement(contact);
                //contactDB.addContact(contact);
            break;
            case 10: findElementAndRemove(contact);
            break;
            case 20: updateContact(contact);
            break;
            default:
        }
    }

    //*************************************************************
    //in the case of delting an element, we find it in the list
    //and remvove it from the lsit
    //Written by william Hood
    //**************************************************************
    private void findElementAndRemove(ContactClass contact) {
        contactDB.deleteContact(contact);
        Log.d("a", String.valueOf(contact.getID()));
        for(int i=0; i<list.size();i++){
            if(list.get(i).getID() == contact.getID()){
                list.remove(i);
                CA.notifyDataSetChanged();
                return;
            }
        }
    }

    //**************************************************************
    //If we start off with a list it needs to be sorted.
    //Written by William Hood
    //**************************************************************
    public void sortList () {
        Collections.sort(list);
    }

    //**************************************************************
    //When inserting an element, it must be done alphabetically.
    //This function inserts based off of last name.
    //Written by William Hood
    //**************************************************************
    public void insertElement(ContactClass contact){
        long index;
        index = contactDB.addContact(contact);
        contact.setID(index);
        list.add(contact);
        Collections.sort(list);
        CA.notifyDataSetChanged();


    }

    //***************************************************************
    //This function updates contacts within the database
    //Written by William hood
    //***************************************************************
    public void updateContact(ContactClass contact){
        list.set(LastPostition, contact);
        contactDB.updateContact(contact);
        CA.notifyItemChanged(LastPostition);
        sortList();
    }


    //***************************************************************
    //This function will create the Database if it does not exist.
    //Written by William hood
    //***************************************************************
    public void does_DB_Exist(){
        try{
            //check if database exists, hopefully this is the right path.
            SQLiteDatabase db = this.openOrCreateDatabase("contactsDB.db", Context.MODE_PRIVATE, null);
            db.close();

        }
        catch (Exception e){
            //database could not be created.
        }
        //database exists if we made it here

    }

    //***************************************************************
    //This function will re-create the database at the users discretion
    //Written by William hood
    //***************************************************************
    public void reinstantiateDB(){
        this.deleteDatabase("contactsDB.db");
        SQLiteDatabase db = this.openOrCreateDatabase("contactsDB.db", MODE_PRIVATE, null);
        db.close();
        list.clear();
        CA.notifyDataSetChanged();
    }

    //***************************************************************
    //This function will read the contacts file and import any contacts to the DB
    //Written by William hood
    //***************************************************************
    public void importToDB(){
       ArrayList<ContactClass> cList = output_to_file.getContactsList();
       list.addAll(cList);
       CA.notifyDataSetChanged();
       //short hand for loop
       for(ContactClass c : cList){
            contactDB.addContact(c);
       }
    }
}
