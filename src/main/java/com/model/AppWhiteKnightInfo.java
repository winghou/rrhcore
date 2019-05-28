package com.model;

public class AppWhiteKnightInfo {
	
	private Integer id;
	private Integer apprId;
	private String tokenKey;  //设备号关联key
	private String mac;  
	private String imei;
	private String idfa;
	private String ip;
	private String longitude;  //GPS经度
	private String latitude;  //GPS维度
	private String address;  //地理地址
	private String phone_model;  //设备型号
	private String ver_code;  //设备版本号
	private String app_list;  //app应用列表
	
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone_model() {
		return phone_model;
	}
	public void setPhone_model(String phone_model) {
		this.phone_model = phone_model;
	}
	public String getVer_code() {
		return ver_code;
	}
	public void setVer_code(String ver_code) {
		this.ver_code = ver_code;
	}
	public String getApp_list() {
		return app_list;
	}
	public void setApp_list(String app_list) {
		this.app_list = app_list;
	}
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
	public String getTokenKey() {
		return tokenKey;
	}
	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	
}
