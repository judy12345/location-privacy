package com.jnu.lbsprivacy.hooks;

import java.util.Locale;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetNetworkOperationHook extends BaseHook {

    public TMgetNetworkOperationHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getNetworkOperator", this);
        XposedBridge.log("[TM:gno] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gno]@ " + this.pkgname + " " + methodHookParam.getResult());
        XposedBridge.log("[TM:gno] " + this.pkgname + " {status(gps)=on}");
        String res = String.format(Locale.US, "%03d", 460);
        res += String.format(Locale.US, "%02d", 0);
        methodHookParam.setResult(res);
    }

    @Override
    public String tag() {
        return "[TM:gno]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
