package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryBaseInfoService {

	public JSONObject info(JSONObject params);
	
	public JSONObject saveInfo(JSONObject params);
}
