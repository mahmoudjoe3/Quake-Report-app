package com.example.android.quakereport.Model;

public class item {
    String location,date,time,url;
    float mag;

    public item(float mag, String location, String date, String time,String url) {
        this.mag = mag;
        this.location = location;
        this.date = date;
        this.time = time;
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public float getMag() {
        return mag;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
