package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppGoSumbitService {
	public JSONObject AppgoSumbit(JSONObject params) throws Exception;
	public JSONObject sumbitUserInfoToDecisionEngine(JSONObject params) throws Exception;

}
