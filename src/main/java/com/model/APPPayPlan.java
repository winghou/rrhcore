package com.model;

import java.util.Date;

public class APPPayPlan {
    private Integer id;

    private Integer withdrawId;
    
    private Integer apprId;

    private Date repayDate;
    
    private Double prinPay;
    
    private Double interPay;

    private Double monthPay;

    private String status;

    private String isCompent;

    private Date payDate;

    private Integer curperiods;

    private String loanStatus;

    private Integer days;

    private Double fxje;
    
    private Double ht_amt;
    
    private Double reducAmt; //优惠金额
    
    

    public Double getReducAmt() {
		return reducAmt;
	}

	public void setReducAmt(Double reducAmt) {
		this.reducAmt = reducAmt;
	}

	public Double getHt_amt() {
		return ht_amt;
	}

	public void setHt_amt(Double ht_amt) {
		this.ht_amt = ht_amt;
	}

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

	public Integer getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Integer withdrawId) {
        this.withdrawId = withdrawId;
    }

    public Date getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(Date repayDate) {
        this.repayDate = repayDate;
    }
    

    public Double getPrinPay() {
		return prinPay;
	}

	public void setPrinPay(Double prinPay) {
		this.prinPay = prinPay;
	}

	public Double getInterPay() {
		return interPay;
	}

	public void setInterPay(Double interPay) {
		this.interPay = interPay;
	}

	public Double getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(Double monthPay) {
        this.monthPay = monthPay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsCompent() {
        return isCompent;
    }

    public void setIsCompent(String isCompent) {
        this.isCompent = isCompent;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Integer getCurperiods() {
        return curperiods;
    }

    public void setCurperiods(Integer curperiods) {
        this.curperiods = curperiods;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Double getFxje() {
        return fxje;
    }

    public void setFxje(Double fxje) {
        this.fxje = fxje;
    }
}