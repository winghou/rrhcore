package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface LoginService {
	public JSONObject login(JSONObject params) throws Exception;
	
	public JSONObject fastLogin(JSONObject params) throws Exception;

	public JSONObject queryUserIdToken(JSONObject params) throws Exception;
}
