package com.model;

import java.util.Date;

public class AppUploadPdfFile {
	private Integer id;
	private String applyNo;// 签约合同号
	private String withDrawId;// 订单号
	private String fileUrl;// PDF文件保存路径
	private Date createTime;
	private String fileDesc;
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getApplyNo() {
		return applyNo;
	}

	public void setApplyNo(String applyNo) {
		this.applyNo = applyNo;
	}

	public String getWithDrawId() {
		return withDrawId;
	}

	public void setWithDrawId(String withDrawId) {
		this.withDrawId = withDrawId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
