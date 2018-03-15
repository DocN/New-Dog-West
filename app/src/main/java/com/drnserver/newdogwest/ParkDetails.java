package com.drnserver.newdogwest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.drnserver.newdogwest.Services.PlacesDataService;
import com.squareup.picasso.Picasso;

public class ParkDetails extends AppCompatActivity {

    private TextView titleTextView;
    private TextView pDetailAddressText;
    private ImageView stockImage;
    private int parkDataIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_details);
        Intent thisIntent = getIntent();
        Bundle b = thisIntent.getExtras();
        if(b != null) {
            parkDataIndex = (int) b.get("position");
        }
        this.initItems();
    }

    private void initItems() {
        //init view items
        titleTextView = findViewById(R.id.pDetailTitleView);
        titleTextView.setText(PlacesDataService.parkDataList.get(parkDataIndex).getParkName());
        pDetailAddressText = findViewById(R.id.pDetailAddressText);
        pDetailAddressText.setText(PlacesDataService.parkDataList.get(parkDataIndex).getAddress());
        Context context = this;
        stockImage = findViewById(R.id.stock);
        try {
            Picasso.with(context).load(PlacesDataService.parkDataList.get(parkDataIndex).getImgUrl()).into(stockImage);
        } catch(RuntimeException e) {

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
                Intent myIntent = new Intent(ParkDetails.this, DogRadar.class);
                ParkDetails.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
