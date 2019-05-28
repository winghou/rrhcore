package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface PersonDataQueryService {
	public JSONObject querySchedule(JSONObject params);
	public JSONObject selectRewardAmt(JSONObject params);
	public JSONObject selectPersonalInfo(JSONObject params);

}
