package com.model;

import java.util.Date;

public class IfmWithhold {
    private int id;
    /**
     * 商户订单号
     */
    private String merchOrderNo;
    /**
     * 商户签约合同号
     */
    private String merchSignOrderNo;
    /**
     * 回款状态
     */
    private String serviceStatus;
    /**
     * 代扣金额
     */
    private Double deductAmount;
    /**
     * 回款金额
     */
    private String transAmount;
    /**
     * 实际还款时间
     */
    private Date realRepayTime;
    /**
     * 回执时间
     */
    private Date receiptTime;
    /**
     * 姓名
     */
    private String name;
    /**
     * 银行卡号
     */
    private String bankCardNo;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 签约描述
     */
    private String description;
    /**
     * 描述
     */
    private String resultMessage;
    /**
     * 异步消息通知时间
     */
    private Date notifyTime;
    /**
     * 还款计划表id
     */
    private String plan_id;
    
    private String appr_id;
    /**
     * 创建时间
     */
    private Date create_date;
    

    public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public Double getDeductAmount() {
		return deductAmount;
	}

	public void setDeductAmount(Double deductAmount) {
		this.deductAmount = deductAmount;
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

    public String getServiceStatus() {
	return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
	this.serviceStatus = serviceStatus;
    }

    public String getTransAmount() {
	return transAmount;
    }

    public void setTransAmount(String transAmount) {
	this.transAmount = transAmount;
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

    public String getResultMessage() {
	return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
	this.resultMessage = resultMessage;
    }

    public Date getNotifyTime() {
	return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
	this.notifyTime = notifyTime;
    }


    public Date getRealRepayTime() {
        return realRepayTime;
    }

    public void setRealRepayTime(Date realRepayTime) {
        this.realRepayTime = realRepayTime;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMerchSignOrderNo() {
        return merchSignOrderNo;
    }

    public void setMerchSignOrderNo(String merchSignOrderNo) {
        this.merchSignOrderNo = merchSignOrderNo;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getAppr_id() {
        return appr_id;
    }

    public void setAppr_id(String appr_id) {
        this.appr_id = appr_id;
    }
    
    
}
