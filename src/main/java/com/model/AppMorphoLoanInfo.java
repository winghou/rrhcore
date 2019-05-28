package com.model;

import java.util.Date;

public class AppMorphoLoanInfo {

	private Integer id;

    private Integer apprId;
    
    private String loanInfo;
    
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

	public String getLoanInfo() {
		return loanInfo;
	}

	public void setLoanInfo(String loanInfo) {
		this.loanInfo = loanInfo;
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
