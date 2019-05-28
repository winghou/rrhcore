package com.model;

import java.util.Date;

/**
 * 上上签
 * @author Administrator
 *
 */
public class AppSSQContract {

	private Integer id;
	
	private Integer apprId;
	
	private Integer withdrawId;
	
	private String contractId;	// 上上签合同号
	
	private String contractUrl; // 上上签合同地址
	
	private Date createTime;
	
	private Date viewTime;		// 查看合同时间
	
	private String type;		// 0:借款合同，1：代扣协议
	
	private String downloadUrl;	// 下载地址
	
	private String signStatus;	// 签署状态（0：未签署，1：已签署，2：已锁定（不能再签署，可公正））

	public String getSignStatus() {
		return signStatus;
	}

	public void setSignStatus(String signStatus) {
		this.signStatus = signStatus;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

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

	public Integer getApprId() {
		return apprId;
	}

	public void setApprId(Integer apprId) {
		this.apprId = apprId;
	}

	public Integer getWithdrawId() {
		return withdrawId;
	}

	public void setWithdrawId(Integer withdrawId) {
		this.withdrawId = withdrawId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getContractUrl() {
		return contractUrl;
	}

	public void setContractUrl(String contractUrl) {
		this.contractUrl = contractUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getViewTime() {
		return viewTime;
	}

	public void setViewTime(Date viewTime) {
		this.viewTime = viewTime;
	}
	
}
