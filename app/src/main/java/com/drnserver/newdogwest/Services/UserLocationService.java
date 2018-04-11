package com.drnserver.newdogwest.Services;

/**
 * Created by DrN on 4/10/2018.
 */


public class UserLocationService {
    private static double currentLat;
    private static double currentLon;
    private static String displayAddress;

    public UserLocationService() {

    }
    public static double getCurrentLat() {
        return currentLat;
    }

    public static void setCurrentLat(double currentLat) {
        UserLocationService.currentLat = currentLat;
    }

    public static double getCurrentLon() {
        return currentLon;
    }

    public static void setCurrentLon(double currentLon) {
        UserLocationService.currentLon = currentLon;
    }

    public static String getDisplayAddress() {
        return displayAddress;
    }

    public static void setDisplayAddress(String displayAddress) {
        UserLocationService.displayAddress = displayAddress;
    }
}
