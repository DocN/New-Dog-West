package com.drnserver.newdogwest.Services;

import com.drnserver.newdogwest.Models.PlaceProperties;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by DrN on 3/14/2018.
 */

public class YelpData {
    private String apiKey = "ze6ePp2hwKWWuCujlYgh3Irqiq5_WY9-7sJRyeZD-rmAx6e5QpPYl8f-lFleNMJ-P_tP4CPSrI6xCMVOi_W1dPUwSZJPwALpJBDkmVUP3MZQQXxLNDhx3LB5UNWEWnYx";
    private YelpFusionApiFactory apiFactory;
    private YelpFusionApi yelpFusionApi;
    protected ArrayList<Business> businesses;

    public YelpData() {
        apiFactory = new YelpFusionApiFactory();
        try {
            yelpFusionApi = apiFactory.createAPI(apiKey);
        } catch(IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    public ArrayList<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(ArrayList<Business> businesses) {
        this.businesses = businesses;
    }

    public void businessSearch(String term, String lat, String longi, String limit, String category) {
        Map<String, String> params = new HashMap<>();
        // general params
        //-122.9276924813239, 49.21386827563567
        params.put("term", term);
        params.put("latitude", lat);
        params.put("longitude", longi);
        params.put("limit", limit);
        params.put("categories", category);
        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        try {
            Response<SearchResponse> response = call.execute();
            SearchResponse searchResponse = response.body();
            // Update UI text with the searchResponse.
            System.out.println("Searched Yelp!");
            YelpData.this.businesses = searchResponse.getBusinesses();
            for(int i =0; i < businesses.size(); i++) {
                System.out.println(businesses.get(i).getName());
                System.out.println(businesses.get(i).getDistance());
            }
        } catch(IOException e) {

        }
        /*
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the searchResponse.
                System.out.println("Searched Yelp!");
                YelpData.this.businesses = searchResponse.getBusinesses();
                place.setBusinesses(searchResponse.getBusinesses());
                for(int i =0; i < businesses.size(); i++) {
                    System.out.println(businesses.get(i).getName());
                    System.out.println(businesses.get(i).getDistance());
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
            }
        };

        call.enqueue(callback);
        */
    }
}
