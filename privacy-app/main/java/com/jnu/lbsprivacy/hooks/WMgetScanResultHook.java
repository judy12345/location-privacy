package com.jnu.lbsprivacy.hooks;

import java.util.ArrayList;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class WMgetScanResultHook extends BaseHook{

    public WMgetScanResultHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", this.classLoader, "getScanResults", this);
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[WM:gsr] " + this.pkgname + " {on, wifi}");
        methodHookParam.setResult(new ArrayList());
    }

    @Override
    public String tag() {
        return "[WM:gsr]";
    }
}
