package com.smutsx.lbs.LBS.server.Algorithm;

import com.smutsx.lbs.LBS.server.utils.Path;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.stream.XMLInputFactory;

import io.jenetics.jpx.WayPoint;

public class TraceRebuiltMethod {
    private Path path;
    private List<WayPoint> wayPoints;

    double meter =100.0;
    double time = 60.0;
    public TraceRebuiltMethod(Path p) {
        path = p;
    }
    private WayPoint randomlyChoosePoiByCircle(double lat1,double lng1,int radius){
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String stringQuery = "http://api.map.baidu.com/place/v2/search?query=美食$酒店$购物$生活服务$休闲娱乐$旅游景点$医疗$交通设施$教育培训$政府机构&location="+ lat1 +","+lng1 + "&radius=" + radius + "&output=json&ak=OGaj625GYKPUfdZw12h2AdW8WmT8HGEt";
            URL url = new URL(stringQuery);
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("GET");
            //设置连接超时时间（毫秒）
            connection.setConnectTimeout(50000);
            //设置读取超时时间（毫秒）
            connection.setReadTimeout(50000);

            //返回输入流
            InputStream in = connection.getInputStream();

            //读取输入流
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            JSONObject jsonObject = new JSONObject(String.valueOf(result));
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");
            int lenResults = jsonArrayResults.length();
            if (lenResults == 0){
                return null;
            }else{
                int randIndex = randomlyChoose(lenResults);
                JSONObject randValue = jsonArrayResults.getJSONObject(randIndex);
                JSONObject location = randValue.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                return WayPoint.builder().build(lat,lng);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {//关闭连接
                connection.disconnect();
            }
        }
        return null;
    }

    private int randomlyChoose(int lenResults) {
        int x;
        Random random = new Random();
        x = random.nextInt(lenResults);
        return x;
    }
    //LatLng转Waypoints
    private WayPoint convert(WayPoint sourceLatLng)
    {
        double m=sourceLatLng.getLatitude().doubleValue();
        double n=sourceLatLng.getLongitude().doubleValue();
        WayPoint wayPoint=WayPoint.builder()
                .lat(m).lon(n)
                .build();
        return wayPoint;
    }

    //求两坐标的中点
    public static WayPoint midPoint(double lat1,double lon1,double lat2,double lon2){
        WayPoint wayPoint;
        double dLon = Math.toRadians(lon2 - lon1);
        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        wayPoint = WayPoint.builder()
                .lat(Math.toDegrees(lat3))
                .lon(Math.toDegrees(lon3))
                .build();
        return wayPoint;
    }
    //筛选符合要求的坐标
    private ArrayList<WayPoint> getPoint() {
        int k = path.getWayPoints().size();
        WayPoint Point;
        ArrayList<WayPoint> wayPoint = path.getWayPoints();
        ArrayList<WayPoint> mywayPoint = new ArrayList<>();
        int i = 0;
        int j;
        double poilat=0;
        double poilng=0;
        mywayPoint.add(wayPoint.get(0));
        while ( i < k - 1)
        {
            for(j = i+1;j<k-2;j++)
            {
                double x=getSecond(wayPoint.get(i),wayPoint.get(j));
                double jlat=wayPoint.get(j).getLatitude().doubleValue();
                double jlng=wayPoint.get(j).getLongitude().doubleValue();
                double ilat=wayPoint.get(i).getLatitude().doubleValue();
                double ilng=wayPoint.get(i).getLongitude().doubleValue();
                double dis =getDistance(ilat,ilng, jlat,jlng);

                if ((getSecond(wayPoint.get(i),wayPoint.get(j)) >time))
                {
                    WayPoint mid= midPoint(ilat,ilng,jlat,jlng);
                    mywayPoint.add(mid);
                    Point = randomlyChoosePoiByCircle(jlat,jlng,100);
                    while(poilat==Point.getLatitude().doubleValue() && poilng==Point.getLongitude().doubleValue())
                    {
                        Point=randomlyChoosePoiByCircle(jlat,jlng,100);

                    }
                    mywayPoint.add(convert(Point));
                    poilat=Point.getLatitude().doubleValue();
                    poilng=Point.getLongitude().doubleValue();
                }
                else
                {

                    while(j<k-2)
                    {
                        mywayPoint.add(wayPoint.get(j));
                        if ((getSecond(wayPoint.get(i), wayPoint.get(j)) > time && (getDistance(ilat,ilng,wayPoint.get(j).getLatitude().doubleValue(),wayPoint.get(j).getLongitude().doubleValue())< meter)))
                        {
                            WayPoint mid= midPoint(ilat,ilng,wayPoint.get(j).getLatitude().doubleValue(),wayPoint.get(j).getLongitude().doubleValue());
                            mywayPoint.add(mid);
                            //取中点作为stop
                            double lat=mid.getLatitude().doubleValue();
                            double lng=mid.getLongitude().doubleValue();
                            Point = randomlyChoosePoiByCircle(lat, lng, 100);
                            //判断POI是否重复
                            while(poilat==Point.getLatitude().doubleValue() && poilng==Point.getLongitude().doubleValue())
                            {
                                Point=randomlyChoosePoiByCircle(jlat,jlng,100);

                            }
                            mywayPoint.add(convert(Point));
                            poilat=Point.getLatitude().doubleValue();
                            poilng=Point.getLongitude().doubleValue();
                            break;
                        }
                        else
                        {
                            mywayPoint.add(wayPoint.get(j));
                            j++;}
                    }
                }
                break;
            }
            i=j;
        }
        mywayPoint.add(wayPoint.get(k-1));
        return mywayPoint;
    }
    private WayPoint getMyWaypoint(ArrayList<WayPoint> wayPoints,int k)
    {
        return wayPoints.get(k);
    }
    //两个坐标的距离
    public static double getDistance(double lng1,double lat1,double lng2,double lat2){
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    //两坐标时间间隔
    public static double getSecond(WayPoint w1, WayPoint w2)
    {
        int m;
        m=w2.getTime().get().getHour()*3600+w2.getTime().get().getMinute()*60+w2.getTime().get().getSecond()-
                w1.getTime().get().getHour()*3600-w1.getTime().get().getMinute()*60-w1.getTime().get().getSecond();
        return m;
    }
    public Path getResult() {
        Path res = new Path();
        ArrayList<WayPoint> finelPoint=getPoint();
        for(int i= 0;i< finelPoint.size();i++)
            res.addWayPoint(getMyWaypoint(finelPoint,i));
        return res;
    }

}
