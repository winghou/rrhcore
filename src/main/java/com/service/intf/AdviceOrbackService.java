package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AdviceOrbackService {
	public JSONObject adviceOrbackService(JSONObject params) throws Exception;

	public JSONObject adviceOrbackTypeService(JSONObject params) throws Exception;
}
