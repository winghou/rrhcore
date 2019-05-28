package com.model;

import java.util.Date;

public class APPWithDrawAppl {
    private Integer id;

    private Integer apprId;

    private Date borrowTime;

    private String borrowAmt;

    private String borrowPerions;
    
    private String borrowDays;

    private String borrowUse;

    private String actualAmt;

    private String commissionAmt;
    
    private String month_pay;
    
    private String capital_and_interest; 	//本息和
    
    private String coupon_amt;			 	//已优惠金额

    private String contractCode;

    private String contractTempid;

    private String status;

    private String loanStatus;

    private Date confirmDate;

    private Date loanDate;

    private String cooperation;

    private String tactUuid;

    private String isFullScale;

    private Date fullDate;
     
    private Date settle_date;
     
    private String settle_amt; 
    
    private String nhl;
    
    private String audio_url;
    
    private String contract_no;
    
    private String couponId; //关联优惠券ID
    
    private String fwfRate; //服务费率
    
    private String purpose; //借款用途
    
    private String loanSource; //'借款来源标记 0或null：悦才，1：攸县
    
    private String insurance_type; //保险类型
    
    private String protocol_type;  //协议类型
    
    private String dayRate; //每天的利率
    
    private String creditxScore; //氪信分
    
    private String consumeType; //消费订单类型

    
	public String getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(String consumeType) {
		this.consumeType = consumeType;
	}

	public String getCreditxScore() {
		return creditxScore;
	}

	public void setCreditxScore(String creditxScore) {
		this.creditxScore = creditxScore;
	}

	public String getDayRate() {
		return dayRate;
	}

	public void setDayRate(String dayRate) {
		this.dayRate = dayRate;
	}
    
	public String getProtocol_type() {
		return protocol_type;
	}

	public void setProtocol_type(String protocol_type) {
		this.protocol_type = protocol_type;
	}

	public String getInsurance_type() {
		return insurance_type;
	}

	public void setInsurance_type(String insurance_type) {
		this.insurance_type = insurance_type;
	}

	public String getLoanSource() {
		return loanSource;
	}

	public void setLoanSource(String loanSource) {
		this.loanSource = loanSource;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getFwfRate() {
		return fwfRate;
	}

	public void setFwfRate(String fwfRate) {
		this.fwfRate = fwfRate;
	}

	public String getCapital_and_interest() {
		return capital_and_interest;
	}

	public void setCapital_and_interest(String capital_and_interest) {
		this.capital_and_interest = capital_and_interest;
	}

	public String getCoupon_amt() {
		return coupon_amt;
	}

	public void setCoupon_amt(String coupon_amt) {
		this.coupon_amt = coupon_amt;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public String getContract_no() {
		return contract_no;
	}

	public void setContract_no(String contract_no) {
		this.contract_no = contract_no;
	}

	public String getAudio_url() {
		return audio_url;
	}

	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}

	public String getNhl() {
		return nhl;
	}

	public void setNhl(String nhl) {
		this.nhl = nhl;
	}

	public String getMonth_pay() {
		return month_pay;
	}

	public void setMonth_pay(String month_pay) {
		this.month_pay = month_pay;
	}

	public Date getSettle_date() {
		return settle_date;
	}

	public void setSettle_date(Date settle_date) {
		this.settle_date = settle_date;
	}

	public String getSettle_amt() {
		return settle_amt;
	}

	public void setSettle_amt(String settle_amt) {
		this.settle_amt = settle_amt;
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

    public Date getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Date borrowTime) {
        this.borrowTime = borrowTime;
    }

    public String getBorrowAmt() {
        return borrowAmt;
    }

    public void setBorrowAmt(String borrowAmt) {
        this.borrowAmt = borrowAmt;
    }
    

	public String getBorrowPerions() {
        return borrowPerions;
    }

    public void setBorrowPerions(String borrowPerions) {
        this.borrowPerions = borrowPerions;
    }

    public String getBorrowUse() {
        return borrowUse;
    }

    public void setBorrowUse(String borrowUse) {
        this.borrowUse = borrowUse;
    }

    public String getActualAmt() {
        return actualAmt;
    }

    public void setActualAmt(String actualAmt) {
        this.actualAmt = actualAmt;
    }

    public String getCommissionAmt() {
        return commissionAmt;
    }

    public void setCommissionAmt(String commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public String getContractTempid() {
        return contractTempid;
    }

    public void setContractTempid(String contractTempid) {
        this.contractTempid = contractTempid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public Date getConfirmDate() {
        return confirmDate;
    }

    public void setConfirmDate(Date confirmDate) {
        this.confirmDate = confirmDate;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public String getCooperation() {
        return cooperation;
    }

    public void setCooperation(String cooperation) {
        this.cooperation = cooperation;
    }

    public String getTactUuid() {
        return tactUuid;
    }

    public void setTactUuid(String tactUuid) {
        this.tactUuid = tactUuid;
    }

    public String getIsFullScale() {
        return isFullScale;
    }

    public void setIsFullScale(String isFullScale) {
        this.isFullScale = isFullScale;
    }

    public Date getFullDate() {
        return fullDate;
    }

    public void setFullDate(Date fullDate) {
        this.fullDate = fullDate;
    }

	public String getBorrowDays() {
		return borrowDays;
	}

	public void setBorrowDays(String borrowDays) {
		this.borrowDays = borrowDays;
	}
    
    
}