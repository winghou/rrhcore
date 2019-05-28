package com.model;

import java.util.Date;

public class AppMorphoBlackList {

	private Integer id;

    private Integer apprId;
    
    private String blackList;
    
    private Date createDate;
    
    private Date updateDate;
    
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

	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
