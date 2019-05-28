package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppIncreaseAmtInfoService {
	
	public JSONObject getIncreaseAmtInfoStatus(JSONObject params);
	
	public JSONObject increaseAmtInfoAccredit(JSONObject params)throws Exception;
	
}
