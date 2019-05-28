package com.model;

public class AppLoanCtmCnt {
    private Integer id;

    private String cntType;

    private String cntCommt;

    private String cntPass;

    private String cntDesc;

    private String cntLx;

    private Integer apprId;

    private Integer platformId;

    private String mobilePhone;

    private String province;

    private String city;

    private int status;

    private String groupId;
    
    private String postboxUUID;
    
    private Integer postboxType;
    
    public Integer getPostboxType() {
		return postboxType;
	}

	public void setPostboxType(Integer postboxType) {
		this.postboxType = postboxType;
	}

	public String getPostboxUUID() {
		return postboxUUID;
	}

	public void setPostboxUUID(String postboxUUID) {
		this.postboxUUID = postboxUUID;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCntType() {
        return cntType;
    }

    public void setCntType(String cntType) {
        this.cntType = cntType;
    }

    public String getCntCommt() {
        return cntCommt;
    }

    public void setCntCommt(String cntCommt) {
        this.cntCommt = cntCommt;
    }

    public String getCntPass() {
        return cntPass;
    }

    public void setCntPass(String cntPass) {
        this.cntPass = cntPass;
    }

    public String getCntDesc() {
        return cntDesc;
    }

    public void setCntDesc(String cntDesc) {
        this.cntDesc = cntDesc;
    }

    public Integer getApprId() {
        return apprId;
    }

    public void setApprId(Integer apprId) {
        this.apprId = apprId;
    }

    public String getCntLx() {
        return cntLx;
    }

    public void setCntLx(String cntLx) {
        this.cntLx = cntLx;
    }

    public Integer getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}