package com.jnu.lbsprivacy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.jnu.lbsprivacy.algorithm.AnonymizationMethod;
import com.jnu.lbsprivacy.algorithm.ExampleMethod;
import com.jnu.lbsprivacy.models.MyLocation;
import com.jnu.lbsprivacy.algorithm.TraceMethod;

import com.jnu.lbsprivacy.algorithm.GenerationBased;
import com.jnu.lbsprivacy.algorithm.TraceRebuiltMethod;
import com.jnu.lbsprivacy.models.MyLocation;

import com.jnu.lbsprivacy.ui.map.MapFragment;
import com.jnu.lbsprivacy.utils.MultiprocessSharedPreferences;
import com.jnu.lbsprivacy.utils.Path;
import com.tbruyelle.rxpermissions3.RxPermissions;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private Path examplePath;
    private Path savedPath;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //使得网络连接可以放在主线程上
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_map)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        getPermissions();

    }

    @Override
    public void onStart() {
        super.onStart();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        MapFragment mapFragment = (MapFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        loadExamplePath();

        // 读取多个Gpx文件
        ArrayList<Path> mPath08 = HandleMultiGpxFile("trajectory08");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_algorithm1:
                        // 2017年算法
                        TraceRebuiltMethod TraceRebuilt = new TraceRebuiltMethod(examplePath);
                        Path rebuiltPath = TraceRebuilt.getResult();
                        Toast.makeText(getApplicationContext(), "轨迹重建算法", Toast.LENGTH_SHORT).show();
                        mapFragment.clearOverlay();
                        mapFragment.drawPath(rebuiltPath);
                        break;

                    case R.id.nav_algorithm2:
                        // 2008年算法
                        Toast.makeText(getApplicationContext(), "多路径匿名化重建算法", Toast.LENGTH_SHORT).show();
                        GenerationBased generationBased = new GenerationBased(mPath08);
                        ArrayList<Path> paths = generationBased.getResult();
                        // 显示所有的路径
                        mapFragment.clearOverlay();
                        for(Path path : paths){
                            if (path.getWayPoints().size() >= 2) {
                                mapFragment.drawPath(path);
                            }else{
                                Log.d("error","not enough points");
                            }
                        }
                        break;

                    case R.id.nav_algorithm2_1:
                        // 2008年算法只匿名
                        Toast.makeText(getApplicationContext(), "多路径匿名化算法", Toast.LENGTH_SHORT).show();
                        GenerationBased generationBased2 = new GenerationBased(mPath08);
                        ArrayList<Path> paths2 = generationBased2.getResultNotReconstruct();
                        mapFragment.clearOverlay();
                        for(Path path : paths2){
                            if (path.getWayPoints().size() >= 2) {
                                mapFragment.drawPath(path);
                            }else{
                                Log.d("error","not enough points");
                            }
                        }
                        break;

                    case R.id.nav_algorithm3:
                        Toast.makeText(getApplicationContext(), "掩蔽数据交换算法", Toast.LENGTH_SHORT).show();
                        TraceMethod traceMethod = new TraceMethod(examplePath);
                        Path res3 = traceMethod.getResult();
                        mapFragment.clearOverlay();
                        mapFragment.drawPath(res3);
                        break;

                    case R.id.nav_showroute:
                        Toast.makeText(getApplicationContext(), "单路径示例展示", Toast.LENGTH_SHORT).show();
                        mapFragment.clearOverlay();
                        mapFragment.drawPath(examplePath);
                        break;

                    case R.id.nav_show_multi_route:
                        // 2008年算法原路径
                        Toast.makeText(getApplicationContext(), "多路径示例展示", Toast.LENGTH_SHORT).show();
                        GenerationBased generationBasedOriginal = new GenerationBased(mPath08);
                        paths = generationBasedOriginal.getResultOriginal();
                        mapFragment.clearOverlay();
                        for(Path path : paths){
                            if (path.getWayPoints().size() >= 2) {
                                mapFragment.drawPath(path);
                            }
                        }
                        break;

                    case R.id.nav_clearroute:
                        Toast.makeText(getApplicationContext(), "路径清除", Toast.LENGTH_SHORT).show();
                        mapFragment.clearOverlay();
                        break;

                    case R.id.nav_show_recordroute:
                        if (savedPath != null) {
                            mapFragment.clearOverlay();
                            mapFragment.drawPath(savedPath);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Load recorded route first", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.fake_location:
                        mapFragment.setInjectionModeOn();
                        break;

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_load_gpx:
                Intent i = new Intent(getBaseContext(), LoadGPXActivity.class);
                startActivityForResult(i, LoadGPXActivity.GET_GPX_FILENAME_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        if (requestCode==LoadGPXActivity.GET_GPX_FILENAME_REQUEST && resultCode == LoadGPXActivity.GET_GPX_FILENAME_RESULT) {
            String gpxFilename = bundle.getString(LoadGPXActivity.GPX_FILENAME_PARAM);
            Log.d("onActivityResult", "here: " + gpxFilename);
            loadPathFromGPX(gpxFilename);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void getPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(
                granted -> {
                    if (granted) {
                        Toast.makeText(getApplicationContext(), "Permissions Success", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Permissions Failed", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void loadExamplePath() {
        examplePath = new Path();
        try {
            InputStream in = getAssets().open("test1.gpx");
            examplePath.loadFromGPX(in);
        }
        catch (IOException e) {
            Log.d("loadExamplePath", e.toString());
        }
    }

    private void loadPathFromGPX(String gpxFilename) {
       savedPath = new Path();
       try {
           InputStream in = openFileInput(gpxFilename);
           savedPath.loadFromGPX(in);
       } catch (IOException e) {
           Log.d("loadPathFromGPX", e.toString());
       }
    }

    private boolean isAppInstalled(String packageName) {
        try {
            getApplicationContext().getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 读取多个gpx文件
    private ArrayList<Path> HandleMultiGpxFile(String packageName) {
        AssetManager assetManager = getAssets();
        String[] files = null;
        // 读取文件夹下的文件名
        try {
            files = assetManager.list(packageName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert files != null;
        ArrayList<Path> trajectorySet = new ArrayList<>();
        try {
            for(String file:files) {
                Path trajectory = new Path();
                InputStream input = getAssets().open(packageName +"/" + file);
                trajectory.loadFromGPX(input);
                trajectorySet.add(trajectory);
            }
        } catch (IOException e) {
            Log.d("LoadError","Something went wrong in class: HandleMultiGpxFile");
        }
        return trajectorySet;
    }
    public void injectLocation(MyLocation myLocation) {
        SharedPreferences sharedPref = MultiprocessSharedPreferences.getSharedPreferences(getApplicationContext(), BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("latitude", Double.doubleToLongBits(myLocation.latLng.latitude));
        editor.putLong("longitude", Double.doubleToLongBits(myLocation.latLng.longitude));
        editor.putFloat("bearing", myLocation.bearing);
        editor.putFloat("float", myLocation.speed);
        editor.apply();
    }


}