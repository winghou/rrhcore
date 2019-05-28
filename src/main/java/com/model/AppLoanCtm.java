package com.model;

import java.util.Date;

public class AppLoanCtm {
    private Integer id;

    private Integer apprId;

    private String customName;

    private String sex;

    private String identityCard;

    private Date birthday;

    private String place;

    private String province;

    private String city;

    private String town;

    private String schoolName;

    private String grade;

    private String stuCard;

    private Date entraDate;

    private String bdrAddr;

    private String bank;

    private String bankCard;

    private String bankPhone;

    private String monthlyAmt;

    private String isMarry;

    private String schoolJob;

    private String address;

    private String jg;

    private String educational;

    private String status;

    private String companyName;

    private String companyAddress;

    private String compayPayDay;

    private String commProvince;

    private String commCity;

    private String commTown;
    
    private String commJob;
    
    private String identity_status; 
    
    private String school_status;
    
    private String lxr_status;
    
    private String bank_status;
    
    private String company_status;
    
    private String family_status;
    
    private String schedule_status;
    
    private String companyPhone;
    
    private String is_pass;
    
    private String is_modify;
    
    private String verify_time; 
    
    private String bind_card_time;//验卡成功时间
    
    private Date operatorTime; //运营商认证时间
    
    private String phoneAndIdMd5; //手机号与身份证号拼接后加密
    
    private String car; //车辆情况
    
    private String profession; //职业
    
    private String social_security; //社保缴纳
    
    private String work_years; //车辆情况
    
    private String income; //收入
    
    private String live_way; //居住情况
    
    private String merch_order_no; //协议支付用
    
    

    
	public String getMerch_order_no() {
		return merch_order_no;
	}

	public void setMerch_order_no(String merch_order_no) {
		this.merch_order_no = merch_order_no;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getSocial_security() {
		return social_security;
	}

	public void setSocial_security(String social_security) {
		this.social_security = social_security;
	}

	public String getWork_years() {
		return work_years;
	}

	public void setWork_years(String work_years) {
		this.work_years = work_years;
	}

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	public String getLive_way() {
		return live_way;
	}

	public void setLive_way(String live_way) {
		this.live_way = live_way;
	}

	public String getPhoneAndIdMd5() {
		return phoneAndIdMd5;
	}

	public void setPhoneAndIdMd5(String phoneAndIdMd5) {
		this.phoneAndIdMd5 = phoneAndIdMd5;
	}

	/**
	 * @return the operatorTime
	 */
	public Date getOperatorTime() {
		return operatorTime;
	}

	/**
	 * @param operatorTime the operatorTime to set
	 */
	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime;
	}

	/**
	 * @return the bind_card_time
	 */
	public String getBind_card_time() {
		return bind_card_time;
	}

	/**
	 * @param bind_card_time the bind_card_time to set
	 */
	public void setBind_card_time(String bind_card_time) {
		this.bind_card_time = bind_card_time;
	}

	public String getVerify_time() {
		return verify_time;
	}

	public void setVerify_time(String verify_time) {
		this.verify_time = verify_time;
	}

	public String getIs_pass() {
		return is_pass;
	}

	public void setIs_pass(String is_pass) {
		this.is_pass = is_pass;
	}

	public String getIs_modify() {
		return is_modify;
	}

	public void setIs_modify(String is_modify) {
		this.is_modify = is_modify;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

    public String getCommJob() {
		return commJob;
	}

	public void setCommJob(String commJob) {
		this.commJob = commJob;
	}

	public String getLxr_status() {
		return lxr_status;
	}

	public void setLxr_status(String lxr_status) {
		this.lxr_status = lxr_status;
	}

	public String getIdentity_status() {
		return identity_status;
	}

	public void setIdentity_status(String identity_status) {
		this.identity_status = identity_status;
	}

	public String getSchool_status() {
		return school_status;
	}

	public void setSchool_status(String school_status) {
		this.school_status = school_status;
	}

	public String getBank_status() {
		return bank_status;
	}

	public void setBank_status(String bank_status) {
		this.bank_status = bank_status;
	}

	public String getCompany_status() {
		return company_status;
	}

	public void setCompany_status(String company_status) {
		this.company_status = company_status;
	}

	public String getFamily_status() {
		return family_status;
	}

	public void setFamily_status(String family_status) {
		this.family_status = family_status;
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

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStuCard() {
        return stuCard;
    }

    public void setStuCard(String stuCard) {
        this.stuCard = stuCard;
    }

    public Date getEntraDate() {
        return entraDate;
    }

    public void setEntraDate(Date entraDate) {
        this.entraDate = entraDate;
    }

    public String getBdrAddr() {
        return bdrAddr;
    }

    public void setBdrAddr(String bdrAddr) {
        this.bdrAddr = bdrAddr;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankPhone() {
        return bankPhone;
    }

    public void setBankPhone(String bankPhone) {
        this.bankPhone = bankPhone;
    }

    public String getMonthlyAmt() {
        return monthlyAmt;
    }

    public void setMonthlyAmt(String monthlyAmt) {
        this.monthlyAmt = monthlyAmt;
    }

    public String getIsMarry() {
        return isMarry;
    }

    public void setIsMarry(String isMarry) {
        this.isMarry = isMarry;
    }

    public String getSchoolJob() {
        return schoolJob;
    }

    public void setSchoolJob(String schoolJob) {
        this.schoolJob = schoolJob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJg() {
        return jg;
    }

    public void setJg(String jg) {
        this.jg = jg;
    }

    public String getEducational() {
        return educational;
    }

    public void setEducational(String educational) {
        this.educational = educational;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompayPayDay() {
        return compayPayDay;
    }

    public void setCompayPayDay(String compayPayDay) {
        this.compayPayDay = compayPayDay;
    }

    public String getCommProvince() {
        return commProvince;
    }

    public void setCommProvince(String commProvince) {
        this.commProvince = commProvince;
    }

    public String getCommCity() {
        return commCity;
    }

    public void setCommCity(String commCity) {
        this.commCity = commCity;
    }

    public String getCommTown() {
        return commTown;
    }

    public void setCommTown(String commTown) {
        this.commTown = commTown;
    }

	public String getSchedule_status() {
		return schedule_status;
	}

	public void setSchedule_status(String schedule_status) {
		this.schedule_status = schedule_status;
	}

	

	
	
    

}