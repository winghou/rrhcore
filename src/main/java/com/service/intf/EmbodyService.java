package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface EmbodyService {
	
	/**
	 * 首页
	 * @param params
	 * @return
	 */
	public JSONObject homePage(JSONObject params);
	
	/**
	 * 首页未读消息数量分开查询
	 * @param params
	 * @return
	 */
	public JSONObject queryNotReadMessage(JSONObject params);
	
	/**
	 * 首页用户未还款还款计划轮播图
	 * @param params
	 * @return
	 */
	public JSONObject payPlanCarousel(JSONObject params);

}
