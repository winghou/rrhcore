package com.model;

/**
 * 中奖用户填写中奖信息
 * @author Administrator
 *
 */
public class AppPrizePersonInfo {
	
	private Integer id;
	private Integer apprId;
	private String customName;		//姓名
	private String customPhone;		//手机号
	private String customAddress;	//地址
	private String type;			//活动类型 1：iphoneX首发日抽奖活动
	
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
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getCustomPhone() {
		return customPhone;
	}
	public void setCustomPhone(String customPhone) {
		this.customPhone = customPhone;
	}
	public String getCustomAddress() {
		return customAddress;
	}
	public void setCustomAddress(String customAddress) {
		this.customAddress = customAddress;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
