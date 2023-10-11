package com.jnu.lbsprivacy.hooks;

import java.util.Locale;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetSimOperatorHook extends BaseHook {

    public TMgetSimOperatorHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getSimOperator", this);
        XposedBridge.log("[TM:gso] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gso]@ " + this.pkgname + " " + methodHookParam.getResult());
        String res = String.format(Locale.US, "%03d", 460);
        res += String.format(Locale.US, "%02d", 0);
        XposedBridge.log("[TM:gso] " + this.pkgname + " MCC+MNC = " + res);
        methodHookParam.setResult(res);

    }

    @Override
    public String tag() {
        return "[TM:gso]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }

}
