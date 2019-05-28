package com.service.intf;

import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface AppBestSignService {

	/**  
	*   
	* 描述：  上传并创建合同
	* 创建人：zhaheng  
	* 创建时间：2017年12月26日 下午5:54:49  
	* @version       
	 * @throws Exception 
	*/ 
	JSONObject uploadAndCreateContract(JSONObject params) throws Exception;
	/**生成用户签名/印章图片**/
	JSONObject CreateSign();
	/**上传用户签名/印章图片 这个是盖章
	 * @throws Exception **/
	JSONObject uploadSign() throws Exception;
	/**下载用户签名/印章图片
	 * @throws Exception **/
	String downLoadSign() throws Exception;
	/**上传合同文件
	 * @throws Exception **/
	String uploadContract(JSONObject params) throws Exception;
	/**为PDF文件添加元素 
	 * @throws Exception **/
	JSONObject addDataToPdf(JSONObject params) throws Exception;
	/**创建合同
	 * @throws Exception **/
	JSONObject createContract(JSONObject params) throws Exception;
	/**签署并锁定合同
	 * @throws Exception **/
	JSONObject signAndLockContract(JSONObject params) throws Exception;
	/**获取预览页URL
	 * @throws IOException **/
	JSONObject getContractUrl(JSONObject params) throws Exception;
	/**上传合同模板接口——为PDF添加元素接口——创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口
	 * 一次性完成的接口，方便调试
	 * @throws Exception 
	 * **/
	JSONObject fenQiContract(JSONObject params) throws Exception;
	/**上传并创建合同接口——签署合接口——锁定并结束合同接口——预览合同接口
	 * 一次性完成的接口，方便调试
	 * **/
	JSONObject daiKouContract(JSONObject params) throws Exception;
	/*** 上传合同--添加PDF元素--创建合同方法 ***/
	public String uploadAndCreatefenQiContract(String fname, String title, String fpages, String description,String daiKouContractPath, JSONArray elementsArray) throws Exception;
	
	/** 获取预览URL **/
	public String getScanUrl(String contractId) throws Exception;
	/** 签署并锁定合同同时生成预览URL方法封装 **/
	public Boolean signAndLockAndScan(JSONArray signaturePositionsArray, String contractId) throws Exception;
	
	/**
	 * 借款页面预览合同
	 */
	JSONObject viewBestSign(JSONObject params) throws Exception;
	
}
