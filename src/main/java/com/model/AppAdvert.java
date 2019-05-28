package com.model;

public class AppAdvert {
    private Integer id;

    private String url;
    
    private String picJumpUrl;
    
    private String module;// 广告轮播的模块
    
    private String messageId;
    
    
    
    
    
    public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getPicJumpUrl() {
		return picJumpUrl;
	}

	public void setPicJumpUrl(String picJumpUrl) {
		this.picJumpUrl = picJumpUrl;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}