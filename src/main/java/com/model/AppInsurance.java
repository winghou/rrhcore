package com.model;

//保险
public class AppInsurance {

	private Integer id;
	private String type;    //保险类型
	private String insurance_name;  //保险名称
	private String insurance_url;   //保险链接
	private String is_use;  //保险是否可用
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInsurance_name() {
		return insurance_name;
	}
	public void setInsurance_name(String insurance_name) {
		this.insurance_name = insurance_name;
	}
	public String getInsurance_url() {
		return insurance_url;
	}
	public void setInsurance_url(String insurance_url) {
		this.insurance_url = insurance_url;
	}
	public String getIs_use() {
		return is_use;
	}
	public void setIs_use(String is_use) {
		this.is_use = is_use;
	}
	
	
}
