package com.model;

import java.util.Date;

//优惠券
public class AppCoupon {

	private Integer id;		// id
	private String uses;  			// 用途
	private String description; 	// 描述
	private String type; 			// 优惠券类型
	private Date createDate; 		// 创建时间
	private Integer days;			// 优惠券有效天数
	private String status; 			// 优惠券是否显示给运营 0:显示，1：不显示（运营删除）


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUses() {
		return uses;
	}

	public void setUses(String uses) {
		this.uses = uses;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
