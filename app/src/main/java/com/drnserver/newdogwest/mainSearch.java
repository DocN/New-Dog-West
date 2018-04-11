package com.drnserver.newdogwest;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.drnserver.newdogwest.Adapter.PlaceAutocompleteAdapter;
import com.drnserver.newdogwest.Services.UserLocationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

public class mainSearch extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView locationEdit;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private static final String TAG = "mainSearch";
    private int duration = Toast.LENGTH_SHORT;
    private static UserLocationService myLocation = new UserLocationService();

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng locationCord;
    private Location currentLocation;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 13f;
    private Boolean mLocationPermissionsGranted = false;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);
        getSupportActionBar().hide();
        final Button button = findViewById(R.id.searchButton);
        locationEdit = (AutoCompleteTextView) findViewById(R.id.location);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        locationEdit.setAdapter(mPlaceAutocompleteAdapter);
        getLocationPermission();
        getDeviceLocation();

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                geoLocate();
                Intent myIntent = new Intent(mainSearch.this, ParkSearch.class);
                mainSearch.this.startActivity(myIntent);
            }
        });

        locationEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    Context context = getApplicationContext();
                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = locationEdit.getText().toString();
        if(searchString.equals("My Location")) {
            UserLocationService.setCurrentLat(currentLocation.getLatitude());
            UserLocationService.setCurrentLon(currentLocation.getLongitude());
            UserLocationService.setDisplayAddress("My Location");
        }
        Geocoder geocoder = new Geocoder(mainSearch.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            if(address.getAddressLine(0).equals("Malaysia")) {
                UserLocationService.setDisplayAddress("My Location");
                return;
            }
            UserLocationService.setCurrentLat(address.getLatitude());
            UserLocationService.setCurrentLon(address.getLongitude());
            UserLocationService.setDisplayAddress(locationEdit.getText().toString());
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: found location!");
                        currentLocation = (Location) task.getResult();
                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                        Toast.makeText(mainSearch.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

}
