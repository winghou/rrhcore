package com.model;

import java.util.Date;

public class AppLoanAppl {
	private Integer id;

	private String itemCode;

	private String status;

	private String loaningStatus;

	private Date createDate;

	private String prvince; // 用户身份 0：学生，1：上班族

	private String city; // 渠道来源

	private String comment;

	private String creditAmt;

	private Date updateTime;

	private String level;

	private Integer userId; // 关联ifm_sys_user表主键

	private String zhimaCreditScore; // 芝麻信用分

	private String zhimaIvsScore; // 反欺诈分数

	private String watchlistiiIsMatched; // 是否是行业关注名单
	
	private String inviteCode;    //邀请码
	
	private String inviterInviteCode;   //邀请人邀请码
	
	private Date authenTime;  //资料认证时间
	
	private String operatorStatus; // 运营商状态 0：正常，1：重新需要认证（只认证运营商）
	
	private String zhimaStatus; //芝麻分状态 0：正常，1：需要重新认证（只认证芝麻分）
	
	private String baseInfoStatus; //风控拒绝基本信息状态 0：正常，1：需要重新认证（只认证基本信息）
	
	private String contactInfoStatus; //风控拒绝联系人状态 0：正常，1：需要重新认证（只认证联系人）
	
	private Integer accountStatus; //账号状态
	
	private Integer abnormalNumber;  //账户异常次数
	
	private Date upgradeAccountTime;  //账户升级时间
	
	private Date recoverAccountTime;  //账户恢复时间

	private String ssqApplyStatus;  //上上签用户申请状态

	
	/**
	 * @return the upgradeAccountTime
	 */
	public Date getUpgradeAccountTime() {
		return upgradeAccountTime;
	}

	/**
	 * @param upgradeAccountTime the upgradeAccountTime to set
	 */
	public void setUpgradeAccountTime(Date upgradeAccountTime) {
		this.upgradeAccountTime = upgradeAccountTime;
	}

	/**
	 * @return the recoverAccountTime
	 */
	public Date getRecoverAccountTime() {
		return recoverAccountTime;
	}

	/**
	 * @param recoverAccountTime the recoverAccountTime to set
	 */
	public void setRecoverAccountTime(Date recoverAccountTime) {
		this.recoverAccountTime = recoverAccountTime;
	}

	/**
	 * @return the accountStatus
	 */
	public Integer getAccountStatus() {
		return accountStatus;
	}

	/**
	 * @param accountStatus the accountStatus to set
	 */
	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	/**
	 * @return the abnormalNumber
	 */
	public Integer getAbnormalNumber() {
		return abnormalNumber;
	}

	/**
	 * @param abnormalNumber the abnormalNumber to set
	 */
	public void setAbnormalNumber(Integer abnormalNumber) {
		this.abnormalNumber = abnormalNumber;
	}

	public String getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(String operatorStatus) {
		this.operatorStatus = operatorStatus;
	}

	public String getZhimaStatus() {
		return zhimaStatus;
	}

	public void setZhimaStatus(String zhimaStatus) {
		this.zhimaStatus = zhimaStatus;
	}

	public String getBaseInfoStatus() {
		return baseInfoStatus;
	}

	public void setBaseInfoStatus(String baseInfoStatus) {
		this.baseInfoStatus = baseInfoStatus;
	}

	public String getContactInfoStatus() {
		return contactInfoStatus;
	}

	public void setContactInfoStatus(String contactInfoStatus) {
		this.contactInfoStatus = contactInfoStatus;
	}

	public Date getAuthenTime() {
		return authenTime;
	}

	public void setAuthenTime(Date authenTime) {
		this.authenTime = authenTime;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public String getWatchlistiiIsMatched() {
		return watchlistiiIsMatched;
	}

	public void setWatchlistiiIsMatched(String watchlistiiIsMatched) {
		this.watchlistiiIsMatched = watchlistiiIsMatched;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLoaningStatus() {
		return loaningStatus;
	}

	public void setLoaningStatus(String loaningStatus) {
		this.loaningStatus = loaningStatus;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getPrvince() {
		return prvince;
	}

	public void setPrvince(String prvince) {
		this.prvince = prvince;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(String creditAmt) {
		this.creditAmt = creditAmt;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getInviterInviteCode() {
		return inviterInviteCode;
	}

	public void setInviterInviteCode(String inviterInviteCode) {
		this.inviterInviteCode = inviterInviteCode;
	}

	public String getSsqApplyStatus() {
		return ssqApplyStatus;
	}

	public void setSsqApplyStatus(String ssqApplyStatus) {
		this.ssqApplyStatus = ssqApplyStatus;
	}
	
	
	
}