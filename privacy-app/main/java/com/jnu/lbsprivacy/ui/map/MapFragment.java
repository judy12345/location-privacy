package com.jnu.lbsprivacy.ui.map;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.jnu.lbsprivacy.BuildConfig;
import com.jnu.lbsprivacy.R;
import com.jnu.lbsprivacy.models.MyLocation;
import com.jnu.lbsprivacy.utils.MultiprocessSharedPreferences;
import com.jnu.lbsprivacy.utils.Path;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import io.jenetics.jpx.WayPoint;

public class MapFragment extends Fragment {

    private MapViewModel mViewModel;
    private View mView;
    private TextureMapView mTextureMapView = null;
    private LocationClient mLocationClient = null;
    private LocationClient mRecordLocationClient = null;
    private BaiduMap mBaiduMap = null;
    private ImageView mUpdateeLocationBtn;
    private ToggleButton mRecordBtn;
    private MyLocationListener myLocationListener = new MyLocationListener();
    private RecordLocationListener recordLocationListener = new RecordLocationListener();
    private Path mRecordPath = null;
    private double lat;
    private double lon;
    private boolean injectionMode;
    private LatLng injectLocation;
    private boolean isFirstLocation = true;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_fragment, container, false);
        mTextureMapView = mView.findViewById(R.id.bmapView);
        mUpdateeLocationBtn = mView.findViewById(R.id.updateLocationBtn);
        mRecordBtn = mView.findViewById(R.id.recordBtn);
        mBaiduMap = mTextureMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        injectionMode = false;
        initMap();
        initRecordLocationClient();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MapFragment", "onActivityCreated");
        mUpdateeLocationBtn.setOnClickListener(v -> {
            mLocationClient.requestLocation();
            LatLng ll = new LatLng(lat, lon);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
            MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(17f);
            mBaiduMap.animateMapStatus(u1);
            }
        );
        mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("MapFragment", "Start Recording");
                    mRecordPath = new Path();
                    mRecordLocationClient.start();
                } else{
                    Log.d("MapFragment", "Stop Recording");
                    mRecordLocationClient.stop();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                    String filename = ZonedDateTime.now().format(formatter) + ".gpx";
                    try {
                        FileOutputStream outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                        Toast.makeText(getContext(), "New route save to: " + filename, Toast.LENGTH_SHORT).show();
                        mRecordPath.saveGPX(outputStream);
                    }
                    catch (IOException e) {
                        Log.d("MapFragment", e.toString());
                    }

                    Log.d("MapFragment", mRecordPath.toString());
                }
            }
        });

        mViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MapFragment", "onResume");
        isFirstLocation = true;
        mTextureMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MapFragment", "onPause");
        mTextureMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MapFragment", "onDestroy");
        mLocationClient.unRegisterLocationListener(myLocationListener);
        mLocationClient.stop();
        mRecordLocationClient.unRegisterLocationListener(recordLocationListener);
        mRecordLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mTextureMapView.onDestroy();
        mTextureMapView = null;
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mTextureMapView == null) {
                return;
            }
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d("latlon", "" + lat + "," + lon);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            if (isFirstLocation) {
                isFirstLocation = false;
                mBaiduMap.setMyLocationData(locData);
                MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(17f);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    public class RecordLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mTextureMapView == null) {
                return;
            }
            lat = location.getLatitude();
            lon = location.getLongitude();
            Log.d("latlon", "" + lat + "," + lon);

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mRecordPath.addWayPoint(WayPoint.builder().lat(lat).lon(lon).time(ZonedDateTime.now()).build());
            Log.d("RecordLocation", locData.latitude + ", " + locData.longitude);
        }
    }

    public class MyOnClickListener implements BaiduMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng point) {
           Log.d("OnClick", point.toString());
           if (injectionMode) {
               injectLocation = point;
               addMarker(point);
               injectLocation(new MyLocation(point));
               setInjectionModeOff();
           }
        }

        @Override
        public void onMapPoiClick(MapPoi mapPoi) {
           Log.d("OnClick", mapPoi.toString());
        }
    }

   private void initMap() {
       mLocationClient = new LocationClient(getActivity().getApplicationContext());
       LocationClientOption option = new LocationClientOption();
       option.setOpenGps(true);
       option.setCoorType("bd09ll");
       option.setScanSpan(1000);
       option.setEnableSimulateGps(true);
       option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

       mLocationClient.setLocOption(option);
       mLocationClient.registerLocationListener(myLocationListener);
       mLocationClient.start();

       MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, (BitmapDescriptor) null);
       myLocationConfiguration.accuracyCircleFillColor = 0x00000000;
       myLocationConfiguration.accuracyCircleStrokeColor = 0x00000000;
       mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);

       MyOnClickListener myOnClickListener = new MyOnClickListener();
       mBaiduMap.setOnMapClickListener(myOnClickListener);
    }

    private void initRecordLocationClient() {
        mRecordLocationClient = new LocationClient(getActivity().getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("gcj02");
        option.setScanSpan(10000); // update every 10s
        option.setEnableSimulateGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        mRecordLocationClient.setLocOption(option);
        mRecordLocationClient.registerLocationListener(recordLocationListener);
    }

    public void addMarker(LatLng point) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.placeholder);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        mBaiduMap.addOverlay(option);
    }

    public void setInjectionModeOn() {
        Log.d("MapFragment", "Injection mode on");
        Toast.makeText(getActivity(), "请选择注入点", Toast.LENGTH_SHORT).show();
        clearOverlay();
        injectionMode = true;
    }

    public void setInjectionModeOff() {
        Log.d("MapFragment", "Injection mode off");
        injectionMode = false;
    }

    public LatLng getInjectionPoint() {
        return injectLocation;
    }

    public void drawPath(Path path) {
        OverlayOptions polylineOption = new PolylineOptions().points(path.getLatLngList())
                .width(10).color(Color.RED);
        mBaiduMap.addOverlay(polylineOption);
    }

    public void clearOverlay() {
        mBaiduMap.clear();
    }

    public LatLng getCurrentLocation() {
        return new LatLng(lat, lon);
    }

    public Path getmRecordPath() {
        return mRecordPath;
    }

    public void injectLocation(MyLocation myLocation) {
        SharedPreferences sharedPref = MultiprocessSharedPreferences.getSharedPreferences(getActivity(), BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putLong("latitude", Double.doubleToLongBits(myLocation.latLng.latitude));
        editor.putLong("longitude", Double.doubleToLongBits(myLocation.latLng.longitude));
        editor.putFloat("bearing", myLocation.bearing);
        editor.putFloat("float", myLocation.speed);
        editor.apply();

        Toast.makeText(getActivity(), String.format("注入位置：（%.5f, %.5f）", myLocation.latLng.latitude, myLocation.latLng.longitude), Toast.LENGTH_LONG).show();
    }


}