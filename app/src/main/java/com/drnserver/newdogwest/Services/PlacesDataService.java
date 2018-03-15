package com.drnserver.newdogwest.Services;

import com.drnserver.newdogwest.Models.PlaceProperties;

import java.util.ArrayList;

/**
 * Created by DrN on 3/7/2018.
 */

public class PlacesDataService {
    //New west data parsing
    public static ArrayList<PlaceProperties> PlaceDataList;
    public static String selected;

    public static void sortParkData() {
        ArrayList<PlaceProperties> temp = new ArrayList<PlaceProperties>();
        while(PlaceDataList.size() > 0) {
            int minIndex = 0;
            for(int i = 0; i< PlaceDataList.size(); i++) {
                PlaceProperties current = PlaceDataList.get(i);
                if(current.getDistance() < PlaceDataList.get(minIndex).getDistance()) {
                    minIndex = i;
                }
            }
            temp.add(PlaceDataList.get(minIndex));
            PlaceDataList.remove(minIndex);
        }
        for(int i=0; i< temp.size(); i++) {
            PlaceDataList.add(temp.get(i));
        }
    }
}

