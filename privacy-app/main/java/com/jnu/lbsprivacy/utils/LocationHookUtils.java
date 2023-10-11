package com.jnu.lbsprivacy.utils;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.SystemClock;

import com.baidu.mapapi.model.LatLng;
import com.jnu.lbsprivacy.BuildConfig;
import com.jnu.lbsprivacy.models.MyLocation;
import com.jnu.lbsprivacy.models.MyLocationListener;

import java.util.Map;
import java.util.WeakHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class LocationHookUtils {

    public static final Map<Object, Object> locationListenerMap = new WeakHashMap();

    public static SharedPreferences getPrefs()  {
        Context context = (Context) AndroidAppHelper.currentApplication();
        MultiprocessSharedPreferences.setAuthority("com.jnu.lbsprivacy.provider");
        return MultiprocessSharedPreferences.getSharedPreferences(context, BuildConfig.APPLICATION_ID, context.MODE_PRIVATE);
    }

    public static void addLocationListener(XC_MethodHook.MethodHookParam methodHookParam, int idx, SharedPreferences sharedPreferences, String pkgname, String tag) {
        Object listener = methodHookParam.args[idx];
        synchronized (locationListenerMap) {
            if (locationListenerMap.containsKey(listener)) {
                XposedBridge.log(tag + String.format(" %s {reuse proxy}", pkgname));
            } else if (locationListenerMap.containsValue(listener)) {
                XposedBridge.log(tag + String.format(" %s {already proxy}", pkgname));
            } else {
                XposedBridge.log(tag + String.format(" %s {creating proxy}", pkgname));
                MyLocationListener mylistener = new MyLocationListener(sharedPreferences, (LocationListener) methodHookParam.args[idx], pkgname, Binder.getCallingUid());
                synchronized (locationListenerMap) {
                    locationListenerMap.put(listener, mylistener);
                }
                methodHookParam.args[idx] = mylistener;
            }
        }
    }

    public static void removeLocationListener(XC_MethodHook.MethodHookParam methodHookParam, int idx) {
        if (methodHookParam.args.length > idx && methodHookParam.args[idx] != null) {
            Object listener = methodHookParam.args[idx];
            synchronized (listener) {
                if (locationListenerMap.containsKey(listener)) {
                    methodHookParam.args[idx] = locationListenerMap.remove(listener);
                }
            }
        }
    }

    public static Location getLocation(MyLocation myLocation, Boolean isConvert) {
        Location location = new Location("gps");
        if (isConvert) {
            myLocation.latLng = LocationConverter.bd09ToWgs84(myLocation.latLng);
        }
        location.setLatitude(myLocation.latLng.latitude);
        location.setLongitude(myLocation.latLng.longitude);
        location.setTime(System.currentTimeMillis());
        location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        location.setAccuracy(100.0f);
        location.setBearing(myLocation.bearing);
        location.setSpeed(myLocation.speed);
        return location;
    }

    public static MyLocation getMyLocation(SharedPreferences SharedPreferences) {
        double latitude = Double.longBitsToDouble(SharedPreferences.getLong("latitude", Double.doubleToLongBits(0.0d)));
        double longitude = Double.longBitsToDouble(SharedPreferences.getLong("longitude", Double.doubleToLongBits(0.0d)));
        float bearing = SharedPreferences.getFloat("bearing", 0.0f);
        float speed = SharedPreferences.getFloat("speed", 0.0f);

        return new MyLocation(latitude, longitude, bearing, speed);
    }

    public static void setLocationResult(XC_MethodHook.MethodHookParam methodHookParam, SharedPreferences sharedPreferences, String tag, String pkgname) {
        Location orig = (Location) methodHookParam.getResult();
        XposedBridge.log(String.format(tag + "@ %s {on, la=%.6f, lo=%.6f, b=%.2f, s=%.2f}", pkgname, orig.getLatitude(), orig.getLongitude(), orig.getBearing(), orig.getSpeed()));
        MyLocation myLocation = getMyLocation(sharedPreferences);
        Location location = getLocation(myLocation, true);
        XposedBridge.log(String.format(tag + " %s {on, la=%.6f, lo=%.6f, b=%.2f, s=%.2f}", pkgname, myLocation.latLng.longitude, myLocation.latLng.latitude, myLocation.latLng.longitude, myLocation.bearing, myLocation.speed));
        methodHookParam.setResult(location);
    }
}