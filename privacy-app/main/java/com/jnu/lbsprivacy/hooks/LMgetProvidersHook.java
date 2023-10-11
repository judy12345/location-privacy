package com.jnu.lbsprivacy.hooks;

import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class LMgetProvidersHook extends BaseHook{

    public LMgetProvidersHook(ClassLoader classLoader2, String str) {
        super(classLoader2, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getProviders", Boolean.TYPE, this);
        XposedBridge.log("[LM:gp] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[LM:gp]@ " + this.pkgname + " " + methodHookParam.getResult());
        Object res, orig;
        orig = methodHookParam.getResult();
        if (orig == null) {
            res = new ArrayList();
        } else  {
            res = (List) orig;
        }

        if (!((List) res).contains("gps")) {
            XposedBridge.log(String.format(tag() + " {add GPS_PROVIDER to the list}", this.pkgname));
            ((List) res).add("gps");
        }

        methodHookParam.setResult(res);
    }

    @Override
    public String tag() {
        return "LM:gp";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
