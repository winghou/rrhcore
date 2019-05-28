package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryAnnounceService {

	public JSONObject queryAnnounce(JSONObject params);
	
	public JSONObject queryShareContent(JSONObject params);
}
