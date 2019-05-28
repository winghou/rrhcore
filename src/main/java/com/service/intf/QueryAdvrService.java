package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryAdvrService {
	public JSONObject queryAdvr(JSONObject params_);
	
	public JSONObject queryAdvrByModule(JSONObject params);

}
