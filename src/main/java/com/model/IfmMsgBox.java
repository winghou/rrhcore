package com.model;

public class IfmMsgBox {

	private int id;

	private int user_id;

	private String appr_id;

	private String comment; // 您的 subject 通过审核（被驳回，被拒绝） 

	private String flag = "0"; // 您的 subject 通过审核（被驳回，被拒绝） 
	
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getAppr_id() {
		return appr_id;
	}
	public void setAppr_id(String appr_id) {
		this.appr_id = appr_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
