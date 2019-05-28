package com.model;

import java.util.Date;

/**
 * 用户中奖记录
 * @author Administrator
 *
 */
public class AppPrizeRecord {

	private Integer id ;
	private Integer apprId;
	private Integer prizeId;
	private Date createDate; //中奖时间
	
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
	public Integer getPrizeId() {
		return prizeId;
	}
	public void setPrizeId(Integer prizeId) {
		this.prizeId = prizeId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
