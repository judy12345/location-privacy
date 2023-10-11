package com.jnu.lbsprivacy.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetCellLocationHook extends BaseHook{

    public TMgetCellLocationHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }


    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getCellLocation", this);
        XposedBridge.log("[TM:gcl] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gcl]@ " + this.pkgname + " " + methodHookParam.getResult());
        XposedBridge.log("[TM:gcl] " + this.pkgname + " {status(gps)=on}");
        methodHookParam.setResult(null);
    }

    @Override
    public String tag() {
        return "TM:gcl";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
