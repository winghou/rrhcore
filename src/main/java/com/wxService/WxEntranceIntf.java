package com.wxService;

import com.alibaba.fastjson.JSONObject;

public interface WxEntranceIntf {
	//微信绑定接口
	public JSONObject bind(JSONObject params);
	//微信检查是否已经绑定接口
	public JSONObject welogin(JSONObject params);
	
	//微信解绑接口
	public JSONObject relieveBind(JSONObject params);
	
	//微信查询我的额度
	public JSONObject qurMyQuota(JSONObject params);
	//查询微信我的还款
	public JSONObject queryWxOrder(JSONObject params);
	//查询订单号和金额
	public JSONObject qurMyPay(JSONObject params);
	//微信支付
	public JSONObject doPayToWx(JSONObject params);
	//立即注册
	public JSONObject registerWx(JSONObject params) throws Exception;
	//微信二次分享
	public JSONObject wxShare(JSONObject params) throws Exception;
	

}
