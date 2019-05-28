package com.model;

import java.util.Date;

public class APPCredit {
    private Integer id;

    private Double creditAmt;

    private Double useAmt;

    private Double totalConsumeAmt;

    private Integer apprId;
    
    private Double wait_pay_amt;//累计消费金额
    
    private Double total_add_amt;
    
    private Date creditTime;
    
    
    
    public Double getTotal_add_amt() {
		return total_add_amt;
	}

	public void setTotal_add_amt(Double total_add_amt) {
		this.total_add_amt = total_add_amt;
	}

	public Date getCreditTime() {
		return creditTime;
	}

	public void setCreditTime(Date creditTime) {
		this.creditTime = creditTime;
	}

	public Double getWait_pay_amt() {
		return wait_pay_amt;
	}

	public void setWait_pay_amt(Double wait_pay_amt) {
		this.wait_pay_amt = wait_pay_amt;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getCreditAmt() {
        return creditAmt;
    }

    public void setCreditAmt(Double creditAmt) {
        this.creditAmt = creditAmt;
    }

    public Double getUseAmt() {
        return useAmt;
    }

    public void setUseAmt(Double useAmt) {
        this.useAmt = useAmt;
    }

    public Double getTotalConsumeAmt() {
        return totalConsumeAmt;
    }

    public void setTotalConsumeAmt(Double totalConsumeAmt) {
        this.totalConsumeAmt = totalConsumeAmt;
    }

    public Integer getApprId() {
        return apprId;
    }

    public void setApprId(Integer apprId) {
        this.apprId = apprId;
    }
}