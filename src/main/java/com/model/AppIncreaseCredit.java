package com.model;

/**
 * 提额表
 * @author Administrator
 *
 */
public class AppIncreaseCredit {

	private Integer id;
	private Integer apprId;
	private Integer totalIncreasedCredit;			//总提现额度
	private Integer remainingIncreasedCredit;		//剩余提现额度
	private Integer consumedIncreasedCredit;		//已使用额度
	private String type;							//提额类型（0：抽奖，1：其他）
	private String status;							//预留字段
	
	
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
	public Integer getTotalIncreasedCredit() {
		return totalIncreasedCredit;
	}
	public void setTotalIncreasedCredit(Integer totalIncreasedCredit) {
		this.totalIncreasedCredit = totalIncreasedCredit;
	}
	public Integer getRemainingIncreasedCredit() {
		return remainingIncreasedCredit;
	}
	public void setRemainingIncreasedCredit(Integer remainingIncreasedCredit) {
		this.remainingIncreasedCredit = remainingIncreasedCredit;
	}
	public Integer getConsumedIncreasedCredit() {
		return consumedIncreasedCredit;
	}
	public void setConsumedIncreasedCredit(Integer consumedIncreasedCredit) {
		this.consumedIncreasedCredit = consumedIncreasedCredit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
