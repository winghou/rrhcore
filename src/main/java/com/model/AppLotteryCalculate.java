package com.model;

/**
 * 抽奖次数统计
 * @author Administrator
 *
 */
public class AppLotteryCalculate {

	private Integer id;
	private Integer apprId;
	private Integer totalNum;			//总抽奖次数
	private Integer remainingNum;		//剩余抽奖次数
	private Integer consumedNum;		//已用抽奖次数
	private String status;				//备用字段
	
	
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
	public Integer getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}
	public Integer getRemainingNum() {
		return remainingNum;
	}
	public void setRemainingNum(Integer remainingNum) {
		this.remainingNum = remainingNum;
	}
	public Integer getConsumedNum() {
		return consumedNum;
	}
	public void setConsumedNum(Integer consumedNum) {
		this.consumedNum = consumedNum;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
