package com.model;

public class AppYouDunParam {

	private Integer id;
	private String publicKey; //有盾公钥
	private String privateKey; //有盾私钥
	private String packageCode; //有盾套餐编号
	private String noyifyUrl; //有盾异步回调地址
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public String getNoyifyUrl() {
		return noyifyUrl;
	}
	public void setNoyifyUrl(String noyifyUrl) {
		this.noyifyUrl = noyifyUrl;
	}
	
	
	
	
	
}
