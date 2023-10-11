package com.jnu.lbsprivacy.hooks;

import android.location.LocationManager;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static com.jnu.lbsprivacy.utils.LocationHookUtils.getPrefs;
import static com.jnu.lbsprivacy.utils.LocationHookUtils.setLocationResult;

public class LMgetLastKnownLocationHook extends BaseHook {

    public LMgetLastKnownLocationHook(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, this);
        XposedBridge.log("[LM:glkl] installed");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        setLocationResult(methodHookParam, getPrefs(), pkgname, tag());
    }

    @Override
    public String tag() {
        return "[LM:glkl]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
