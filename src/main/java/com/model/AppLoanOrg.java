package com.model;

import java.util.Date;

public class AppLoanOrg {
    private Integer loanOrgId;

    private String loanOrgName;

    private String orgPicUrl;
    
    private String orgPicUrlLog;

    private String orgInterfaceUrl;

    private String orgDesc;

    private String status;

    private Date createDate;
    
    public String getOrgPicUrlLog() {
		return orgPicUrlLog;
	}

	public void setOrgPicUrlLog(String orgPicUrlLog) {
		this.orgPicUrlLog = orgPicUrlLog;
	}

	public Integer getLoanOrgId() {
        return loanOrgId;
    }

    public void setLoanOrgId(Integer loanOrgId) {
        this.loanOrgId = loanOrgId;
    }

    public String getLoanOrgName() {
        return loanOrgName;
    }

    public void setLoanOrgName(String loanOrgName) {
        this.loanOrgName = loanOrgName;
    }

    public String getOrgPicUrl() {
        return orgPicUrl;
    }

    public void setOrgPicUrl(String orgPicUrl) {
        this.orgPicUrl = orgPicUrl;
    }

    public String getOrgInterfaceUrl() {
        return orgInterfaceUrl;
    }

    public void setOrgInterfaceUrl(String orgInterfaceUrl) {
        this.orgInterfaceUrl = orgInterfaceUrl;
    }

    public String getOrgDesc() {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc) {
        this.orgDesc = orgDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}