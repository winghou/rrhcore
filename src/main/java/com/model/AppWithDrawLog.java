package com.model;

public class AppWithDrawLog {
    private Integer id;

    private Integer withdrawId;

    private String nodeContent;

    private String detail;

    private String happendTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getWithdrawId() {
        return withdrawId;
    }

    public void setWithdrawId(Integer withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getNodeContent() {
        return nodeContent;
    }

    public void setNodeContent(String nodeContent) {
        this.nodeContent = nodeContent;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getHappendTime() {
        return happendTime;
    }

    public void setHappendTime(String happendTime) {
        this.happendTime = happendTime;
    }
}