package com.model;

import java.util.Date;

public class AppzfbOrderList {
    private Integer orderId;

    private String widoutTradeNo;

    private String widtotalFee;

    private Date createDate;

    private String widoutSubject;

    private String repayPlanId;

    private String zfbOrderNo;

    private Date zfbPayDate;

    private String buyerEmail;
    
    private String paySource;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getWidoutTradeNo() {
        return widoutTradeNo;
    }

    public void setWidoutTradeNo(String widoutTradeNo) {
        this.widoutTradeNo = widoutTradeNo;
    }

    public String getWidtotalFee() {
        return widtotalFee;
    }

    public void setWidtotalFee(String widtotalFee) {
        this.widtotalFee = widtotalFee;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getWidoutSubject() {
        return widoutSubject;
    }

    public void setWidoutSubject(String widoutSubject) {
        this.widoutSubject = widoutSubject;
    }

    public String getRepayPlanId() {
        return repayPlanId;
    }

    public void setRepayPlanId(String repayPlanId) {
        this.repayPlanId = repayPlanId;
    }

    public String getZfbOrderNo() {
        return zfbOrderNo;
    }

    public void setZfbOrderNo(String zfbOrderNo) {
        this.zfbOrderNo = zfbOrderNo;
    }

    public Date getZfbPayDate() {
        return zfbPayDate;
    }

    public void setZfbPayDate(Date zfbPayDate) {
        this.zfbPayDate = zfbPayDate;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

	public String getPaySource() {
		return paySource;
	}

	public void setPaySource(String paySource) {
		this.paySource = paySource;
	}
    
    
}