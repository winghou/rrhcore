package com.model;

import java.util.Date;

/**
 * 提额记录
 * @author Administrator
 *
 */
public class AppIncreaseCreditRecord {

	private Integer id;
	private Integer apprId;
	private Integer increasedCredit;	//提现金额
	private String type;				//体现类型（0：抽奖，1：其他）
	private Date createTime;			//提现时间
	private String status;				//预留字段
	
	
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
	public Integer getIncreasedCredit() {
		return increasedCredit;
	}
	public void setIncreasedCredit(Integer increasedCredit) {
		this.increasedCredit = increasedCredit;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
