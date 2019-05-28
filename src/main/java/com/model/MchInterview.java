package com.model;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

public class MchInterview  implements  Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7239804906294595512L;

	private Integer id;
	private String ip;
	private String mac;
	private String user_uuid;
	private Integer interview_type_id;
	private Date interview_time;
	private String device_type;
	private Integer isdelete;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUser_uuid() {
		return user_uuid;
	}
	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}
	public Integer getInterview_type_id() {
		return interview_type_id;
	}
	public void setInterview_type_id(Integer interview_type_id) {
		this.interview_type_id = interview_type_id;
	}
	public Date getInterview_time() {
		return interview_time;
	}
	public void setInterview_time(Date interview_time) {
		this.interview_time = interview_time;
	}
	public String getDevice_type() {
		return device_type;
	}
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}
	public Integer getIsdelete() {
		return isdelete;
	}
	public void setIsdelete(Integer isdelete) {
		this.isdelete = isdelete;
	}
	
	
	
	
	
}
