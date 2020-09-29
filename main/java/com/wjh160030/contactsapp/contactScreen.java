package com.wjh160030.contactsapp;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


//**********************************************************************************
//This file is for the second acticvity screen. Displays the contact details and allows the user to save the contact.
//Written by William Hood and Albin Mathew
// wjh160030, ajm161130
//**********************************************************************************


public class contactScreen extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText fn, ln, pn, adr1, adr2, city_entry, state_entry, zipCode_entry;
    TextView birth, ftc;
    Button btn_save, btn_delete;
    ArrayList<ContactClass> contactList;
    int DATE_TYPE;
    boolean existed;
    ContactClass contact;
    //need error message on too large of fields
    LocationManager locationManager;
    Double lat, lon, address_lon, address_lat;
    final int MY_PERMISSIONS_REQUESt = 10001;
    String provider;
    String requestUrl;
    String frontURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String key = "&key=AIzaSyBjgAblMxojI-49QyV4nc7rcyzeiDuM7QU";

    //**************************************
    //WRITTEN BY WILLIAM HOOD
    //Default method call for activities
    //**************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_screen);
        contactList = new ArrayList<ContactClass>();
        //connecting the views.
        fn = findViewById(R.id.fname); //first name
        ln = findViewById(R.id.lname); //last name
        pn = findViewById(R.id.phone); //phone number
        birth = findViewById(R.id.birth); //birth day
        ftc = findViewById(R.id.contact); //first contact date
        adr1 = findViewById(R.id.adr1); //
        adr2 = findViewById(R.id.adr2);
        city_entry = findViewById(R.id.city_entry);
        state_entry = findViewById(R.id.state_entry);
        zipCode_entry = findViewById(R.id.zip_entry);
        btn_save = findViewById(R.id.save);
        btn_delete = findViewById(R.id.delete);
        ContactClass contact = null;
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
        ftc.setText(currentDate);

        //get data from intent
        try {
            Intent intent = getIntent();

            if (intent.hasExtra("contact")) {
                existed = true;
                try {
                    contact = intent.getParcelableExtra("contact");

                    fn.setText(contact.getFirst_name().trim());
                    ln.setText(contact.getLast_name().trim());
                    pn.setText(contact.getPhone_number().trim());
                    birth.setText(contact.getB_day());
                    ftc.setText(contact.getFirst_contact());
                    adr1.setText(contact.getAddress_1());
                    adr2.setText(contact.getAddress_2());
                    city_entry.setText(contact.getCity());
                    state_entry.setText(contact.getState());
                    zipCode_entry.setText(contact.getZip());

                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }


        //getting index from passed contact
        final long index = contact != null ? contact.getID() : -1;
        if (index == -1)
            existed = false;
        //setting the save function
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (existed)
                    updateContact(index);
                else
                    saveContact();
            }
        });

        //setting the delete function
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(index);
            }
        });

        //set so that the dialog pops up on click
        birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                DATE_TYPE = 1;
            }
        });

        //set so that the dialog pops up on click
        ftc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                DATE_TYPE = 2;
            }
        });

        //initializing locatino
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPerms();
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    //******************************************************
    //This is is called after onCreate and infaltes the menu
    //Written by WIlliam Hood
    //******************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbarmenu, menu);
        return true;
    }


    //**************************************
    //OnClick function of save button
    //WRITTEN BY WILLIAM HOOD
    //**************************************
    void saveContact() {
        //check fields.
        String first = fn.getText().toString(),
                last = ln.getText().toString(),
                phone = pn.getText().toString(),
                address1 = adr1.getText().toString(),
                address2 = adr2.getText().toString(),
                city = city_entry.getText().toString(),
                state = state_entry.getText().toString(),
                zip = zipCode_entry.getText().toString();
        String bday = birth.getText().toString(),
                firstcontact = ftc.getText().toString();
        //create class
        ContactClass newcontact = new ContactClass(first, last, phone, bday, firstcontact, address1,
                address2, city, state, zip);
        //print contact to log
        Intent intentReturn = new Intent();
        intentReturn.putExtra("contact", newcontact);
        setResult(1, intentReturn);
        finish();
    }

    //**************************************
    //OnClick function of save button to update contact
    //WRITTEN BY WILLIAM HOOD
    //**************************************
    void updateContact(long index) {
        //check fields.
        String first = fn.getText().toString(),
                last = ln.getText().toString(),
                phone = pn.getText().toString(),
                address1 = adr1.getText().toString(),
                address2 = adr2.getText().toString(),
                city = city_entry.getText().toString(),
                state = state_entry.getText().toString(),
                zip = zipCode_entry.getText().toString();
        String bday = birth.getText().toString(),
                firstcontact = ftc.getText().toString();
        //update fields

        ContactClass updatedContact = new ContactClass(first, last, phone, bday, firstcontact, address1,
                address2, city, state, zip);
        //returning the updated contact
        Intent intentReturn = new Intent();
        intentReturn.putExtra("contact", updatedContact);
        setResult(20, intentReturn);
        finish();

    }

    //*****************************************************************************
    //This class sends the signal to the main screen that a deletion is requested.
    //Written by william Hood
    //**************************************************************************
    void delete(Long index) {
        String first = fn.getText().toString(),
                last = ln.getText().toString(),
                phone = pn.getText().toString(),
                address1 = adr1.getText().toString(),
                address2 = adr2.getText().toString(),
                city = city_entry.getText().toString(),
                state = state_entry.getText().toString(),
                zip = zipCode_entry.getText().toString();
        String bday = birth.getText().toString(),
                firstcontact = ftc.getText().toString();
        //update fields

        ContactClass uc = new ContactClass(first, last, phone, bday, firstcontact, address1,
                address2, city, state, zip);
        Intent intentReturn = new Intent();
        intentReturn.putExtra("contact", uc);
        setResult(10, intentReturn);
        finish();

    }

    //*****************************************************************************
    //This allows menu items to be selected
    //written by William Hood
    //*****************************************************************************
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(this, "Cannot add contact from this screen.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.back:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.fileToDB:
            case R.id.recreate:
                Toast.makeText(this, "Use in main screen", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.globe:
                //if there is an address launch the map, otherwise nothing
                if (adr1.getText() != null || adr2.getText() != null || !adr1.getText().toString().isEmpty() || !adr2.getText().toString().isEmpty()) {
                    //getting current lat and lon to pass to maps
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPerms();
                    }
                    formatHTTP();
                    new GetAddressLatLon().execute(requestUrl);
                }

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //#########################################################################
    //This is what the fragment opens when a date is selected.
    //written by William Hood
    //#########################################################################
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(c.getTime());
        if (DATE_TYPE == 1) birth.setText(currentDate);
        if (DATE_TYPE == 2) ftc.setText(currentDate);


    }

    //#########################################################################
    //Location LIstener for location updates
    //Implemented by William Hood
    //#########################################################################
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //makeuseofNewLocation
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d("maps location", lat + " " + lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    //#########################################################################
    //onResume, sometimes called after maps activity if the activity was actvie
    // for long enough
    //Written by William Hood
    //#########################################################################
    public void onResume(){
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPerms();
        }


    }
    //#########################################################################
    //Covnieniece function to get poermissions if they are not already allowed
    //Written by William Hood
    //#########################################################################
    void requestPerms(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        }, MY_PERMISSIONS_REQUESt);
    }

    ////#########################################################################
    //Function that tries to get the HTTP response using the address
    //to get the latitude and longitude of the address
    //Written by William Hood
    ////#########################################################################
    class GetAddressLatLon extends AsyncTask<String, Void, String> {
        //**********************************************************
        //Used to connect to the http request
        //Builds String as JSON
        //**********************************************************
        //Written by William Hood
        //**********************************************************
        @Override
        protected String doInBackground(String... strings) {

            //http request to get long and lat
            URL url;
            String jsonRepsonse = "";
            try {
                url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");

                conn.connect();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null)
                        jsonRepsonse = jsonRepsonse + line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonRepsonse;
        }
        //**********************************************************
        //Used to get current location and get address location
        //Then launches maps actvity
        //Using this because I didnt use goelocator or whatever and
        //networking must be async. SO fun.
        //**********************************************************
        //Written by William Hood
        //**********************************************************
        protected void onPostExecute(String s){
            //parse JSON
            try {
                JSONObject json = new JSONObject(s);
                address_lon = ((JSONArray) json.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                address_lat = ((JSONArray) json.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                Log.d("Location", address_lat + " "+ address_lon);
                //Gettting last known location
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                provider = locationManager.getBestProvider(new Criteria(), false);
                if (ActivityCompat.checkSelfPermission(contactScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(contactScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPerms();
                }
                final Location location = locationManager.getLastKnownLocation(provider);
                Log.d("asdf", lon + " " + lat);
                //setting values
                Intent maps = new Intent(contactScreen.this, MapsActivity.class);
                maps.putExtra("lat", address_lat);
                maps.putExtra("lon", address_lon);
                maps.putExtra("current_lat", lat);
                maps.putExtra("current_lon", lon);
                startActivity(maps);

            } catch (Exception e) {

            }
        }
    }


    void formatHTTP(){
        String add, c, s,p1="+",p2="+";
        add = adr1.getText().toString().trim().replaceAll(" ", "+");
        add += ",";


        c=city_entry.getText().toString().trim().replaceAll(" ", "+");
        c += ",";
        p1= p1 + c;

        s=state_entry.getText().toString();
        p2 = p2 + s;
        String address = add +p1 +p2;

        Log.d("Address", address);

        requestUrl = frontURL+address+key;
    }
}
