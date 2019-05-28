package com.model;

public class AppWithDrawStatus {
	
	private Integer id;
	private Integer withdrawId;
	private String status;
	private Integer apprId;
	private Integer wfmLogId;
	private Integer sendCount;
	private String type;			// 类型 0：授信，1：提现
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getWithdrawId() {
		return withdrawId;
	}
	public void setWithdrawId(Integer withdrawId) {
		this.withdrawId = withdrawId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getApprId() {
		return apprId;
	}
	public void setApprId(Integer apprId) {
		this.apprId = apprId;
	}
	public Integer getWfmLogId() {
		return wfmLogId;
	}
	public void setWfmLogId(Integer wfmLogId) {
		this.wfmLogId = wfmLogId;
	}
	public Integer getSendCount() {
		return sendCount;
	}
	public void setSendCount(Integer sendCount) {
		this.sendCount = sendCount;
	}
	
	
	
}
