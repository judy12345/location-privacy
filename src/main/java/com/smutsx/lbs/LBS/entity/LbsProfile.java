package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;

public class LbsProfile extends BaseEntity {
    /**
     * 物理主键
     */
    private Long id;
    /**
     * 用户唯一标识
     */
    private String uid;
    /**
     * 用户年龄,1:0~18,2:19~30,3:30+
     */
    private Long age;
    /**
     * 用户目的，1:获取信息，2:导航，3：救援
     */
    private Long purpose;
    /**
     * 信息敏感度，1:不敏感，2:一般 ，3:敏感
     */
    private Long sensitivity;
    /**
     * 用户所处位置的情况，1：熟悉，2：不熟悉
     */
    private Long location;
    /**
     * 目前时间，1：安全时间，2：危险时间
     */
    private Long time;
    /**
     * 对应用的信任，1：信任，2：不信任
     */
    private Long trust;
    /**
     * k值
     */
    private Long k;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Long getPurpose() {
        return purpose;
    }

    public void setPurpose(Long purpose) {
        this.purpose = purpose;
    }

    public Long getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(Long sensitivity) {
        this.sensitivity = sensitivity;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getTrust() {
        return trust;
    }

    public void setTrust(Long trust) {
        this.trust = trust;
    }

    public Long getK() {
        return k;
    }

    public void setK(Long k) {
        this.k = k;
    }
    public void copy(LbsProfile lbsProfile)
    {
        this.age = lbsProfile.getAge();
        this.location = lbsProfile.getLocation();
        this.purpose = lbsProfile.getPurpose();
        this.sensitivity = lbsProfile.getSensitivity();
        this.time = lbsProfile.getTime();
        this.trust = lbsProfile.getTrust();
        this.uid = lbsProfile.getUid();
        this.id = lbsProfile.getId();
        this.k =lbsProfile.getK();
    }
}
