package com.model;

import java.util.Date;

public class AppEbAddress {
    private Integer addressId;

    private String userId;

    private String ebFrom;

    private String address;

    private String zipcode;

    private String reviceName;

    private String revicePhone;

    private String reviceTel;

    private Date queryDate;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEbFrom() {
        return ebFrom;
    }

    public void setEbFrom(String ebFrom) {
        this.ebFrom = ebFrom;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getReviceName() {
        return reviceName;
    }

    public void setReviceName(String reviceName) {
        this.reviceName = reviceName;
    }

    public String getRevicePhone() {
        return revicePhone;
    }

    public void setRevicePhone(String revicePhone) {
        this.revicePhone = revicePhone;
    }

    public String getReviceTel() {
        return reviceTel;
    }

    public void setReviceTel(String reviceTel) {
        this.reviceTel = reviceTel;
    }

    public Date getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(Date queryDate) {
        this.queryDate = queryDate;
    }
}