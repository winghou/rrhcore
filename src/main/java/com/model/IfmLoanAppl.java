package com.model;

import java.util.Date;

public class IfmLoanAppl {
    private Integer id;

    private String itemCode;

    private String status;

    private String loaningStatus;

    private Date createDate;

    private String prvince;

    private String city;

    private String comment;

    private Date apprDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoaningStatus() {
        return loaningStatus;
    }

    public void setLoaningStatus(String loaningStatus) {
        this.loaningStatus = loaningStatus;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPrvince() {
        return prvince;
    }

    public void setPrvince(String prvince) {
        this.prvince = prvince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getApprDate() {
        return apprDate;
    }

    public void setApprDate(Date apprDate) {
        this.apprDate = apprDate;
    }
}