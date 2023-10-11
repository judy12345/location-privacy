package com.jnu.lbsprivacy.hooks;

import android.location.LocationListener;
import android.location.LocationManager;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.jnu.lbsprivacy.utils.LocationHookUtils.removeLocationListener;

public class LMremoveUpdate extends BaseHook {

    public LMremoveUpdate(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "removeUpdates", LocationListener.class, this);
        XposedBridge.log("[LM:ru] installed");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {

    }

    @Override
    public String tag() {
        return null;
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log(String.format("[LM:ru] %s {remove proxy}", pkgname));
        removeLocationListener(methodHookParam, 0);
    }
}
