package com.jnu.lbsprivacy.hooks;

import android.content.SharedPreferences;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;

public abstract class BaseHook extends XC_MethodHook {
    protected String pkgname;
    protected ClassLoader classLoader;

    public BaseHook(int i, ClassLoader classLoader2, String str) {
        super(i);
        this.classLoader = classLoader2;
        this.pkgname = str;
    }

    public BaseHook(ClassLoader classLoader2, String str) {
        this.classLoader = classLoader2;
        this.pkgname = str;
    }

    public abstract void install();

    public abstract void myAfterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam);

    public void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
        myAfterHookedMethod(methodHookParam);
    }

    public abstract String tag();

    public abstract void myBeforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam);

    public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
        myBeforeHookedMethod(methodHookParam);
    }


}
