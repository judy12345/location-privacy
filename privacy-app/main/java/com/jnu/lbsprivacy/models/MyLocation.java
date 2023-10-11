package com.jnu.lbsprivacy.models;

import com.baidu.mapapi.model.LatLng;

public class MyLocation {
    public float bearing;
    public float speed;
    public LatLng latLng;

    public MyLocation(double latitude, double longitude, float bearing, float speed) {
        latLng = new LatLng(latitude, longitude);
        this.bearing = bearing;
        this.speed = speed;
    }

    public MyLocation(LatLng latLng) {
        this.latLng = latLng;
        this.bearing = 0.0f;
        this.speed = 0.0f;
    }

    public String toString() {
       return String.format("MyLocation: {lat=%.6f, lon=%.6f, b=%.2f, s=%.2f}", latLng.latitude, latLng.longitude, bearing, speed);
    }
}