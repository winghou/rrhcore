package com.model;

public class AppPostbox {
	private Integer id;//自增ID
	private String postboxurl;//邮箱URL
	private String postboxname;//邮箱名
	private Integer postboxtype;//邮箱识别号
	private String postboxImg;//邮箱图标
	private String loginSuccess;//成功时需传的值
	private String html;
	private String agent;
	private String postboxalias;//邮箱别名
	public AppPostbox() {
	}
	public AppPostbox(Integer id, String postboxurl, String postboxname,
			Integer postboxtype, String postboxImg, String loginSuccess,
			String html, String agent, String postboxalias) {
		super();
		this.id = id;
		this.postboxurl = postboxurl;
		this.postboxname = postboxname;
		this.postboxtype = postboxtype;
		this.postboxImg = postboxImg;
		this.loginSuccess = loginSuccess;
		this.html = html;
		this.agent = agent;
		this.postboxalias = postboxalias;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPostboxurl() {
		return postboxurl;
	}
	public void setPostboxurl(String postboxurl) {
		this.postboxurl = postboxurl;
	}
	public String getPostboxname() {
		return postboxname;
	}
	public void setPostboxname(String postboxname) {
		this.postboxname = postboxname;
	}
	public Integer getPostboxtype() {
		return postboxtype;
	}
	public void setPostboxtype(Integer postboxtype) {
		this.postboxtype = postboxtype;
	}
	public String getPostboxImg() {
		return postboxImg;
	}
	public void setPostboxImg(String postboxImg) {
		this.postboxImg = postboxImg;
	}
	public String getLoginSuccess() {
		return loginSuccess;
	}
	public void setLoginSuccess(String loginSuccess) {
		this.loginSuccess = loginSuccess;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public String getPostboxalias() {
		return postboxalias;
	}
	public void setPostboxalias(String postboxalias) {
		this.postboxalias = postboxalias;
	}
	
	

	


	
}
