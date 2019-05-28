package com.model;

/**
 * 等级表
 * @author Administrator
 *
 */
public class AppLevel {
	
	private Integer id;
	private String level;		//用户等级
	private Integer minCredit;	//等级最小额度
	private Integer maxCredit;	//等级最大额度
	private String mchRate;		//借款利率
	private String fwfRate;		//服务费利率
	private String description;		//描述
	private String status;		//预留字段
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public Integer getMinCredit() {
		return minCredit;
	}
	public void setMinCredit(Integer minCredit) {
		this.minCredit = minCredit;
	}
	public Integer getMaxCredit() {
		return maxCredit;
	}
	public void setMaxCredit(Integer maxCredit) {
		this.maxCredit = maxCredit;
	}
	public String getMchRate() {
		return mchRate;
	}
	public void setMchRate(String mchRate) {
		this.mchRate = mchRate;
	}
	public String getFwfRate() {
		return fwfRate;
	}
	public void setFwfRate(String fwfRate) {
		this.fwfRate = fwfRate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
