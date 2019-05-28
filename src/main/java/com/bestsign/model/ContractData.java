package com.bestsign.model;

/**
 * 
 * 项目名称：随心花分期服务协议数据 创建人：zhaheng 创建时间：2017年12月29日 下午3:03:24
 * 
 * @version
 */
public class ContractData {
	private String customName;
	private String idetityCard;
	private String phone;
	private String borrowPerion;// 贷款期限
	private String purposeName;// 贷款用途
	private String borrowAmt;
	private String contractNo;
	private String mchRate;
	private String loanDate;
	private String repayDate;
	private String shouldPayAmt;
	private String bankCardNo;
	private String serveRate;
	private String overdule3DayRate;
	private String overduleOver3DayRate;
	private String borrowOfficialSealsText;// 借款公章
	private String withholdOfficialSealsText;// 代扣公章

	public String getBorrowOfficialSealsText() {
		return borrowOfficialSealsText;
	}

	public void setBorrowOfficialSealsText(String borrowOfficialSealsText) {
		this.borrowOfficialSealsText = borrowOfficialSealsText;
	}

	public String getWithholdOfficialSealsText() {
		return withholdOfficialSealsText;
	}

	public void setWithholdOfficialSealsText(String withholdOfficialSealsText) {
		this.withholdOfficialSealsText = withholdOfficialSealsText;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public String getIdetityCard() {
		return idetityCard;
	}

	public void setIdetityCard(String idetityCard) {
		this.idetityCard = idetityCard;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBorrowPerion() {
		return borrowPerion;
	}

	public void setBorrowPerion(String borrowPerion) {
		this.borrowPerion = borrowPerion;
	}

	public String getPurposeName() {
		return purposeName;
	}

	public void setPurposeName(String purposeName) {
		this.purposeName = purposeName;
	}

	public String getBorrowAmt() {
		return borrowAmt;
	}

	public void setBorrowAmt(String borrowAmt) {
		this.borrowAmt = borrowAmt;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getMchRate() {
		return mchRate;
	}

	public void setMchRate(String mchRate) {
		this.mchRate = mchRate;
	}

	public String getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(String loanDate) {
		this.loanDate = loanDate;
	}

	public String getRepayDate() {
		return repayDate;
	}

	public void setRepayDate(String repayDate) {
		this.repayDate = repayDate;
	}

	public String getShouldPayAmt() {
		return shouldPayAmt;
	}

	public void setShouldPayAmt(String shouldPayAmt) {
		this.shouldPayAmt = shouldPayAmt;
	}

	public String getBankCardNo() {
		return bankCardNo;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getServeRate() {
		return serveRate;
	}

	public void setServeRate(String serveRate) {
		this.serveRate = serveRate;
	}

	public String getOverdule3DayRate() {
		return overdule3DayRate;
	}

	public void setOverdule3DayRate(String overdule3DayRate) {
		this.overdule3DayRate = overdule3DayRate;
	}

	public String getOverduleOver3DayRate() {
		return overduleOver3DayRate;
	}

	public void setOverduleOver3DayRate(String overduleOver3DayRate) {
		this.overduleOver3DayRate = overduleOver3DayRate;
	}
}
