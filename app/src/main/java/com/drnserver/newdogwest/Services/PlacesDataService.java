package com.drnserver.newdogwest.Services;

import com.drnserver.newdogwest.Models.PlaceProperties;

import java.util.ArrayList;

/**
 * Created by DrN on 3/7/2018.
 */

public class PlacesDataService {
    //New west data parsing
    public static ArrayList<PlaceProperties> parkDataList;

    public static void sortParkData() {
        ArrayList<PlaceProperties> temp = new ArrayList<PlaceProperties>();
        while(parkDataList.size() > 0) {
            int minIndex = 0;
            for(int i = 0; i< parkDataList.size(); i++) {
                PlaceProperties current = parkDataList.get(i);
                if(current.getDistance() < parkDataList.get(minIndex).getDistance()) {
                    minIndex = i;
                }
            }
            temp.add(parkDataList.get(minIndex));
            parkDataList.remove(minIndex);
        }
        for(int i=0; i< temp.size(); i++) {
            parkDataList.add(temp.get(i));
        }
    }
}

