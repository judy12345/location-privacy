package com.jnu.lbsprivacy;

import android.util.Log;

import com.jnu.lbsprivacy.hooks.LMgetBestProviderHook;
import com.jnu.lbsprivacy.hooks.LMgetLastKnownLocationHook;
import com.jnu.lbsprivacy.hooks.LMgetLastLocationHook;
import com.jnu.lbsprivacy.hooks.LMgetProvidersHook;
import com.jnu.lbsprivacy.hooks.LMisProviderEnableHook;
import com.jnu.lbsprivacy.hooks.LMremoveUpdate;
import com.jnu.lbsprivacy.hooks.LMrequestLocationUpdateHook;
import com.jnu.lbsprivacy.hooks.TMgetAllCellInfoHook;
import com.jnu.lbsprivacy.hooks.TMgetCellLocationHook;
import com.jnu.lbsprivacy.hooks.TMgetNeighboringCellInfoHook;
import com.jnu.lbsprivacy.hooks.TMgetNetworkOperationHook;
import com.jnu.lbsprivacy.hooks.TMgetPhoneTypeHook;
import com.jnu.lbsprivacy.hooks.TMgetSimOperatorHook;
import com.jnu.lbsprivacy.hooks.WMgetBSSIDHook;
import com.jnu.lbsprivacy.hooks.WMgetScanResultHook;

import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XposedMod implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final String TAG = XposedMod.class.getName();
    public static final String prefilename = "FakeLocationPref";
    private List<String> pkgNameList;


    private void installWifiHooks(XC_LoadPackage.LoadPackageParam loadPackageParam, String pkgName) {
        new WMgetBSSIDHook(loadPackageParam.classLoader, pkgName).install();
        new WMgetScanResultHook(loadPackageParam.classLoader, pkgName).install();

    }

    private void installCellHooks(XC_LoadPackage.LoadPackageParam loadPackageParam, String pkgName) {
        new TMgetPhoneTypeHook(loadPackageParam.classLoader, pkgName).install();
        new TMgetCellLocationHook(loadPackageParam.classLoader, pkgName).install();
        new TMgetNeighboringCellInfoHook(loadPackageParam.classLoader, pkgName).install();
        new TMgetAllCellInfoHook(loadPackageParam.classLoader, pkgName).install();
        new TMgetNetworkOperationHook(loadPackageParam.classLoader, pkgName).install();
        new TMgetSimOperatorHook(loadPackageParam.classLoader, pkgName).install();
    }

    private void installLocationHooks(XC_LoadPackage.LoadPackageParam loadPackageParam, String pkgName) {
        new LMisProviderEnableHook(loadPackageParam.classLoader, pkgName).install();
        new LMgetProvidersHook(loadPackageParam.classLoader, pkgName).install();
        new LMgetBestProviderHook(loadPackageParam.classLoader, pkgName).install();
        new LMrequestLocationUpdateHook(loadPackageParam.classLoader, pkgName).install();
        new LMremoveUpdate(loadPackageParam.classLoader, pkgName).install();
        new LMgetLastLocationHook(loadPackageParam.classLoader, pkgName).install();
        new LMgetLastKnownLocationHook(loadPackageParam.classLoader, pkgName).install();

    }

    private void doAllHooks(XC_LoadPackage.LoadPackageParam loadPackageParam, String pkgName) {
        installWifiHooks(loadPackageParam, pkgName);
        installCellHooks(loadPackageParam, pkgName);
        installLocationHooks(loadPackageParam, pkgName);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        pkgNameList = List.of("com.dianping.v1", "me.ele", "com.autonavi.minimap", "com.sankuai.meituan");

        if (pkgNameList.contains(loadPackageParam.packageName)) {
            Log.d(TAG, "Find and Hook your pkg!!");
            doAllHooks(loadPackageParam, loadPackageParam.packageName);
        }
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

}