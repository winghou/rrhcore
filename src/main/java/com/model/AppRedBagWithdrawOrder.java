package com.model;

import java.util.Date;

/**
 * 红包提现订单表
 * @author Administrator
 *
 */
public class AppRedBagWithdrawOrder {

	private Integer id;
	private Integer apprId;
	private String bankCode;		//银行对应编码
	private String bankCard;		//银行卡号
	private String withdrawAmt;		//提现金额
	private String taxAmt;			//税额
	private String taxRate;			//税率
	private String loanAmt;			//放款金额
	private String loanStatus;		//放款状态  0：未放款，1：已放款，2：已结算
	private Date withdrawTime; 		//提现时间
	private Date loanTime;			//放款时间
	private Date settleTime;		//结算时间
	private String settleUrl;		//结算上传URL
	
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
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getWithdrawAmt() {
		return withdrawAmt;
	}
	public void setWithdrawAmt(String withdrawAmt) {
		this.withdrawAmt = withdrawAmt;
	}
	public String getTaxAmt() {
		return taxAmt;
	}
	public String getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}
	public void setTaxAmt(String taxAmt) {
		this.taxAmt = taxAmt;
	}
	public String getLoanAmt() {
		return loanAmt;
	}
	public void setLoanAmt(String loanAmt) {
		this.loanAmt = loanAmt;
	}
	public String getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
	public Date getWithdrawTime() {
		return withdrawTime;
	}
	public void setWithdrawTime(Date withdrawTime) {
		this.withdrawTime = withdrawTime;
	}
	public Date getLoanTime() {
		return loanTime;
	}
	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
	}
	public Date getSettleTime() {
		return settleTime;
	}
	public void setSettleTime(Date settleTime) {
		this.settleTime = settleTime;
	}
	public String getSettleUrl() {
		return settleUrl;
	}
	public void setSettleUrl(String settleUrl) {
		this.settleUrl = settleUrl;
	}
	
}
