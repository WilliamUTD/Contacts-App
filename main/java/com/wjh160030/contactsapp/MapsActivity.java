package com.wjh160030.contactsapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//**********************************************************************************
//This class is the default, available maps activity that has increased functionality.
//The latitudes and longitudes are passed by intent, and done what is needed here
//Written by William Hood and Albin Mathew
// wjh160030, ajm161130
//**********************************************************************************

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView longitude, latitude, distance;
    double a_lon=0, a_lat=0, my_lat=0, my_lon=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get Intent with location details
        Intent maps = getIntent();
        a_lat = maps.getDoubleExtra("lat", 0);
        a_lon=maps.getDoubleExtra("lon", 0);
        my_lat=maps.getDoubleExtra("current_lat", 0);
        my_lon=maps.getDoubleExtra("current_lon", 0);
        Log.d("Extras:",a_lat+" "+a_lon+" "+my_lat+" "+my_lon);
        longitude = findViewById(R.id.lon);
        latitude = findViewById(R.id.lat);
        distance = findViewById(R.id.distance);

        String lng = "Longitude" + " " + String.valueOf(a_lon);
        String latt = "Latitude" + " " + a_lat;
        longitude.setText(lng);
        latitude.setText(latt);
        //call function to calc distance
        double dist = getDistance(a_lon,a_lat,my_lat,my_lon);
        String d = "Disatance from address: " + dist;
        distance.setText(d);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng( a_lat, a_lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Address"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    //Gets the distance between the two locations.
    //Used formula from geeks for geeks
    //
    double getDistance(double a_lon, double a_lat, double lon, double lat){
        double dist=0;
        double dlon = my_lon - a_lon;
        double dlat = my_lat - a_lat;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(a_lat) * Math.cos(my_lat)
                * Math.pow(Math.sin(dlon / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371;
        return (c*r);
    }
}
