package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface UpdatePasswordService {
	public JSONObject updatePassword(JSONObject params) throws Exception;
	public JSONObject forgetPassword(JSONObject params) throws Exception;
	public JSONObject setPassword(JSONObject params) throws Exception;

}
