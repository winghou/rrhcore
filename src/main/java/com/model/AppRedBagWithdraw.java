package com.model;

public class AppRedBagWithdraw {

	private Integer id;
	private Integer apprId;
	private String useAmt;				// 可用红包
	private String totalMakeAmt;		// 累计赚取红包
	private String totalConsumeAmt;		// 累计提现红包
	private String monthlyUsedAmt;		// 当月提现红包
	
	
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
	public String getUseAmt() {
		return useAmt;
	}
	public void setUseAmt(String useAmt) {
		this.useAmt = useAmt;
	}
	public String getTotalMakeAmt() {
		return totalMakeAmt;
	}
	public void setTotalMakeAmt(String totalMakeAmt) {
		this.totalMakeAmt = totalMakeAmt;
	}
	public String getTotalConsumeAmt() {
		return totalConsumeAmt;
	}
	public void setTotalConsumeAmt(String totalConsumeAmt) {
		this.totalConsumeAmt = totalConsumeAmt;
	}
	public String getMonthlyUsedAmt() {
		return monthlyUsedAmt;
	}
	public void setMonthlyUsedAmt(String monthlyUsedAmt) {
		this.monthlyUsedAmt = monthlyUsedAmt;
	}
	
	
}
