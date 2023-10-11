package com.jnu.lbsprivacy.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetNeighboringCellInfoHook extends BaseHook{

    public TMgetNeighboringCellInfoHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getNeighboringCellInfo", this);
        XposedBridge.log("[TM:gnci] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gnci]@ " + this.pkgname + " " + methodHookParam.getResult());
        XposedBridge.log("[TM:gnci] " + this.pkgname +" {status(gps)=on}");
        methodHookParam.setResult(null);
    }

    @Override
    public String tag() {
        return "[TM:gnci]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
