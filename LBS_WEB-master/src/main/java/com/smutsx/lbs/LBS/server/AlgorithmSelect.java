package com.smutsx.lbs.LBS.server;

import com.smutsx.lbs.LBS.server.Algorithm.AnonymizationMethod;
import com.smutsx.lbs.LBS.server.Algorithm.TraceMethod;
import com.smutsx.lbs.LBS.server.Algorithm.TraceRebuiltMethod;
import com.smutsx.lbs.LBS.server.utils.Path;
import io.jenetics.jpx.WayPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
选择算法
 */
public class AlgorithmSelect {

    public Path select(int level, int k, Path examplePath) {
        TraceMethod trace = new TraceMethod(examplePath, k);
        Path res = trace.getResult();
//        TraceRebuiltMethod TraceRebuilt = new TraceRebuiltMethod(examplePath);
//        Path res = TraceRebuilt.getResult();
//        AnonymizationMethod anonymizationMethod = new AnonymizationMethod(examplePath);
//        Path res = anonymizationMethod.getResult();
        return res;
    }

    public Path Creatwaypoint(String waypoints) {
        JSONArray reqArray;
        Path path;
        try{
            reqArray = new JSONArray(waypoints);
            ArrayList<WayPoint> temp_list =new ArrayList<WayPoint>();
            for(int i = 0;i<reqArray.length();i++)
            {
                WayPoint temp_one;
                JSONObject temp = reqArray.getJSONObject(i);
                temp_one = WayPoint.builder().lon(Double.parseDouble(temp.get("longitude").toString())).lat(Double.parseDouble(temp.get("latitude").toString())).build();
                temp_list.add(temp_one);
            }
            path = new Path(temp_list);

        }catch (Exception e){
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
        return path;
    }
}
