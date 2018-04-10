package com.drnserver.newdogwest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.drnserver.newdogwest.Models.PlaceProperties;
import com.drnserver.newdogwest.Services.PlacesDataService;
import com.drnserver.newdogwest.Services.YelpData;
import com.yelp.fusion.client.models.*;

public class ParkSearch extends AppCompatActivity {
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
        //new west data array
        first = true;

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
                    String lat = "49.21386827563567";
                    String longi = "-122.9276924813239";
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
                        currentPlace.setDistance(round2(currentBusiness.getDistance()/1000));
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
        }
    }
}