package com.drnserver.newdogwest;

/**
 * Created by DrN on 2/14/2018.
 */

public class Parks {
    private String title;
    private String location;
    private String distance;
    private String image;

    public Parks() {
    }

    public Parks(String title, String genre, String distance) {
        this.title = title;
        this.location = genre;
        this.distance = distance;
    }

    public Parks(String title, String genre, String year, String image) {
        this.title = title;
        this.location = genre;
        this.distance = year;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLocation() {
        return location;
    }

    public void setGenre(String location) {
        this.location = location;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
