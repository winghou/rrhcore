package com.model;

public class AppLoanAttch {
    private Integer id;

    private String fileName; //照片类型 0：正面，1：反面

    private String fileUri; //身份证正面照地址
    
    private String smallPicUrl; //身份证正面压缩照地址

    private Integer apprId;
    
    private Integer picDesc;
    
    private String portraitPhotoUrl; //身份证头像照地址
    
    
	

	public String getPortraitPhotoUrl() {
		return portraitPhotoUrl;
	}

	public void setPortraitPhotoUrl(String portraitPhotoUrl) {
		this.portraitPhotoUrl = portraitPhotoUrl;
	}

	public Integer getPicDesc() {
		return picDesc;
	}

	public void setPicDesc(Integer picDesc) {
		this.picDesc = picDesc;
	}

	public String getSmallPicUrl() {
		return smallPicUrl;
	}

	public void setSmallPicUrl(String smallPicUrl) {
		this.smallPicUrl = smallPicUrl;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public Integer getApprId() {
        return apprId;
    }

    public void setApprId(Integer apprId) {
        this.apprId = apprId;
    }
}