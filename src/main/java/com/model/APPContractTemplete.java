package com.model;

public class APPContractTemplete {
    private Integer id;

    private String tmpid;

    private String contractType;

    private String url;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTmpid() {
        return tmpid;
    }

    public void setTmpid(String tmpid) {
        this.tmpid = tmpid;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}