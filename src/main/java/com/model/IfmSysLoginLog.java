package com.model;

import java.util.Date;

public class IfmSysLoginLog {
    private Integer id;

    private String userCode;

    private Date logindate;

    private String loginplace;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Date getLogindate() {
        return logindate;
    }

    public void setLogindate(Date logindate) {
        this.logindate = logindate;
    }

    public String getLoginplace() {
        return loginplace;
    }

    public void setLoginplace(String loginplace) {
        this.loginplace = loginplace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}