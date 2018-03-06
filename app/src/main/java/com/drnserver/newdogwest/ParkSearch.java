package com.drnserver.newdogwest;

import android.content.Intent;
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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ParkSearch extends AppCompatActivity {
    private ArrayList<Parks> parkList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParkAdapter mAdapter;
    private ProgressDialog pDialog;
    private ListView lv;
    private String TAG = ParkSearch.class.getSimpleName();
    private ArrayList<HashMap<String, String>> contactList;

    // URL to get contacts JSON

    //private static String url = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";
    private static String url = "http://opendata.newwestcity.ca/downloads/parks/PARKS.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_search);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ParkAdapter(parkList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent myIntent = new Intent(ParkSearch.this, ParkDetails.class);
                ParkSearch.this.startActivity(myIntent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // ...
            }
        }));
        contactList = new ArrayList<>();
        new GetContacts().execute();
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


    private void prepareMovieData() {
        Parks park = new Parks("Queen's Park", "3rd Ave, New Westminster, BC V3M 1V2", "50km");
        parkList.add(park);

        park = new Parks("Westminster Pier Park", "1 Sixth St, New Westminster, BC V3M 6Z6", "20.8km");
        parkList.add(park);
        park = new Parks("Queen's Park", "3rd Ave, New Westminster, BC V3M 1V2", "50km");
        parkList.add(park);

        park = new Parks("Westminster Pier Park", "1 Sixth St, New Westminster, BC V3M 6Z6", "20.8km");
        parkList.add(park);
        park = new Parks("Queen's Park", "3rd Ave, New Westminster, BC V3M 1V2", "50km");
        parkList.add(park);

        park = new Parks("Westminster Pier Park", "1 Sixth St, New Westminster, BC V3M 6Z6", "20.8km");
        parkList.add(park);
        park = new Parks("Queen's Park", "3rd Ave, New Westminster, BC V3M 1V2", "50km");
        parkList.add(park);

        park = new Parks("Westminster Pier Park", "1 Sixth St, New Westminster, BC V3M 6Z6", "20.8km");
        parkList.add(park);
        park = new Parks("Queen's Park", "3rd Ave, New Westminster, BC V3M 1V2", "50km");
        parkList.add(park);

        park = new Parks("Westminster Pier Park", "1 Sixth St, New Westminster, BC V3M 6Z6", "20.8km");
        parkList.add(park);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

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
                        JSONObject c = features.getJSONObject(i);
                        System.out.println(c.get("properties.StrName"));
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
        }
    }
}