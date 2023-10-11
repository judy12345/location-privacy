package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;

public class Algorithm extends BaseEntity {
    /**
     * 物理主键
     */
    private Long id;
    /**
     * 算法名
     */
    private String algorithmName;
    /**
     * 算法描述
     */
    private String descri;
    /**
     * 算法强度
     */
    private Long strength;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public Long getStrength() {
        return strength;
    }

    public void setStrength(Long strength) {
        this.strength = strength;
    }
}
