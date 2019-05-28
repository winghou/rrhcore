package com.model;

import java.util.Date;

public class AppActivityTask {

	private Integer id;
	private String money;			// 活动金额1
	private String money2;			// 活动金额2
	private String money3;			// 活动金额3
	private String type;			// 类型  0：红包
	private Date create_time;		// 活动创建时间
	private Date start_time;		// 活动开始时间
	private Date end_time;			// 活动结束时间
	private String description;		// 描述
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getMoney2() {
		return money2;
	}
	public void setMoney2(String money2) {
		this.money2 = money2;
	}
	public String getMoney3() {
		return money3;
	}
	public void setMoney3(String money3) {
		this.money3 = money3;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getStart_time() {
		return start_time;
	}
	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}
	public Date getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
