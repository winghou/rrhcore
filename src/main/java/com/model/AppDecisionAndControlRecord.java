package com.model;

import java.util.Date;

/**
 * 决策引擎和风控授信及审核记录表
 * @author Administrator
 *
 */
public class AppDecisionAndControlRecord {
	
	private Integer id;
	private String orderId;
	private Integer apprId;
	private Integer withdrawId;
	private Date createTime;			//创建时间
	private Date engineFinishTime;		//决策引擎完成时间
	private String engineStatus;		//决策引擎状态（0：拒绝，1：通过）
	private Date controlFinishTime;		//风控完成时间
	private String controlStatus;		//风控状态（0：拒绝，1：通过）
	private String score;				//决策引擎判定分数
	private String level;				//决策引擎判定等级
	private String credit;				//决策引擎判定授信额度
	private Integer wfId;
	private Integer wfmLogId;
	private String description;
	private String credit_score;       //芝麻信用分
	private String ivs_score;       //芝麻反欺诈分
	private String watchlist_matched;       //芝麻行业关注名单
	private String open_id_status;       //芝麻openid的状态，是否已经失效
	
	
	public Integer getWfmLogId() {
		return wfmLogId;
	}
	public void setWfmLogId(Integer wfmLogId) {
		this.wfmLogId = wfmLogId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEngineFinishTime() {
		return engineFinishTime;
	}
	public void setEngineFinishTime(Date engineFinishTime) {
		this.engineFinishTime = engineFinishTime;
	}
	public String getEngineStatus() {
		return engineStatus;
	}
	public void setEngineStatus(String engineStatus) {
		this.engineStatus = engineStatus;
	}
	public Date getControlFinishTime() {
		return controlFinishTime;
	}
	public void setControlFinishTime(Date controlFinishTime) {
		this.controlFinishTime = controlFinishTime;
	}
	public String getControlStatus() {
		return controlStatus;
	}
	public void setControlStatus(String controlStatus) {
		this.controlStatus = controlStatus;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public Integer getWfId() {
		return wfId;
	}
	public void setWfId(Integer wfId) {
		this.wfId = wfId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getCredit_score() {
		return credit_score;
	}

	public void setCredit_score(String credit_score) {
		this.credit_score = credit_score;
	}

	public String getIvs_score() {
		return ivs_score;
	}

	public void setIvs_score(String ivs_score) {
		this.ivs_score = ivs_score;
	}

	public String getWatchlist_matched() {
		return watchlist_matched;
	}

	public void setWatchlist_matched(String watchlist_matched) {
		this.watchlist_matched = watchlist_matched;
	}

	public String getOpen_id_status() {
		return open_id_status;
	}

	public void setOpen_id_status(String open_id_status) {
		this.open_id_status = open_id_status;
	}
}
