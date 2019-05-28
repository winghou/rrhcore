package com.service.intf;


import com.alibaba.fastjson.JSONObject;

public interface IfmBaseDictServiceIntf {
	/**
	 * 查询服务URL(第一个请求接口)
	 * @param params
	 * @return
	 */
	public  JSONObject queryServiceUrl(JSONObject params);
}
