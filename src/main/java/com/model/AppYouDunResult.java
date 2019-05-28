package com.model;

import java.util.Date;

public class AppYouDunResult {

	private Integer id;
	private Integer apprId;
	private String partnerOrderId; 		// 商户订单号
	private String idNumber; 			// 身份证号
	private String idName; 				// 姓名
	private String idcardFrontPhoto; 	// 身份证正面照
	private String idcardBackPhoto;		// 身份证反面照
	private String address;				// 地址
	private String riskTag;				// 风险标签  0-未检测到活体攻击；1-存在活体攻击风险；
	private String similarity;			// 相似度
	private String thresholds;			// 相似度阈值
	private String verifyStatus;		// 身份认证结果
	private String authResult;			// 比对结果
	private Date createDate;			// 创建时间
	private String status;				// 预留字段（异常标记）0:校验成功,1:校验失败
	
	
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
	public String getPartnerOrderId() {
		return partnerOrderId;
	}
	public void setPartnerOrderId(String partnerOrderId) {
		this.partnerOrderId = partnerOrderId;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public String getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	public String getIdcardFrontPhoto() {
		return idcardFrontPhoto;
	}
	public void setIdcardFrontPhoto(String idcardFrontPhoto) {
		this.idcardFrontPhoto = idcardFrontPhoto;
	}
	public String getIdcardBackPhoto() {
		return idcardBackPhoto;
	}
	public void setIdcardBackPhoto(String idcardBackPhoto) {
		this.idcardBackPhoto = idcardBackPhoto;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRiskTag() {
		return riskTag;
	}
	public void setRiskTag(String riskTag) {
		this.riskTag = riskTag;
	}
	public String getSimilarity() {
		return similarity;
	}
	public void setSimilarity(String similarity) {
		this.similarity = similarity;
	}
	public String getThresholds() {
		return thresholds;
	}
	public void setThresholds(String thresholds) {
		this.thresholds = thresholds;
	}
	public String getVerifyStatus() {
		return verifyStatus;
	}
	public void setVerifyStatus(String verifyStatus) {
		this.verifyStatus = verifyStatus;
	}
	public String getAuthResult() {
		return authResult;
	}
	public void setAuthResult(String authResult) {
		this.authResult = authResult;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
