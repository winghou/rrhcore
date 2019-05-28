package com.bestsign.model;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.bestsign.config.BestSignConfig;
import com.bestsign.utils.EncodeUtils;


/**  
*   
* 项目名称：悦才合同  
* 创建人：zhaheng  
* 创建时间：2017年12月27日 上午11:01:51  
* @version       
*/ 
public class YueCaiContract {

	//文件md5值
	private String fmd5;
	//文件类型
	private String ftype="PDF";
	//文件页数
	private String fpages;
	//文件数据，base64编码
	private String fdata;
	//合同标题
	private String title;
	//合同签署的到期时间   用户签署合同之后的六天  就是144个小时之内需要完成签署
	private String expireTime;
	//合同文件路径
	private String daiKouContractPath;
	//文件名
	private String fname;
	//合同描述
	private String description;
	
	public  YueCaiContract(String ffname,String ftitle,String fpage,String fdescription,String daiKouContractPath) throws IOException {
		FileInputStream file = new FileInputStream(daiKouContractPath);
		byte[] bdata = IOUtils.toByteArray(file);
		//文件md5值
		fname=ffname;
		title=ftitle;
		fpages=fpage;
		description=fdescription;
		fmd5= EncodeUtils.md5(bdata);
		fdata=Base64.encodeBase64String(bdata);
		expireTime=BestSignConfig.getExpireTime(6);//合同签署的到期时间
	}

	public String getFmd5() {
		return fmd5;
	}
	public void setFmd5(String fmd5) {
		this.fmd5 = fmd5;
	}
	public String getFtype() {
		return ftype;
	}
	public void setFtype(String ftype) {
		this.ftype = ftype;
	}
	public String getFpages() {
		return fpages;
	}
	public void setFpages(String fpages) {
		this.fpages = fpages;
	}
	public String getFdata() {
		return fdata;
	}
	public void setFdata(String fdata) {
		this.fdata = fdata;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}
	public String getDaiKouContractPath() {
		return daiKouContractPath;
	}
	public void setDaiKouContractPath(String daiKouContractPath) {
		this.daiKouContractPath = daiKouContractPath;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
