package com.jnu.lbsprivacy.hooks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import com.jnu.lbsprivacy.models.MyLocation;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.jnu.lbsprivacy.utils.LocationHookUtils.addLocationListener;
import static com.jnu.lbsprivacy.utils.LocationHookUtils.getLocation;
import static com.jnu.lbsprivacy.utils.LocationHookUtils.getMyLocation;
import static com.jnu.lbsprivacy.utils.LocationHookUtils.getPrefs;

public class LMrequestLocationUpdateHook extends BaseHook{

    public LMrequestLocationUpdateHook(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        Class locationRequestClass = XposedHelpers.findClass("android.location.LocationRequest", this.classLoader);
        XposedHelpers.findAndHookMethod(LocationManager.class, "requestLocationUpdates", locationRequestClass, LocationListener.class, Looper.class, PendingIntent.class, this);
        XposedBridge.log("[LM:rlu] installed");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        MyLocation myLocation = getMyLocation(getPrefs());
        Location location = getLocation(myLocation, true);
        LocationListener listener = (LocationListener) methodHookParam.args[1];
        PendingIntent intent = (PendingIntent) methodHookParam.args[3];
        if (listener != null) {
            XposedBridge.log(String.format("[LM:rlu] %s {listener, la=%.6f, lo=%.6f, b=%.2f, s=%.2f}", pkgname, myLocation.latLng.latitude, myLocation.latLng.longitude, myLocation.bearing, myLocation.speed));
            XposedHelpers.callMethod(listener, "onLocationChanged", location);
        } else if (intent != null) {
            Intent newIntent = new Intent();
            newIntent.putExtra("location", new Location(location));
            Context context = (Context) XposedHelpers.getObjectField(methodHookParam.thisObject, "mContext");
            if (context != null) {
                XposedBridge.log(String.format("[LM:rlu] %s {intent, la=%.6f, lo=%.6f, b=%.2f, s=%.2f}", pkgname, myLocation.latLng.latitude, myLocation.latLng.longitude, myLocation.bearing, myLocation.speed));
                try {
                    intent.send(context, 0, newIntent);
                } catch (Exception e) {
                    XposedBridge.log(e.toString());
                }
            }

        }

    }

    @Override
    public String tag() {
        return "[LM:rlu]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {
        if (methodHookParam.args[1] != null) {
            XposedBridge.log("[LM:rlu] Before");
            addLocationListener(methodHookParam, 1, getPrefs(), pkgname, tag());
        }

    }
}
