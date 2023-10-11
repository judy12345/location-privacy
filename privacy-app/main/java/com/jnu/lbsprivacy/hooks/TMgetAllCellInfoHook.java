package com.jnu.lbsprivacy.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetAllCellInfoHook extends BaseHook{

    public TMgetAllCellInfoHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getAllCellInfo", this);
        XposedBridge.log("[TM:gaci] installed.");

    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gaci]@ " + this.pkgname + " " + methodHookParam.getResult());
        XposedBridge.log("[TM:gaci] " + this.pkgname + " {gps=on}");
        methodHookParam.setResult(null);
    }

    @Override
    public String tag() {
        return "[TM:gacl]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
