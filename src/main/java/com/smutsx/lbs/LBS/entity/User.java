package com.smutsx.lbs.LBS.entity;

import com.smutsx.lbs.common.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

public class User extends BaseEntity {
    /**
     * 物理主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮件
     */
    private String email;
    /**
     * 手机号
     */
    private String phone;
    private Long  gender;
    /**
     * 工作
     */
    private String job;
    /**
     * 角色
     */
    private String roles;
    /**
     * 真实姓名
     */
    private String realname;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String birthdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getbirthdate() {
        return birthdate;
    }

    public void setbirthdate(String birthdate) {
        this.birthdate = birthdate;
    }
}
