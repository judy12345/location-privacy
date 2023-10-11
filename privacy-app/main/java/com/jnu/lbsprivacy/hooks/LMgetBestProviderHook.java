package com.jnu.lbsprivacy.hooks;

import android.location.Criteria;
import android.location.LocationManager;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LMgetBestProviderHook extends BaseHook{
    public LMgetBestProviderHook(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, this);
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log(String.format(tag() + "%s {return GPS_PROVIDER directly}", this.pkgname));
        methodHookParam.setResult("gps");
    }

    @Override
    public String tag() {
        return "LM:gbp";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
