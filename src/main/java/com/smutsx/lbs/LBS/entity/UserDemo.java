package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;

public class UserDemo extends BaseEntity{
    /**
     * 物理主键
     */
    private Long id;


    /**
     * 用户编号
     */
    private String userNo;
    /**
     * 用户名
     */
    private String loginName;
    /**
     * 密码
     */
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
