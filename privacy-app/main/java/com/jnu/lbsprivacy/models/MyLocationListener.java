package com.jnu.lbsprivacy.models;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;

import de.robv.android.xposed.XposedBridge;

import static com.jnu.lbsprivacy.utils.LocationHookUtils.getLocation;
import static com.jnu.lbsprivacy.utils.LocationHookUtils.getMyLocation;

public class MyLocationListener implements LocationListener {

    private int uid;
    private LocationListener mListener;
    private String provider;
    private SharedPreferences sharedPreferences;

    public MyLocationListener(SharedPreferences sharedPreferences, LocationListener locationListener, String provider, int uid) {
       this.uid = uid;
       this.mListener = locationListener;
       this.provider = provider;
       this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
       MyLocation myLocation = getMyLocation(sharedPreferences);
       Location newLocation = getLocation(myLocation, true);
       XposedBridge.log(String.format("[LM:XLL:olc] %s {gps, la=%.6f, lo=%.6f, b=%.2f, s=%.2f}", provider, myLocation.latLng.latitude, myLocation.latLng.longitude, myLocation.bearing, myLocation.speed));
       this.mListener.onLocationChanged(newLocation);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        this.mListener.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        this.mListener.onProviderDisabled(provider);
    }
}
