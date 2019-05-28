package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppPayZfbService {
	
	/**
	 * 支付宝还款
	 * @param jo
	 * @return
	 */
	public JSONObject payZfb(JSONObject params) throws Exception;
	
	/**
	 * 还款结果
	 * @param jo
	 * @return
	 */
	public JSONObject payResult(JSONObject params) throws Exception;
	
	/**
	 * 快捷代扣
	 * @param jo
	 * @return
	 */
	public JSONObject fastWithholding(JSONObject params) throws Exception;
	
	/**
	 * 支付选择页面
	 * @param jo
	 * @return
	 */
	public JSONObject payWayChoosonPage(JSONObject params) throws Exception;
	
	/**
	 * 支付完成查询还款结果
	 * @param jo
	 * @return
	 */
	public JSONObject qryPayResultFrequently(JSONObject params);
	/**
	 * 验卡  支付 开关
	 */
	public String withholdingSwitch();
	/**
     * @author zw 
     * 直接支付接口
     * @param params
     * @return
     * @throws Exception
     */
	public JSONObject fastBoofProtocolPay(JSONObject params) throws Exception;
	
}
