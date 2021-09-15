package com.smutsx.lbs.LBS.server.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.WayPoint;
public class Grid {
    private double latMin;
    private double latMax;
    private double lngMin;
    private double lngMax;
    private int count;

    public Grid(double _latMin, double _latMax, double _lngMin, double _lngMax) {
        latMin = _latMin;
        latMax = _latMax;
        lngMin = _lngMin;
        lngMax = _lngMax;
    }

    public Grid(Path p) {
        ArrayList<WayPoint> wayPoints = p.getWayPoints();
        if (wayPoints == null || wayPoints.size() == 0) {
            throw new ExceptionInInitializerError();
        }
        latMin = wayPoints.stream()
                .map(WayPoint::getLatitude)
                .map(Latitude::doubleValue)
                .min(Comparator.comparing(Double::doubleValue)).get();
        latMax = wayPoints.stream()
                .map(WayPoint::getLatitude)
                .map(Latitude::doubleValue)
                .max(Comparator.comparing(Double::doubleValue)).get();
        lngMin = wayPoints.stream()
                .map(WayPoint::getLongitude)
                .map(Longitude::doubleValue)
                .min(Comparator.comparing(Double::doubleValue)).get();
        lngMax = wayPoints.stream()
                .map(WayPoint::getLongitude)
                .map(Longitude::doubleValue)
                .max(Comparator.comparing(Double::doubleValue)).get();
    }

    public double getLatMin() {
        return latMin;
    }

    public double getLatMax() {
        return latMax;
    }

    public double getLngMin() {
        return lngMin;
    }

    public double getLngMax() {
        return lngMax;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean isWayPointInside(WayPoint wayPoint) {
        return wayPoint.getLatitude().doubleValue() >= latMin
                && wayPoint.getLatitude().doubleValue() <= latMax
                && wayPoint.getLongitude().doubleValue() >= lngMin
                && wayPoint.getLongitude().doubleValue() <= lngMax;
    }

    public WayPoint getRandomWayPointInside() {
        Random r = new Random();
        double lat = latMin + ((latMax - latMin) * r.nextDouble());
        double lng = lngMin + ((lngMax - lngMin) * r.nextDouble());
        return WayPoint.builder().lat(lat).lon(lng).build();
    }

    @Override
    public String toString() {
        return "Grid{" +
                "latMin=" + latMin +
                ", latMax=" + latMax +
                ", lngMin=" + lngMin +
                ", lngMax=" + lngMax +
                ", count=" + count +
                '}';
    }
}
