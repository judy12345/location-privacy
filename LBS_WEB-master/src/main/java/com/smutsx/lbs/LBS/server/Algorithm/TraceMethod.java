package com.smutsx.lbs.LBS.server.Algorithm;

import com.smutsx.lbs.LBS.server.utils.Path;
import com.smutsx.lbs.LBS.server.utils.WSClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import io.jenetics.jpx.WayPoint;

public class TraceMethod {
    private Path path;

    private ArrayList<Path> data;
    //每米的大约经纬度差距
    final double METER = 0.00001;
    boolean HTTP_FLAG = false;
    public TraceMethod(Path p,int k) {
        path = p;
        int a = k * 100;
        generate(k,a,(ArrayList<WayPoint>) path.getWayPoints().clone());
        exchange(k,a,data.get(3).getWayPoints().get(0));
    }
    //初始化生成各个
    public void generate(int k,int a,ArrayList<WayPoint> wayPoint)
    {
        Random r = new Random();
        int temp;
        data = new ArrayList<Path>();
        data.add(new Path(wayPoint));
        for(int i = 1;i < k;i++)
        {
            data.add(new Path());
            temp = r.nextInt(a/10)+1;
            WayPoint temp_wayPoint;
            temp_wayPoint = WayPoint.builder().build( wayPoint.get(0).getLatitude().doubleValue() + a/temp * METER, wayPoint.get(0).getLongitude().doubleValue() + temp * METER );
            data.get(i).addWayPoint(temp_wayPoint);
        }

    }
    public void exchange(int k,int a,WayPoint wayPoint)
    {
        int num;
        int scope;
        Random r = new Random();
        WayPoint now ;
        WayPoint temp;
        now = WayPoint.builder().build(wayPoint.getLatitude().doubleValue(),wayPoint.getLongitude().doubleValue());
        temp = WayPoint.builder().build(wayPoint.getLatitude().doubleValue(),wayPoint.getLongitude().doubleValue());
        for (int i = 1;i < k;i++)
        {
            num = r.nextInt(k) + 1;
            for (int j = 1;j <= num;j++)
            {
                scope = r.nextInt(a/10)+1;
                temp= WayPoint.builder().build(now.getLatitude().doubleValue()+ scope * METER,now.getLongitude().doubleValue()+a/num * METER);
            }
            now = WayPoint.builder().build(temp.getLatitude().doubleValue(),temp.getLongitude().doubleValue());;
            data.get(i).getWayPoints().remove(0);
            data.get(i).addWayPoint(now);
        }
    }

    //生成一条虚假轨迹
    public void generate_false(int id_num,int step,WayPoint wayPoint)
    {
        Random r = new Random();
        int ran;
        Path start = data.get(id_num);
        WayPoint now = start.getoneWayPoint(0);


        //随机设置经度纬度的步数
        int lat_step = r.nextInt(step-5);
        int long_step = step - 1 - lat_step;

        //随机设置经度纬度的每次步长
        double lat_onestep = (now.getLatitude().doubleValue() - wayPoint.getLatitude().doubleValue()) / lat_step;
        double long_onestep = (now.getLongitude().doubleValue() - wayPoint.getLongitude().doubleValue()) /long_step;

        while(true)
        {
            //随机判断每次走哪个方向
            ran =r.nextInt(3);
            if(lat_step==0 && long_step==0) break;
            if(ran == 0 && long_step > 0)
            {
                now = WayPoint.builder().build(now.getLatitude().doubleValue(),now.getLongitude().doubleValue() - long_onestep );
                long_step--;
            }
            else if(ran == 1 && lat_step > 0)
            {
                now = WayPoint.builder().build( now.getLatitude().doubleValue()- lat_onestep,now.getLongitude().doubleValue() );
                lat_step--;
            }
            else if(ran == 2 && lat_step > 0 && long_step > 0)
            {
                now = WayPoint.builder().build( now.getLatitude().doubleValue()- lat_onestep, now.getLongitude().doubleValue()-long_onestep);
                long_step--;
                lat_step--;
            }
            else continue;

            //每次的定位点存入轨迹集
            WayPoint temp_wayPoint;
            temp_wayPoint = WayPoint.builder().build( now.getLatitude().doubleValue(), now.getLongitude().doubleValue() );
            start.addWayPoint(temp_wayPoint);
        }
        WayPoint temp_wayPoint;
        temp_wayPoint = WayPoint.builder().build( now.getLatitude().doubleValue(), now.getLongitude().doubleValue() );
        start.addWayPoint(temp_wayPoint);

    }
    //生成一条轨迹纠正correct
    public void correct(int id_num) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                try{

                    String BMAP_AK = "mbffiA31zBz8sQprxFWNaLHT6erdImrp";
                    String URL = "http://api.map.baidu.com/rectify/v1/track";
                    Map dataMap = new HashMap();
                    dataMap.put("ak", BMAP_AK);
                    dataMap.put("point_list", getReqData(id_num));
                    WSClient ws = new WSClient();
                    String resMsg = ws.httpPost(URL,dataMap);
                    System.out.println("纠偏返回数据：" + resMsg);
                    JSONObject obj = new JSONObject(resMsg);
                    JSONArray reqArray = obj.getJSONArray("points");
                    data.get(id_num).setWayPoint(creat_point(reqArray));
                    HTTP_FLAG = true;
                }catch (Exception e){
                    System.out.println("请求异常");
                    HTTP_FLAG = true;
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    //从json读取一组点
    public ArrayList<WayPoint> creat_point(JSONArray reqArray) throws JSONException {
        ArrayList<WayPoint> temp_list =new ArrayList<WayPoint>();
        for(int i = 0;i<reqArray.length();i++)
        {
            WayPoint temp_one;
            JSONObject temp = reqArray.getJSONObject(i);
            temp_one = WayPoint.builder().build(Double.parseDouble(temp.get("latitude").toString()),Double.parseDouble(temp.get("longitude").toString()));
            temp_list.add(temp_one);
        }
        return temp_list;
    }
    public String getReqData(int k) throws JSONException {
        JSONArray reqArray = new JSONArray();
        long time = unixTime();
        for(int i = 0;i<data.get(k).getWayPoints().size();i++)
        {
            JSONObject obj = new JSONObject();
            obj.put("latitude", data.get(k).getoneWayPoint(i).getLatitude().toString());
            obj.put("longitude", data.get(k).getoneWayPoint(i).getLongitude().toString());
            obj.put("coord_type_input", "bd09ll");
            obj.put("loc_time", time+i*100000);
            reqArray.put(obj);
        }
        return reqArray.toString();
    }
    public  long unixTime() {
        return new Date().getTime() / 1000;
    }

    public Path getResult() {

        Random r = new Random();
        int k = r.nextInt(data.size()-1) + 1;
        int step = data.get(0).getWayPoints().size();
        generate_false(k,step,data.get(0).getWayPoints().get(step-1));
        correct(k);
        while(HTTP_FLAG);
        HTTP_FLAG = false;
        ArrayList<WayPoint> orig = data.get(k).getWayPoints();
        return new Path(orig);
    }

}
