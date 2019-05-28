package com.model;

import java.util.Date;

public class AppAccountInfo {

	private Integer id;
	private Integer apprId;
	private Integer settleCount;			//结清次数
	private Integer currentOverduleCount;	//当前逾期次数
	private Integer totalOverduleCount;		//总逾期次数
	private Date lastPayTime;				//最后一次还款时间
	private Integer couponCount;			//
	private Date couponTime;				//
	private Integer loanCount;				//放款次数
	private Date borrowTime;				//最后借款时间
	private String grade;					//用户等级
	private Date gradeDate;					//用户等级改变时间
	
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
	public Integer getSettleCount() {
		return settleCount;
	}
	public void setSettleCount(Integer settleCount) {
		this.settleCount = settleCount;
	}
	public Integer getCurrentOverduleCount() {
		return currentOverduleCount;
	}
	public void setCurrentOverduleCount(Integer currentOverduleCount) {
		this.currentOverduleCount = currentOverduleCount;
	}
	public Integer getTotalOverduleCount() {
		return totalOverduleCount;
	}
	public void setTotalOverduleCount(Integer totalOverduleCount) {
		this.totalOverduleCount = totalOverduleCount;
	}
	public Date getLastPayTime() {
		return lastPayTime;
	}
	public void setLastPayTime(Date lastPayTime) {
		this.lastPayTime = lastPayTime;
	}
	public Integer getCouponCount() {
		return couponCount;
	}
	public void setCouponCount(Integer couponCount) {
		this.couponCount = couponCount;
	}
	public Date getCouponTime() {
		return couponTime;
	}
	public void setCouponTime(Date couponTime) {
		this.couponTime = couponTime;
	}
	public Integer getLoanCount() {
		return loanCount;
	}
	public void setLoanCount(Integer loanCount) {
		this.loanCount = loanCount;
	}
	public Date getBorrowTime() {
		return borrowTime;
	}
	public void setBorrowTime(Date borrowTime) {
		this.borrowTime = borrowTime;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Date getGradeDate() {
		return gradeDate;
	}
	public void setGradeDate(Date gradeDate) {
		this.gradeDate = gradeDate;
	}

}
