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
    public TraceMethod(Path p) {
        path = p;
        generate(10,1000,path.getWayPoints());
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
            for(int j=0;j<path.getWayPoints().size();j++)
            {
                temp = r.nextInt(a/10)+1;
                WayPoint temp_wayPoint = WayPoint.builder().lon(data.get(i-1).getWayPoints().get(j).getLongitude().doubleValue()+ a/temp * METER)
                        .lat(data.get(i-1).getWayPoints().get(j).getLatitude().doubleValue()+ temp * METER)
                        .time(data.get(i-1).getWayPoints().get(j).getTime().get()).build();
                data.get(i).addWayPoint(temp_wayPoint);
            }

        }

    }

    //生成一条虚假轨迹
    public void generate_false(int k)
    {
        path = new Path();
        int num = data.get(0).getWayPoints().size()/k;
        for(int i=0;i<data.get(0).getWayPoints().size();)
        {
            Random r = new Random();
            int temp = r.nextInt(k);
            if(i+10<data.get(0).getWayPoints().size())
            {
                for(int j=0;j<num;j++)
                {
                    path.addWayPoint(data.get(temp).getoneWayPoint(i));
                }
                i += num;
            }
            else
            {
                path.addWayPoint(data.get(temp).getoneWayPoint(i));
                i++;
            }
        }

    }

    //    @RequiresApi(api = Build.VERSION_CODES.O)
    public Path getResult() {
        Random r = new Random();
        generate_false(data.size());
//        correct();
        return path;
    }


}
