package com.jnu.lbsprivacy.hooks;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WMgetBSSIDHook extends BaseHook {

    public WMgetBSSIDHook(ClassLoader classLoader, String str) {
       super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo", this.classLoader, "getBSSID", this);
        XposedBridge.log("[WM:gbss] installed.");
    }

    @Override
    public void myBeforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {}

    @Override
    public void myAfterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
        XposedBridge.log("[WM:gbss] " + this.pkgname + " {on, BssId}");
        methodHookParam.setResult("00:00:00:00:00:00");
    }

    @Override
    public String tag() {
        return "[WM:gbss]";
    }

}
