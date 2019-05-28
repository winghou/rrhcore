package com.model;

import java.util.Date;

public class IfmYxtSign {
    private int id;
    private String merchOrderNo ;//'商户订单号'
    private String merchContractNo ;//'商户签约合同号'
    private String signStatus ;// '签约状态'
    private Date receiptTime ;//'回执时间'
    private String name ;// '姓名'
    private String bankCardNo ;// '银行卡号'
    private String bankName ;// '银行名称'
    private String bankCardType ;// '银行卡类型'
    private String bankCode ;// '银行编码'
    private String errorCode ;// '错误码'
    private String description ;// '签约描述'
    private Date notifyTime ;//'时间'
    private String deductStatus;
    private String apprId; //订单号
    private Integer deductCount;
    /**签约类型*/
    private String signType;
    private Date deductDateTime;
    
    public Date getDeductDateTime() {
		return deductDateTime;
	}
	public void setDeductDateTime(Date deductDateTime) {
		this.deductDateTime = deductDateTime;
	}
    
   
	public Integer getDeductCount() {
		return deductCount;
	}
	public void setDeductCount(Integer deductCount) {
		this.deductCount = deductCount;
	}
	public String getApprId() {
		return apprId;
	}
	public void setApprId(String apprId) {
		this.apprId = apprId;
	}
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMerchOrderNo() {
        return merchOrderNo;
    }
    public void setMerchOrderNo(String merchOrderNo) {
        this.merchOrderNo = merchOrderNo;
    }
    public String getMerchContractNo() {
        return merchContractNo;
    }
    public void setMerchContractNo(String merchContractNo) {
        this.merchContractNo = merchContractNo;
    }
    public String getSignStatus() {
        return signStatus;
    }
    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }
    public Date getReceiptTime() {
        return receiptTime;
    }
    public void setReceiptTime(Date receiptTime) {
        this.receiptTime = receiptTime;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBankCardNo() {
        return bankCardNo;
    }
    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getBankCardType() {
        return bankCardType;
    }
    public void setBankCardType(String bankCardType) {
        this.bankCardType = bankCardType;
    }
    public String getBankCode() {
        return bankCode;
    }
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getNotifyTime() {
        return notifyTime;
    }
    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }
    public String getDeductStatus() {
        return deductStatus;
    }
    public void setDeductStatus(String deductStatus) {
        this.deductStatus = deductStatus;
    }
    public String getSignType() {
        return signType;
    }
    public void setSignType(String signType) {
        this.signType = signType;
    }
    
}
