package com.model;

import java.util.Date;

public class AppWxSession {
    private Integer id;

    private Integer wxApprid;

    private String session;

    private Date lgTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWxApprid() {
        return wxApprid;
    }

    public void setWxApprid(Integer wxApprid) {
        this.wxApprid = wxApprid;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public Date getLgTime() {
        return lgTime;
    }

    public void setLgTime(Date lgTime) {
        this.lgTime = lgTime;
    }
}