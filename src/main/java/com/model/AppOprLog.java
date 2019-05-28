package com.model;

import java.util.Date;

public class AppOprLog {
    private Integer id;

    private String moduleId;

    private String oprContent;

    private Date oprTime;

    private String userid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getOprContent() {
        return oprContent;
    }

    public void setOprContent(String oprContent) {
        this.oprContent = oprContent;
    }

    public Date getOprTime() {
        return oprTime;
    }

    public void setOprTime(Date oprTime) {
        this.oprTime = oprTime;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}