package com.model;

import java.util.Date;

public class AppRedBagWithdrawLog {

	private Integer id;
	private Integer apprId;
	private String title;			// 红包明细描述
	private String amt;				// 红包提现/返现金额
	private String type;			// 类型  0：返现，1：提现
	private Date createDate;
	private Integer activeTaskId;	// 关联活动
	private Integer redBagId;		// 关联红包
	
	
	public Integer getRedBagId() {
		return redBagId;
	}
	public void setRedBagId(Integer redBagId) {
		this.redBagId = redBagId;
	}
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Integer getActiveTaskId() {
		return activeTaskId;
	}
	public void setActiveTaskId(Integer activeTaskId) {
		this.activeTaskId = activeTaskId;
	}
	
}
