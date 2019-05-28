package com.model;

/**
 * 用户抽奖次数统计
 * @author Administrator
 *
 */
public class AppPrizeAcount {
	
	private Integer id;
	private Integer apprId;
	private Integer totalLotteryNum;		//总抽奖次数
	private Integer remainingLotteryNum;	//剩余抽奖次数
	private Integer usedLotteryNum;			//已使用次数
	
	
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
	public Integer getTotalLotteryNum() {
		return totalLotteryNum;
	}
	public void setTotalLotteryNum(Integer totalLotteryNum) {
		this.totalLotteryNum = totalLotteryNum;
	}
	public Integer getRemainingLotteryNum() {
		return remainingLotteryNum;
	}
	public void setRemainingLotteryNum(Integer remainingLotteryNum) {
		this.remainingLotteryNum = remainingLotteryNum;
	}
	public Integer getUsedLotteryNum() {
		return usedLotteryNum;
	}
	public void setUsedLotteryNum(Integer usedLotteryNum) {
		this.usedLotteryNum = usedLotteryNum;
	}

}
