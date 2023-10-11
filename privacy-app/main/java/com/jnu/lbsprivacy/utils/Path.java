package com.jnu.lbsprivacy.utils;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class Path {
    private List<WayPoint> wayPoints;

    public Path() {
        wayPoints = new ArrayList<WayPoint>();
    }

    public Path(ArrayList<WayPoint> _wayPoints) {
        wayPoints = _wayPoints;
    }

    public void loadFromGPX(InputStream inputStream) {
        try {
            wayPoints = GPX.read(inputStream)
                    .wayPoints().collect(Collectors.toList());
        } catch (IOException e) {
            Log.d("Path", e.toString());
        }
    }

    public ArrayList<LatLng> getLatLngList() {
        ArrayList<LatLng> res = new ArrayList<LatLng>();
        CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.COMMON);
        for (WayPoint waypoint : wayPoints) {
            LatLng commonCoord = new LatLng(waypoint.getLatitude().doubleValue(), waypoint.getLongitude().doubleValue());
            LatLng baiduCoord = converter.coord(commonCoord).convert();
            res.add(baiduCoord);
        }
        return res;
    }

    public void addWayPoint(WayPoint wp) {
        wayPoints.add(wp);
    }

    public ArrayList<WayPoint> getWayPoints() {
        return (ArrayList<WayPoint>) wayPoints;
    }

    public void setWayPoint(ArrayList<WayPoint> wayPoint) {
        this.wayPoints = wayPoint;
    }

    public void saveGPX(FileOutputStream outputStream) {
        GPX.Builder gpxBuiler= GPX.builder();
        for (WayPoint waypoint: wayPoints) {
            gpxBuiler.addWayPoint(waypoint);
        }

        try {
            GPX.write(gpxBuiler.build(), outputStream);
        }catch (IOException e) {
            Log.d("Path", e.toString());
        }
    }
    public WayPoint getoneWayPoint(int k) {
        WayPoint temp;
        temp = WayPoint.builder().build( (wayPoints.get(k).getLatitude()), (wayPoints.get(k).getLongitude()) );
        return temp;
    }

    @Override
    public String toString() {
        return "Path{" +
                "wayPoints=" + wayPoints +
                '}';
    }
}
