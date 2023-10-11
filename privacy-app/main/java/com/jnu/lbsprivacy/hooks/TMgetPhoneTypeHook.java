package com.jnu.lbsprivacy.hooks;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class TMgetPhoneTypeHook  extends BaseHook {

    public TMgetPhoneTypeHook(ClassLoader classLoader, String str) {
        super(classLoader, str);
    }

    @Override
    public void install() {
        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", this.classLoader, "getPhoneType", this);
        XposedBridge.log("[TM:gpt] installed.");
    }

    @Override
    public void myAfterHookedMethod(MethodHookParam methodHookParam) {
        XposedBridge.log("[TM:gpt]@ " + this.pkgname + " " + methodHookParam.getResult());
        XposedBridge.log("[TM:gpt] " + this.pkgname + "{cell(1), phone(1)}");
        methodHookParam.setResult(1);
    }

    @Override
    public String tag() {
        return "[TM:gpt]";
    }

    @Override
    public void myBeforeHookedMethod(MethodHookParam methodHookParam) {

    }
}
