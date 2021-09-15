package com.smutsx.lbs.LBS.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smutsx.lbs.LBS.entity.Send;
import com.smutsx.lbs.LBS.entity.SendMessage;
import com.smutsx.lbs.LBS.entity.Waypoints;
import com.smutsx.lbs.LBS.mapper.WaypointMapper;
import com.smutsx.lbs.LBS.server.Algorithm.AnonymizationMethod;
import com.smutsx.lbs.LBS.server.Algorithm.TraceMethod;
import com.smutsx.lbs.LBS.server.Algorithm.TraceRebuiltMethod;
import com.smutsx.lbs.LBS.server.utils.Path;
import com.smutsx.lbs.LBS.server.utils.WSClient;
import io.jenetics.jpx.WayPoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;

/*
选择算法
 */
public class AlgorithmSelect {
    @Autowired(required=false)
    private WaypointMapper waypointMapper;

    public Path select(int level, int k, Path examplePath) {
        Path res;
        TraceMethod trace = new TraceMethod(examplePath);
        res = trace.getResult();
//        if (level == 1) {
//            TraceMethod trace = new TraceMethod(examplePath);
//            res = trace.getResult();
//        } else if (level == 2) {
////            TraceRebuiltMethod TraceRebuilt = new TraceRebuiltMethod(examplePath);
////            res = TraceRebuilt.getResult();
//            AnonymizationMethod anonymizationMethod = new AnonymizationMethod(examplePath);
//            res = anonymizationMethod.getResult();
//        } else {
//            AnonymizationMethod anonymizationMethod = new AnonymizationMethod(examplePath);
//            res = anonymizationMethod.getResult();
//        }
        return res;
    }

    public Path Creatwaypoint(String waypoints) {
        JSONArray reqArray;
        Path path;
        waypoints = waypoints.replace('=',':');
        waypoints = waypoints.replace("lat","\"lat\"").replace("lon","\"lon\"").replace("time:","\"time\":\"").replace("}","\"}");
        try {
            reqArray = new JSONArray(waypoints);
            ArrayList<WayPoint> temp_list = new ArrayList<WayPoint>();
            for (int i = 0; i < reqArray.length(); i++) {
                WayPoint temp_one;
                JSONObject temp = reqArray.getJSONObject(i);
                Calendar c = Calendar.getInstance();
                c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(temp.get("time").toString().replace('T', ' ').replace('Z', ' ')));
//                System.out.println("时间转化后的毫秒数为：" + c.getTimeInMillis());
//                System.out.println(temp.get("time").toString().replace('T',' ').replace('Z',' '));
                temp_one = WayPoint.builder().lon(Double.parseDouble(temp.get("lon").toString())).lat(Double.parseDouble(temp.get("lat").toString())).time(c.getTimeInMillis()).build();

                temp_list.add(temp_one);
            }
            path = new Path(temp_list);

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
        return path;
    }

    public Path selectway() {
        JSONArray reqArray;
        Path path;
        Map dataMap = new HashMap();
        
        String url = "http://api.map.baidu.com/direction/v2/driving?origin=23.02363,113.42737&destination=23.05004,113.46101&ak=mbffiA31zBz8sQprxFWNaLHT6erdImrp";
        try {
            WSClient ws = new WSClient();
            String resMsg = ws.httpGet(url);
            JSONObject obj = new JSONObject(resMsg);
//            reqArray = new JSONArray(resMsg);
            obj = new JSONObject(obj.get("result").toString());
            reqArray = new JSONArray(obj.get("routes").toString());
            obj = reqArray.getJSONObject(0);
            reqArray = new JSONArray(obj.get("steps").toString());
            ArrayList<WayPoint> temp_list =new ArrayList<WayPoint>();
            for(int i = 0;i<reqArray.length();i++)
            {

                JSONObject temp = reqArray.getJSONObject(i);
                WayPoint start;
                if(i==0)
                {
                    Long time = new Date().getTime();
                    start = WayPoint.builder().lon(Double.parseDouble(temp.getJSONObject("start_location").get("lng").toString())).lat(Double.parseDouble(temp.getJSONObject("start_location").get("lat").toString())).time(time).build();
                }
                else
                {
                    start = WayPoint.builder().lon(Double.parseDouble(temp.getJSONObject("start_location").get("lng").toString())).lat(Double.parseDouble(temp.getJSONObject("start_location").get("lat").toString())).time(temp_list.get(temp_list.size()-1).getTime().get()).build();

                }

                WayPoint end = WayPoint.builder().lon(Double.parseDouble(temp.getJSONObject("end_location").get("lng").toString())).lat(Double.parseDouble(temp.getJSONObject("end_location").get("lat").toString())).build();
                int metre = temp.getInt("distance")/100 - 1;
                temp_list.add(start);
                System.out.println(temp_list.size());
                if(metre>0) temp_list = MidPoint(start,end,1,metre,temp_list);
                end = WayPoint.builder().lon(Double.parseDouble(temp.getJSONObject("end_location").get("lng").toString())).lat(Double.parseDouble(temp.getJSONObject("end_location").get("lat").toString())).time(temp_list.get(temp_list.size()-1).getTime().get()).build();
                temp_list.add(end);
                
            }
            path = new Path(temp_list);

        } catch (Exception e) {
            System.out.println("请求异常");
            throw new RuntimeException(e);
        }
        return path;

    }
    public ArrayList<WayPoint> MidPoint(WayPoint start,WayPoint end,int point,int metre,ArrayList<WayPoint> waypoints)
    {
        if(point>=metre)
        {
            WayPoint mid = WayPoint.builder().lon((start.getLongitude().doubleValue()+end.getLongitude().doubleValue())/2).lat((start.getLatitude().doubleValue()+end.getLatitude().doubleValue())/2).time(waypoints.get(waypoints.size()-1).getTime().get().toInstant().toEpochMilli()+60000).build();
            waypoints.add(mid);
            return waypoints;
        }
        else
        {
            WayPoint mid = WayPoint.builder().lon((start.getLongitude().doubleValue()+end.getLongitude().doubleValue())/2).lat((start.getLatitude().doubleValue()+end.getLatitude().doubleValue())/2).build();
            waypoints = MidPoint(start,mid,point*2,metre,waypoints);
            waypoints = MidPoint(mid,end,point*2,metre,waypoints);
        }
        return waypoints;
    }

//    public void correct(ArrayList<WayPoint> waypoints) {
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try{
//
//                    String BMAP_AK = "mbffiA31zBz8sQprxFWNaLHT6erdImrp";
//                    String URL = "http://api.map.baidu.com/rectify/v1/track";
//                    Map dataMap = new HashMap();
//                    dataMap.put("ak", BMAP_AK);
//                    dataMap.put("point_list", getReqData(waypoints));
//                    WSClient ws = new WSClient();
//                    String resMsg = ws.httpPost(URL,dataMap);
//                    System.out.println("纠偏返回数据：" + resMsg);
//                    JSONObject obj = new JSONObject(resMsg);
//                    JSONArray reqArray = obj.getJSONArray("points");
//                    data.get(id_num).setWayPoint(creat_point(reqArray));
//                }catch (Exception e){
//                    System.out.println("请求异常");
//                    throw new RuntimeException(e);
//                }
//            }
//        }).start();
//    }
//    public ArrayList<WayPoint> creat_point(JSONArray reqArray) throws JSONException {
//        ArrayList<WayPoint> temp_list =new ArrayList<WayPoint>();
//        for(int i = 0;i<reqArray.length();i++)
//        {
//            WayPoint temp_one;
//            JSONObject temp = reqArray.getJSONObject(i);
//            temp_one = WayPoint.builder().lat(Double.parseDouble(temp.get("latitude").toString())..build(Double.parseDouble(temp.get("latitude").toString()),Double.parseDouble(temp.get("longitude").toString()));
//            temp_list.add(temp_one);
//        }
//        return temp_list;
//    }
//
//    public String getReqData(ArrayList<WayPoint> waypoints) throws JSONException {
//        JSONArray reqArray = new JSONArray();
//        for(int i = 0;i<waypoints.size();i++)
//        {
//            JSONObject obj = new JSONObject();
//            obj.put("latitude", waypoints.get(i).getLatitude().toString());
//            obj.put("longitude", waypoints.get(i).getLongitude().toString());
//            obj.put("coord_type_input", "bd09ll");
//            obj.put("loc_time", waypoints.get(i).getTime().get().toInstant().toEpochMilli());
//            reqArray.put(obj);
//        }
//        return reqArray.toString();
//    }
}