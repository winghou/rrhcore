package com.model;

import java.util.Date;

public class AppSysSession {

	private Integer id;

    private Integer userId;

    private String token;

    private Date startDate;
    
    private Integer bank_count;

    
    public Integer getBank_count() {
		return bank_count;
	}

	public void setBank_count(Integer bank_count) {
		this.bank_count = bank_count;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
		this.userId = userId;
	}

    public Integer getUserId() {
		return userId;
	}

	public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
