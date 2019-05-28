package com.model;

public class AppLoanBank {
    private Integer id;

    private String bankId;

    private String bankName;

    private String cardType;

    private String cardNo;

    private String dqYear;

    private String dqMonth;

    private String cardLast3;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getDqYear() {
        return dqYear;
    }

    public void setDqYear(String dqYear) {
        this.dqYear = dqYear;
    }

    public String getDqMonth() {
        return dqMonth;
    }

    public void setDqMonth(String dqMonth) {
        this.dqMonth = dqMonth;
    }

    public String getCardLast3() {
        return cardLast3;
    }

    public void setCardLast3(String cardLast3) {
        this.cardLast3 = cardLast3;
    }
}