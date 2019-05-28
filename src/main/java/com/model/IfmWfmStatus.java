package com.model;

import java.util.Date;

public class IfmWfmStatus {
    private Integer id;

    private Integer tpid;

    private String appluser;

    private String applname;

    private String status;

    private Date createdate;

    private String bizid;

    private String comment;

    private String custId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTpid() {
        return tpid;
    }

    public void setTpid(Integer tpid) {
        this.tpid = tpid;
    }

    public String getAppluser() {
        return appluser;
    }

    public void setAppluser(String appluser) {
        this.appluser = appluser;
    }

    public String getApplname() {
        return applname;
    }

    public void setApplname(String applname) {
        this.applname = applname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getBizid() {
        return bizid;
    }

    public void setBizid(String bizid) {
        this.bizid = bizid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}