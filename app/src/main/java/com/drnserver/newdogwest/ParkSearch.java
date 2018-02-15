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

public class ParkSearch extends AppCompatActivity {
    private ArrayList<Parks> parkList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ParkAdapter mAdapter;

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

}