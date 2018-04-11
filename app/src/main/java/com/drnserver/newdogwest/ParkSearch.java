package com.drnserver.newdogwest;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.drnserver.newdogwest.Adapter.PlaceAutocompleteAdapter;
import com.drnserver.newdogwest.Models.PlaceProperties;
import com.drnserver.newdogwest.Services.PlacesDataService;
import com.drnserver.newdogwest.Services.UserLocationService;
import com.drnserver.newdogwest.Services.YelpData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.yelp.fusion.client.models.*;

public class ParkSearch extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private ArrayList<Parks> parkList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParkAdapter mAdapter;
    private Spinner chooseList;
    private String[] choices = new String[]{"Pet Parks", "Pet Stores", "Pet Clinic", "Pet Care", "Pet Groom", "Pet Training", "Pet Food"};
    private ProgressDialog pDialog;
    private ListView lv;
    private String TAG = ParkSearch.class.getSimpleName();
    private int type;
    private boolean first;
    private AutoCompleteTextView addressText;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private TextView titleText;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng locationCord;
    private Location currentLocation;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    // URL to get contacts JSON

    //private static String url = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
    private static String url = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PlacesDataService pDataServ = new PlacesDataService();
        PlacesDataService.PlaceDataList = new ArrayList<PlaceProperties>();
        addressText = (AutoCompleteTextView)findViewById(R.id.addressText);
        addressText.setText(UserLocationService.getDisplayAddress());
        addressText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    Context context = getApplicationContext();
                    //execute our method for searching
                    geoLocate();
                    new getParks().execute();
                }

                return false;
            }
        });


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        addressText.setAdapter(mPlaceAutocompleteAdapter);
        titleText = (TextView)findViewById(R.id.titleText);
        //new west data array
        first = true;

        getDeviceLocation();
        mAdapter = new ParkAdapter(PlacesDataService.PlaceDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(ParkSearch.this, ParkDetails.class);
                //store index of item.
                myIntent.putExtra("position", position);
                ParkSearch.this.startActivity(myIntent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // ...
            }
        }));
        //spinner for choice selection
        chooseList = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);
        chooseList.setAdapter(adapter);

        chooseList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = chooseList.getItemAtPosition(position).toString();
                type = position;
                PlacesDataService.selected = selected;
                if(first == true) {
                    first = false;
                }
                else {
                    new getParks().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        type = 0;
        new getParks().execute();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = addressText.getText().toString();
        if(searchString.equals("My Location")) {
            UserLocationService.setCurrentLat(currentLocation.getLatitude());
            UserLocationService.setCurrentLon(currentLocation.getLongitude());
        }
        Geocoder geocoder = new Geocoder(ParkSearch.this);
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
            UserLocationService.setDisplayAddress(addressText.getText().toString());
            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.openSettings:
                Intent myIntent = new Intent(ParkSearch.this, DogRadar.class);
                ParkSearch.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        Toast.makeText(ParkSearch.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class getParks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ParkSearch.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            PlacesDataService.PlaceDataList.clear();
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray features = jsonObj.getJSONArray("features");

                    // looping through All Contacts
                    for (int i = 0; i < features.length(); i++) {
                        PlaceProperties currentPark = new PlaceProperties();
                        JSONObject c = features.getJSONObject(i);
                        JSONObject property = c.getJSONObject("properties");
                        /*
                        JSONArray coordinates = c.getJSONObject("geometry").getJSONArray("coordinates");
                        System.out.println(coordinates);
                        JSONArray coordinatesValue = coordinates.getJSONArray(0);
                        System.out.println(coordinatesValue.getString(0));
                        //System.out.println(coordinatesValue);
                        */
                        currentPark.setStrName(property.getString("StrName"));
                        currentPark.setStrNum(property.getString("StrNum"));
                        currentPark.setParkName(property.getString("Name"));
                        currentPark.setCategory(property.getString("Category"));
                        currentPark.setNeighbourhood(property.getString("Neighbourhood"));
                        currentPark.setZone_category(property.getString("Zone_Category"));
                        currentPark.setSurveyed(property.getString("Surveyed"));
                        currentPark.setSiteArea(property.getString("Site_Area"));
                        currentPark.setLat(property.getString("StrName"));
                        currentPark.setLon(property.getString("StrName"));
                        currentPark.setIndex(i);
                        //-122.9276924813239, 49.21386827563567
                        /*
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                        */
                    }
                    YelpData yelper = new YelpData();
                    String term = choices[type];
                    //testing values
                    String lat = "49.21386827563567";
                    String longi = "-122.9276924813239";

                    //String lat = "" + UserLocationService.getCurrentLat();
                    //String longi = "" + UserLocationService.getCurrentLon();
                    //private String[] choices = new String[]{"Pet Parks", "Pet Stores", "Pet Clinic", "Pet Care", "Pet Groom", "Pet Training", "Pet Food"};
                    if(term.equals("Pet Parks")) {
                        yelper.businessSearch("parks", lat, longi, "50", "Parks");
                    }else {
                        yelper.businessSearch(term, lat, longi, "50", "");
                    }

                    for(int i =0; i<yelper.getBusinesses().size(); i++) {
                        Business currentBusiness = yelper.getBusinesses().get(i);
                        PlaceProperties currentPlace = new PlaceProperties();
                        currentPlace.setIndex(i);
                        currentPlace.setBusiness(currentBusiness);
                        currentPlace.setParkName(currentBusiness.getName());
                        currentPlace.setImgUrl(currentBusiness.getImageUrl());
                        currentPlace.setType(term);
                        double businessLat = currentBusiness.getCoordinates().getLatitude();
                        double businessLon = currentBusiness.getCoordinates().getLongitude();

                        double distance = GetDistanceFromLatLonInKm(businessLat, businessLon, UserLocationService.getCurrentLat(), UserLocationService.getCurrentLon());
                        currentPlace.setDistance(round2(distance));
                        String address = addressFilter(currentBusiness);
                        if(!address.equals("")) {
                            address = address + "\n";
                        }
                        currentPlace.setAddress(address + currentBusiness.getLocation().getCity() + ", " + currentBusiness.getLocation().getState() + " " + currentBusiness.getLocation().getZipCode());
                        currentPlace.setLat(currentBusiness.getCoordinates().getLatitude() + "");
                        currentPlace.setLon(currentBusiness.getCoordinates().getLongitude() + "");
                        System.out.println(currentPlace.getAddress() + " " + currentBusiness.getLocation().getCity() + ", " + currentBusiness.getLocation().getState() + " " + currentBusiness.getLocation().getZipCode());
                        PlacesDataService.PlaceDataList.add(currentPlace);
                    }
                    PlacesDataService.sortParkData();
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
        protected double round2(double val) {
            val = val*100;
            val = Math.round(val);
            val = val /100;
            return val;
        }
        protected String addressFilter(Business currentBusiness) {
            String address = "";

            if(currentBusiness.getLocation().getAddress1() == null || currentBusiness.getLocation().getAddress1().equals("")) {
                if(currentBusiness.getLocation().getAddress2() == null || currentBusiness.getLocation().getAddress2().equals("")) {
                    if(currentBusiness.getLocation().getAddress3() == null || currentBusiness.getLocation().getAddress3().equals("")) {

                    }
                    else {
                        address = currentBusiness.getLocation().getAddress3();
                    }
                }
                else {
                    address = currentBusiness.getLocation().getAddress2();
                }
            }
            else {
                address = currentBusiness.getLocation().getAddress1();
            }
            return address;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                mAdapter.notifyDataSetChanged();
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            titleText.setText(PlacesDataService.selected + " Nearby");
        }

        public double GetDistanceFromLatLonInKm(double user1Lat, double user1Lon,  double user2Lat, double user2Lon)
        {
            double R = 6371; // Radius of the earth in km
            double dLat = Deg2rad(user2Lat - user1Lat);  // deg2rad below
            double dLon = Deg2rad(user2Lon - user1Lon);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +  Math.cos(Deg2rad(user1Lat)) * Math.cos(Deg2rad(user2Lat)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = R * c; // Distance in km
            return d;
        }

        public double Deg2rad(double deg)
        {
            return deg * (Math.PI / 180);
        }
    }
}