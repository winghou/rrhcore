package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface WebPageStatisticsService {
	/**
	 * @author zw
	 * 渠道页面每日统计
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject pageStatistics(JSONObject params) throws Exception;
}
