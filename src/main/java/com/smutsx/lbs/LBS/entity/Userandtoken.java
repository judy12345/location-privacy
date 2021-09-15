package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;

public class Userandtoken extends BaseEntity {
    /**
     * 物理主键
     */
    private Long id;

    /**
     * 用户id
     */
    private Long uid;
    /**
     * 用户登录凭证
     */
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
