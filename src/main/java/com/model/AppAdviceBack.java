package com.model;

import java.util.Date;

public class AppAdviceBack {
    private Integer id;

    private Integer userid;

    private String content;

    private String type;

    private Date sumbitTime;
    
    private String contactWay;  //联系方式

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getSumbitTime() {
        return sumbitTime;
    }

    public void setSumbitTime(Date sumbitTime) {
        this.sumbitTime = sumbitTime;
    }

	public String getContactWay() {
		return contactWay;
	}

	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}
    
}