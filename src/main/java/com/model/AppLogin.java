package com.model;

import java.util.Date;

public class AppLogin {
    private Integer lgnid;

    private String userCode;

    private String password;

    private String hisPassword;

    private Date registerDate;

    private String style;

    private String accountType;

    public Integer getLgnid() {
        return lgnid;
    }

    public void setLgnid(Integer lgnid) {
        this.lgnid = lgnid;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHisPassword() {
        return hisPassword;
    }

    public void setHisPassword(String hisPassword) {
        this.hisPassword = hisPassword;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}