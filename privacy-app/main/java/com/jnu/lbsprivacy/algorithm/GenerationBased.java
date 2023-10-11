package com.jnu.lbsprivacy.algorithm;


import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.jnu.lbsprivacy.utils.Path;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;

import io.jenetics.jpx.WayPoint;

public class GenerationBased {
    // 层次：多条轨迹->每条轨迹->每个点->每个点的参数取值范围
    // mainActivity传过来的是：Path
    // 先转成：多条轨迹->每条轨迹->每个点->每个点的参数
    // 再转成：层次：多条轨迹->每条轨迹->每个点->每个点的参数取值范围
    ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> notReconstructResult = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<Double>>> trajectoryRemember1 = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<Double>>> trajectoryRemember2 = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> trajectorySet = new ArrayList<>();
    // 将mainActivity传过来的值转换成对应算法所需要的数据结构
    // 时间的浮动是5s
    // 路径的浮动是10m
    // 经度每隔0.0001度，距离相差约10米,纬度也是如此，所以浮动度数为0.0001
    @RequiresApi(api = Build.VERSION_CODES.O)
    public GenerationBased(ArrayList<Path> TrajectorySetPath) {
        ArrayList<ArrayList<ArrayList<Double>>> TrajectorySetSendFromMain = new ArrayList<>();
        for(Path trajectory : TrajectorySetPath){
            ArrayList<ArrayList<Double>> newTrajectory = new ArrayList<>();
            ArrayList<WayPoint> wayPoint = trajectory.getWayPoints();
            for(WayPoint point : wayPoint){
                ArrayList<Double> newPoint = new ArrayList<>();
                newPoint.add(point.getLatitude().doubleValue());
                newPoint.add(point.getLongitude().doubleValue());
                Optional<ZonedDateTime> timeLog = point.getTime();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                double timeLogDouble = 0;
                try {
                    timeLogDouble = (double) (simpleDateFormat.parse(timeLog.get().toString()).getTime() / 1000);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newPoint.add(timeLogDouble);
                newTrajectory.add(newPoint);
            }
            TrajectorySetSendFromMain.add(newTrajectory);
        }





        //对每条轨迹
        for (int i = 0; i < TrajectorySetSendFromMain.size(); i++){
            //对每个点
            ArrayList<ArrayList<ArrayList<Double>>> trajectory =new ArrayList<>();
            for (int j = 0; j < TrajectorySetSendFromMain.get(i).size(); j++){
                ArrayList<ArrayList<Double>> latLngTime = new ArrayList<>();
                ArrayList<Double> lat = new ArrayList<>();
                lat.add(TrajectorySetSendFromMain.get(i).get(j).get(0) - 0.0001);
                lat.add(TrajectorySetSendFromMain.get(i).get(j).get(0) + 0.0001);
                ArrayList<Double> lng = new ArrayList<>();
                lng.add(TrajectorySetSendFromMain.get(i).get(j).get(1) - 0.0001);
                lng.add(TrajectorySetSendFromMain.get(i).get(j).get(1) + 0.0001);
                ArrayList<Double> time = new ArrayList<>();
                time.add(TrajectorySetSendFromMain.get(i).get(j).get(2) - 5);
                time.add(TrajectorySetSendFromMain.get(i).get(j).get(2) + 5);
                latLngTime.add(lat);
                latLngTime.add(lng);
                latLngTime.add(time);
                trajectory.add(latLngTime);
            }
            trajectorySet.add(trajectory);
        }
    }

    // 随机取 0 到 num-1 的数
    private int randomlyChoose(int num){
        return new Random().nextInt(num);
    }

    // 算法1: multiTGA
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> multiTGA(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> TR, int k, boolean isFast, boolean isReconstruction){
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> result = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> G = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> repG = new ArrayList<>();
        do{
            G.clear();
            repG.clear();
            int randFirst = randomlyChoose(TR.size());
            ArrayList<ArrayList<ArrayList<Double>>> tr = TR.get(randFirst);
            G.add(tr);
            repG = tr;

            do{
                ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> TRSubG = subtraction(TR,G);
                ArrayList<ArrayList<ArrayList<Double>>> trPrime = findTheClosestTrajectory(TRSubG, repG);
                G.add(trPrime);
                if (!isFast){
                    ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> tempSet = new ArrayList<>();
                    tempSet.add(repG);
                    tempSet.add(trPrime);
                    repG = anonTrajectory(tempSet).get(0);
                }
            }while(G.size()<k);
            ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> newResult = anonTrajectory(G);
            notReconstructResult.addAll(newResult);
            if (isReconstruction){
                newResult = reconstruction(newResult, k);
            }
            result.addAll(newResult);
            TR = subtraction(TR,G);
        }while(TR.size()>=k);
        return result;
    }

    // 重构轨迹，随机地在一条轨迹上获得k条轨迹
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> reconstruction(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> shouldBeReconstruct, int k) {
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> reconstructionSet = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Double>>> trajectory = shouldBeReconstruct.get(0);
        for (int i = 0; i < k; i++) {
            ArrayList<ArrayList<ArrayList<Double>>> reconstructionTrajectory = new ArrayList<>();
            for (ArrayList<ArrayList<Double>> trajectoryPoint : trajectory) {
                ArrayList<ArrayList<Double>> onePoint = new ArrayList<>();
                double latRandom = doubleRandom(trajectoryPoint.get(0).get(0), trajectoryPoint.get(0).get(1));
                ArrayList<Double> latRandomList = new ArrayList<>();
                latRandomList.add(latRandom - 0.0001);
                latRandomList.add(latRandom + 0.0001);
                double lngRandom = doubleRandom(trajectoryPoint.get(1).get(0), trajectoryPoint.get(1).get(1));
                ArrayList<Double> lngRandomList = new ArrayList<>();
                lngRandomList.add(lngRandom - 0.0001);
                lngRandomList.add(lngRandom + 0.0001);
                double timeRandom = doubleRandom(trajectoryPoint.get(2).get(0), trajectoryPoint.get(2).get(1));
                ArrayList<Double> timeRandomList = new ArrayList<>();
                timeRandomList.add(timeRandom - 5);
                timeRandomList.add(timeRandom + 5);
                onePoint.add(latRandomList);
                onePoint.add(lngRandomList);
                onePoint.add(timeRandomList);
                reconstructionTrajectory.add(onePoint);
            }
            reconstructionSet.add(reconstructionTrajectory);
        }
        return reconstructionSet;
    }

    // 获取 min - max 范围内的 double
    private double doubleRandom(double min, double max) {
        return min + ((max - min) * new Random().nextDouble());
    }

    // 在集合中找到距离“tra”路径最近的一条路径
    private ArrayList<ArrayList<ArrayList<Double>>> findTheClosestTrajectory(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> set,ArrayList<ArrayList<ArrayList<Double>>> tra){
        double minValue = Double.POSITIVE_INFINITY;
        ArrayList<ArrayList<ArrayList<Double>>> trPrime = new ArrayList<>();
        for (ArrayList<ArrayList<ArrayList<Double>>> eachTra : set){
            double DSTResult = DSTorOPT(eachTra,tra);
            if(DSTResult < minValue){
                minValue = DSTResult;
                trPrime = eachTra;
            }
        }
        return trPrime;
    }

    // set1 - set2 求差集
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> subtraction(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> set1,ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> set2){
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> setTemp = new ArrayList<>(set1);
        // 遍历每一条轨迹
        Iterator<ArrayList<ArrayList<ArrayList<Double>>>> it = setTemp.iterator();
        while(it.hasNext()) {
            ArrayList<ArrayList<ArrayList<Double>>> curr = it.next();
            if(set2.contains(curr)) {
                it.remove();
            }
        }
        return setTemp;
    }

    // 去除 M 中多余的轨迹点
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> suppressUnmatched(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> M,ArrayList<ArrayList<ArrayList<Double>>> trPrime){
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> MTemp = new ArrayList<>(M);
        ArrayList<Integer> shouldBeDelete = new ArrayList<>();
        for(ArrayList<ArrayList<Double>> primePoint : trPrime){
            if(!trajectoryRemember1.contains(primePoint)){
                int index = trPrime.indexOf(primePoint);
                shouldBeDelete.add(index);
            }
        }
        // 只能降序删除
        for(int i = shouldBeDelete.size() -1; i>=0;i--){
            for (ArrayList<ArrayList<ArrayList<Double>>> eachMTemp : MTemp) {
                eachMTemp.remove((int)shouldBeDelete.get(i));
            }
        }

        return MTemp;
    }

    // 把没有连到线的点去除
    private ArrayList<ArrayList<ArrayList<Double>>> removeNotExist(ArrayList<ArrayList<ArrayList<Double>>> tra1){
        ArrayList<ArrayList<ArrayList<Double>>> traTemp = new ArrayList<>(tra1);
        // 遍历每一条轨迹
        Iterator<ArrayList<ArrayList<Double>>> it = traTemp.iterator();
        while(it.hasNext()) {
            ArrayList<ArrayList<Double>> curr = it.next();
            if(!trajectoryRemember2.contains(curr)) {
                it.remove();
            }
        }
        return traTemp;
    }

    // 求sigmaLCM的值
    private double sigmaLCM(ArrayList<ArrayList<Double>> p1, ArrayList<ArrayList<Double>> p2){
        double result;
        double S;
        double T;
        double U;
        if (p2 == null){
            // maximum
            double diagonal1 = DistanceUtil.getDistance(new LatLng(p1.get(0).get(0),p1.get(1).get(0)),new LatLng(p1.get(0).get(1),p1.get(1).get(1)));
            double diagonal2 = DistanceUtil.getDistance(new LatLng(p1.get(0).get(0),p1.get(1).get(1)),new LatLng(p1.get(0).get(1),p1.get(1).get(0)));
            double diagonal = Math.max(diagonal1, diagonal2);
            S = (diagonal * diagonal) / 2; // 求正方体面积
            T = Math.abs(p1.get(2).get(1) - p1.get(2).get(0)); // 时间间隔
            U = S * T;
            result = Math.log(U);
        }else{
            // minimum
            double maxLat = Math.max(p1.get(0).get(1),p2.get(0).get(1));
            double minLat = Math.min(p1.get(0).get(0),p2.get(0).get(0));
            double maxLng = Math.max(p1.get(1).get(1),p2.get(1).get(1));
            double minLng = Math.min(p1.get(1).get(0),p2.get(1).get(0));
            double length = DistanceUtil.getDistance(new LatLng(minLat,minLng), new LatLng(maxLat,minLng));
            double width = DistanceUtil.getDistance(new LatLng(minLat,minLng), new LatLng(minLat,maxLng));
            S = length * width;
            T = Math.max(Math.max(p1.get(2).get(1),p2.get(2).get(1)),Math.max(p1.get(2).get(0),p2.get(2).get(0))) - Math.min(Math.min(p1.get(2).get(1),p2.get(2).get(1)),Math.min(p1.get(2).get(0),p2.get(2).get(0)));
            U = S * T;
            result = Math.log(U);
        }
        return result;
    }


    // DST = OPT
    private double DSTorOPT(ArrayList<ArrayList<ArrayList<Double>>> tr1,ArrayList<ArrayList<ArrayList<Double>>> tr2){
        // 用动态规划的方法
        int rowSize = tr1.size() + 1;
        int colSize = tr2.size() + 1;
        // 目的就是要计算dpArray[rowSize-1][colSize-1]的值
        double[][] dpArray = new double[rowSize][colSize];
        dpArray[0][0] = 0;
        // |tr1| = 0 的情况, dp 矩阵的第一行
        for(int j = 1; j < colSize; j++){
            dpArray[0][j] = 0;
            for (int tr2PointTime = colSize - 2; tr2PointTime >= colSize - j - 1; tr2PointTime--){
                dpArray[0][j] += sigmaLCM(tr2.get(tr2PointTime),null);
            }
        }

        // |tr2| = 0 的情况, dp 矩阵的第一列
        for(int i = 1; i < rowSize; i++){
            dpArray[i][0] = 0;
            for (int tr1PointTime = rowSize - 2; tr1PointTime >= rowSize - i - 1; tr1PointTime--){
                dpArray[i][0] += sigmaLCM(tr1.get(tr1PointTime),null);
            }
        }
        // 行元素为tr2，列元素为tr1
        //   //       //          //           //           //         //
        //  空        pn        pn-1,pn    pn-2,pn-1,pn   ……………………     tr2
        //  pn
        //  pn-1,pn
        //  pn-2,pn-1,pn
        // …………………………
        //  tr1

        // 从第二行第二列开始计算，此为 |tr1|,|tr2| > 0 的情况
        // 逐渐往右下角进行计算，直到求出 dpArray[rowSize-1][colSize-1] = dpArray[i][j]
        // tr1 - tr1.p1 相当于往上取一格，行-1
        // tr1 则行不变
        // tr2 - tr2.p1 相当于往左取一格，列-1
        // tr2 则列不变
        // 所以会有三个方位 上、左、左上
        for(int i = 1; i < rowSize; i++){
            for(int j = 1; j < colSize; j++){
                double value1 = dpArray[i-1][j-1] + sigmaLCM(tr1.get(rowSize - i - 1),tr2.get(colSize - j - 1));
                double value2 = dpArray[i][j-1] + sigmaLCM(tr2.get(colSize - j - 1),null);
                double value3 = dpArray[i-1][j] + sigmaLCM(tr1.get(rowSize - i - 1),null);
                double minValue23 = Math.min(value2,value3);
                if (value1 < minValue23){
                    if (!trajectoryRemember1.contains(tr1.get(rowSize - i - 1)) && !trajectoryRemember2.contains(tr2.get(colSize - j - 1))){
                        trajectoryRemember1.add(tr1.get(rowSize - i - 1));
                        trajectoryRemember2.add(tr2.get(colSize - j - 1));
                    }
                    dpArray[i][j] = value1;
                }else{
                    dpArray[i][j] = minValue23;
                }
            }
        }
        return dpArray[rowSize-1][colSize-1];
    }

    // 从给定的 M 中找到一个匿名化的路径 tr-prime 来代替
    private ArrayList<ArrayList<ArrayList<Double>>> searchAnon(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> M){
        ArrayList<ArrayList<ArrayList<Double>>> trPrime = new ArrayList<>();
        if (M.size()==1){
            // 只有一个元素，那么，它的匿名就是它本身
            trPrime = M.get(0);
        }else{
            // 将这几条路径综合成一条路径,成为一个匿名
            // 只需要使得 tr-prime 涵盖整个轨迹 M 即可
            // 当然这里默认两个路径长度一样，因为除了第一条路径，其他都经过了matching的操作
            for(int i = 0;i < Math.min(M.get(0).size(),M.get(1).size()); i++){
                double maxValueLat = Double.NEGATIVE_INFINITY;
                double minValueLat = Double.POSITIVE_INFINITY;
                double maxValueLng = Double.NEGATIVE_INFINITY;
                double minValueLng = Double.POSITIVE_INFINITY;
                double maxValueTime = Double.NEGATIVE_INFINITY;
                double minValueTime = Double.POSITIVE_INFINITY;
                ArrayList<ArrayList<Double>> point = new ArrayList<>();
                for(ArrayList<ArrayList<ArrayList<Double>>> trajectory : M){
                    if(trajectory.get(i).get(0).get(1) > maxValueLat){
                        maxValueLat = trajectory.get(i).get(0).get(1);
                    }
                    if(trajectory.get(i).get(0).get(0) < minValueLat){
                        minValueLat = trajectory.get(i).get(0).get(0);
                    }
                    if(trajectory.get(i).get(1).get(1) > maxValueLng){
                        maxValueLng = trajectory.get(i).get(1).get(1);
                    }
                    if(trajectory.get(i).get(1).get(0) < minValueLng){
                        minValueLng = trajectory.get(i).get(1).get(0);
                    }
                    if(trajectory.get(i).get(2).get(1) > maxValueTime){
                        maxValueTime = trajectory.get(i).get(2).get(1);
                    }
                    if(trajectory.get(i).get(2).get(0) < minValueTime){
                        minValueTime = trajectory.get(i).get(2).get(0);
                    }
                }
                ArrayList<Double> Lat = new ArrayList<>();
                ArrayList<Double> Lng = new ArrayList<>();
                ArrayList<Double> Time = new ArrayList<>();
                Lat.add(minValueLat);
                Lat.add(maxValueLat);
                Lng.add(minValueLng);
                Lng.add(maxValueLng);
                Time.add(minValueTime);
                Time.add(maxValueTime);
                point.add(Lat);
                point.add(Lng);
                point.add(Time);
                trPrime.add(point);
            }
        }
        return trPrime;
    }


    // 匿名化轨迹
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> anonTrajectory(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> G){
        ArrayList<ArrayList<ArrayList<Double>>> trM = calculatePairwise(G);
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> M = new ArrayList<>();
        M.add(trM);
        do{
            ArrayList<ArrayList<ArrayList<Double>>> trPrime = searchAnon(M);
            ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> GSubM = subtraction(G,M);
            ArrayList<ArrayList<ArrayList<Double>>> trNew = GSubM.get(randomlyChoose(GSubM.size()));
            // run OPT to find a min cost matching between the points in trNew and trPrime;
            trajectoryRemember1.clear();
            trajectoryRemember2.clear();
            DSTorOPT(trPrime,trNew);
            // create links;
            // trajectoryRemember1 & trajectoryRemember2 creates the links
            // suppress all unmatched
            trNew = removeNotExist(trNew); // suppress all unmatched in trNew // suppress all unmatched in trPrime and M
            M = suppressUnmatched(M, trPrime);
            M.add(trNew);
        }while(M.size()<G.size());
        M = replacePoints(M);
        return M;
    }

    // 将几条轨迹合并成一条大的轨迹
    private ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> replacePoints(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> M){
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> MTemp = new ArrayList<>(M);
        int size = Math.min(MTemp.get(0).size(),MTemp.get(1).size());
        for(int i = 0; i < size; i++){
            double maxValueLat = Double.NEGATIVE_INFINITY;
            double minValueLat = Double.POSITIVE_INFINITY;
            double maxValueLng = Double.NEGATIVE_INFINITY;
            double minValueLng = Double.POSITIVE_INFINITY;
            double maxValueTime = Double.NEGATIVE_INFINITY;
            double minValueTime = Double.POSITIVE_INFINITY;
            ArrayList<ArrayList<Double>> point = new ArrayList<>();
            for(ArrayList<ArrayList<ArrayList<Double>>> trajectory : MTemp){
                if(trajectory.get(i).get(0).get(1) > maxValueLat){
                    maxValueLat = trajectory.get(i).get(0).get(1);
                }
                if(trajectory.get(i).get(0).get(0) < minValueLat){
                    minValueLat = trajectory.get(i).get(0).get(0);
                }
                if(trajectory.get(i).get(1).get(1) > maxValueLng){
                    maxValueLng = trajectory.get(i).get(1).get(1);
                }
                if(trajectory.get(i).get(1).get(0) < minValueLng){
                    minValueLng = trajectory.get(i).get(1).get(0);
                }
                if(trajectory.get(i).get(2).get(1) > maxValueTime){
                    maxValueTime = trajectory.get(i).get(2).get(1);
                }
                if(trajectory.get(i).get(2).get(0) < minValueTime){
                    minValueTime = trajectory.get(i).get(2).get(0);
                }
            }
            ArrayList<Double> Lat = new ArrayList<>();
            ArrayList<Double> Lng = new ArrayList<>();
            ArrayList<Double> Time = new ArrayList<>();
            Lat.add(minValueLat);
            Lat.add(maxValueLat);
            Lng.add(minValueLng);
            Lng.add(maxValueLng);
            Time.add(minValueTime);
            Time.add(maxValueTime);
            point.add(Lat);
            point.add(Lng);
            point.add(Time);
            for(ArrayList<ArrayList<ArrayList<Double>>> trajectory : MTemp) {
                trajectory.set(i, point);
            }
        }
        return MTemp;
    }


    // 计算 成对的轨迹之间的距离和，并给出最小的距离和
    // 原文算法：Let trM ∈ G be the trajectory whose total pairwise distance with other trajectories is minimum;
    private ArrayList<ArrayList<ArrayList<Double>>> calculatePairwise(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> G){
        ArrayList<Double> totalNumSet = new ArrayList<>();
        for(int i=0;i<G.size();i++){
            double totalNum = 0;
            for(int j=0;j<G.size();j++){
                if(i==j){
                    continue;
                }
                totalNum += DSTorOPT(G.get(i),G.get(j));
            }
            totalNumSet.add(totalNum);
        }

        int minIndex = 0;
        double minValue = Double.POSITIVE_INFINITY;
        for (int i=0;i<totalNumSet.size();i++){
            if(totalNumSet.get(i) < minValue){
                minIndex = i;
                minValue = totalNumSet.get(i);
            }
        }
        return G.get(minIndex);
    }

    // 计算两个经纬度之间的中点
    private static ArrayList<Double> LatLngCalculateMidPoint(double lat1, double lng1, double timeStamp1, double lat2, double lng2, double timeStamp2) {

        double dLng = Math.toRadians(lng2 - lng1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lng1 = Math.toRadians(lng1);

        double Bx = Math.cos(lat2) * Math.cos(dLng);
        double By = Math.cos(lat2) * Math.sin(dLng);

        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lng3 = lng1 + Math.atan2(By, Math.cos(lat1) + Bx);

        ArrayList<Double> midPoint = new ArrayList<>();
        midPoint.add(Math.toDegrees(lat3));
        midPoint.add(Math.toDegrees(lng3));
        midPoint.add((double) Math.round((timeStamp1 + timeStamp2) / 2)); //保证时间戳即使是double类型的也是整数
        return midPoint;
    }

    // 把算法的数据结构转成能够在地图上输出的数据结构
    private static ArrayList<Path> transToPath(ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> trajectorySet){
        // 对所有轨迹
        ArrayList<Path> wayPointsList = new ArrayList<>();
        for (ArrayList<ArrayList<ArrayList<Double>>> trajectory : trajectorySet){
            ArrayList<WayPoint> wayPoints = new ArrayList<>();
            // 对所有点
            for(ArrayList<ArrayList<Double>> point : trajectory){
                // 分别是 纬度最小，经度最小, 时间最小  纬度最大，经度最大, 时间最大
                ArrayList<Double> pointNeeded = LatLngCalculateMidPoint(point.get(0).get(0),point.get(1).get(0),point.get(2).get(0),point.get(0).get(1),point.get(1).get(1),point.get(2).get(1));
                WayPoint pointBuild = WayPoint.builder()
                        .lat(pointNeeded.get(0)).lon(pointNeeded.get(1))
                        .build();
                wayPoints.add(pointBuild);
            }
            wayPointsList.add(new Path(wayPoints));
        }
        return wayPointsList;
    }

    // 与MainActivity交互的接口
    // 展示算法的结果
    public ArrayList<Path> getResult() {
        int k = 2;
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> multiTrajectory = multiTGA(trajectorySet, k,false, true);
//        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> fastTrajectory = multiTGA(trajectorySet, k,true);
        return transToPath(multiTrajectory);
    }

    public ArrayList<Path> getResultNotReconstruct(){
        int k = 2;
        ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> multiTrajectory = multiTGA(trajectorySet, k,false, true);
        return transToPath(notReconstructResult);
    }

    // 与MainActivity交互的接口
    // 展示原来的路径
    public ArrayList<Path> getResultOriginal() {
        return transToPath(trajectorySet);
    }
}