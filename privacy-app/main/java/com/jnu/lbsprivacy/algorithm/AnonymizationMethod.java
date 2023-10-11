package com.jnu.lbsprivacy.algorithm;

import android.util.Log;

import com.jnu.lbsprivacy.utils.Grid;
import com.jnu.lbsprivacy.utils.Path;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import io.jenetics.jpx.WayPoint;

public class AnonymizationMethod {
    // define some parameters
    final long REACHABLE_DIS = 500;
    final int CROSS_CHANCE = 3;

    private Path path;
    private ArrayList<Grid> gridArrayList;

    public AnonymizationMethod(Path p) {
        path = p;
    }

    public Path getResult() {
       Path res = new Path();
       initNineGrids();
       countWayPointInEachGrid();
       res.addWayPoint(getBasePauseWayPoint());

       Grid grid = new Grid(path);
       while (res.getWayPoints().size() < path.getWayPoints().size()) {
           WayPoint tmp = null;
           do {
               tmp = grid.getRandomWayPointInside();
           } while (
               !isWayPointReachable(
                       res.getWayPoints().get(res.getWayPoints().size() - 1),
                       tmp
               )
           );
           res.addWayPoint(tmp);
           // give some chance for the fake path cross with the real
           WayPoint pShared = null;
           Random rand = new Random();
           for (int i=0; i < CROSS_CHANCE; i++) {
               pShared = path.getWayPoints().get(rand.nextInt(path.getWayPoints().size()));
               if (isWayPointReachable(pShared, tmp)) {
                   res.addWayPoint(pShared);
                   Log.d("AnonymizationMethod", "Cross: " + pShared);
                   break;
               }
           }
       }
       return res;
    }

    private WayPoint getBasePauseWayPoint() {
        Grid gBase = getGridWithMinCount();
        ZonedDateTime tBase = path.getWayPoints().get(0).getTime().get();
        WayPoint res = gBase.getRandomWayPointInside().toBuilder().time(tBase).build();
        return res;
    }

        /* initNineGrids
       longMin                           longMax
latMin +------------+-------------+------------+
       |            |             |            |
       |            |             |            |
       |     7      |      8      |     9      |
       |            |             |            |
       |            |             |            |
       +---------------------------------------+
       |            |             |            |
       |            |             |            |
       |     4      |      5      |     6      |
       |            |             |            |
       |            |             |            |
       |            |             |            |
       +---------------------------------------+
       |            |             |            |
       |            |             |            |
       |     1      |      2      |     3      |
       |            |             |            |
       |            |             |            |
       |            |             |            |
 latMax+------------+-------------+------------+
     **/


    private void initNineGrids() {
       gridArrayList = new ArrayList<Grid>();
       Grid grid = new Grid(path);

       double latDistance = grid.getLatMax() - grid.getLatMin();
       double lngDistance = grid.getLngMax() - grid.getLngMin();

       for (int lat_step=0; lat_step < 3; lat_step ++) {
           for (int lng_step=0; lng_step < 3; lng_step++) {
               Grid small_grid = new Grid(
                       grid.getLatMin() + (lat_step / 3.0) * latDistance,
                       grid.getLatMin() + ((lat_step + 1) / 3.0) * latDistance,
                       grid.getLngMin() + (lng_step / 3.0) * lngDistance,
                       grid.getLngMin() + ((lng_step + 1) / 3.0) * lngDistance
               );
               gridArrayList.add(small_grid);
           }
       }
    }

    private void countWayPointInEachGrid() {
        for (WayPoint wayPoint: path.getWayPoints()) {
            for (Grid grid: gridArrayList) {
                if (grid.isWayPointInside(wayPoint)) {
                    grid.setCount(grid.getCount() + 1);
                }
            }
        }
    }

    private Grid getGridWithMinCount() {
        return gridArrayList.stream()
                .min(Comparator.comparing(Grid::getCount)).get();
    }

    private boolean isWayPointReachable(WayPoint dst, WayPoint src) {
        return src.distance(dst).longValue() < REACHABLE_DIS;
    }

}
