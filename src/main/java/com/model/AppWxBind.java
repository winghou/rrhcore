package com.model;

import java.util.Date;

public class AppWxBind {
    private Integer id;

    private Integer apprId;

    private String phone;

    private String openId;

    private Date bindTime;

    private Date creatTime;

    private String uuid;
    
    private Date pushWxsmsTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApprId() {
        return apprId;
    }

    public void setApprId(Integer apprId) {
        this.apprId = apprId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	public Date getPushWxsmsTime() {
		return pushWxsmsTime;
	}

	public void setPushWxsmsTime(Date pushWxsmsTime) {
		this.pushWxsmsTime = pushWxsmsTime;
	}



}