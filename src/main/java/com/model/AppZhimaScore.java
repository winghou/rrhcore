package com.model;

import java.util.Date;

public class AppZhimaScore {
    private Integer id;

    private String zhimaCreditScore;

    private String zhimaIvsScore;

    private String ivsDetail;

    private String watchlistiiIsMatched;

    private String watchlistiiDetail;

    private Integer userId;

    private String openId;

    private Date auticDate;
    
    private String status;
    
    
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZhimaCreditScore() {
        return zhimaCreditScore;
    }

    public void setZhimaCreditScore(String zhimaCreditScore) {
        this.zhimaCreditScore = zhimaCreditScore;
    }

    public String getZhimaIvsScore() {
        return zhimaIvsScore;
    }

    public void setZhimaIvsScore(String zhimaIvsScore) {
        this.zhimaIvsScore = zhimaIvsScore;
    }

    public String getIvsDetail() {
        return ivsDetail;
    }

    public void setIvsDetail(String ivsDetail) {
        this.ivsDetail = ivsDetail;
    }

    public String getWatchlistiiIsMatched() {
        return watchlistiiIsMatched;
    }

    public void setWatchlistiiIsMatched(String watchlistiiIsMatched) {
        this.watchlistiiIsMatched = watchlistiiIsMatched;
    }

    public String getWatchlistiiDetail() {
        return watchlistiiDetail;
    }

    public void setWatchlistiiDetail(String watchlistiiDetail) {
        this.watchlistiiDetail = watchlistiiDetail;
    }


    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Date getAuticDate() {
        return auticDate;
    }

    public void setAuticDate(Date auticDate) {
        this.auticDate = auticDate;
    }
}