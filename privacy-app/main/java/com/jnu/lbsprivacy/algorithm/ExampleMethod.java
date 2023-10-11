package com.jnu.lbsprivacy.algorithm;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorInfo;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.jnu.lbsprivacy.utils.Path;

import java.util.ArrayList;
import java.util.Collections;

import io.jenetics.jpx.WayPoint;

public class ExampleMethod {

    private Path path;
    public ExampleMethod(Path p) {
       path = p;
    }

    public Path getResult() {
        ArrayList<WayPoint> orig = (ArrayList<WayPoint>) path.getWayPoints().clone();
        Collections.shuffle(orig);
        return new Path(orig);
    }

    private class MYPOISearchResult implements OnGetPoiSearchResultListener {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Log.d("POIResult", "No Result");
                return;
            }
            for (PoiInfo poiInfo: poiResult.getAllPoi()) {
                Log.d("POIResult", poiInfo.name + ", " + poiInfo.location.toString());
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            Log.d("POIResult", poiDetailResult.toString());
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            Log.d("POIResult", poiDetailSearchResult.toString());

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    }

    public void testPOI(LatLng center) {
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new MYPOISearchResult());
        PoiNearbySearchOption options = new PoiNearbySearchOption().keyword("酒店$餐厅").location(center).radius(1000).pageNum(0);
        poiSearch.searchNearby(options);
    }
}
