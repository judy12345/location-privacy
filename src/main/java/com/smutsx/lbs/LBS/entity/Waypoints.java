package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;

public class Waypoints extends BaseEntity {
    /**
     * 物理主键
     */
    private Long id;
    /**
     * 轨迹时间
     */
    private Long time;
    /**
     * 轨迹数据
     */
    private String waypoints;
    /**
     * 真实轨迹为1，虚假为0
     */
    private Long istrue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public Long getIstrue() {
        return istrue;
    }

    public void setIstrue(Long istrue) {
        this.istrue = istrue;
    }
}
