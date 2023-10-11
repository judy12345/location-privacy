package com.jnu.lbsprivacy.hooks;

import android.location.Location;
import android.location.LocationManager;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LMisProviderEnableHook extends BaseHook {

    public LMisProviderEnableHook(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "isProviderEnabled", new Object[]{String.class, this});
        XposedBridge.log("[LM:ipe] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[LM:ipe]@ " + this.pkgname + " " + methodHookParam.getResult());
        String orig = (String) methodHookParam.args[0];
        if ("gps".equals(orig)) {
            XposedBridge.log(String.format(tag() + " %s {p=%s, gps, %b}", this.pkgname, orig, true));
            methodHookParam.setResult(true);
        } else if ("network".equals(orig)) {
            XposedBridge.log(String.format(tag() + " %s {p=%s, gps, %b}", this.pkgname, orig, true));
            methodHookParam.setResult(true);
        }
    }

    @Override
    public String tag() {
        return "[LM:ipe]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
