package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AuthenticationCustomInfo {
	public JSONObject authenticationCustom(JSONObject params) throws Exception;
	public JSONObject saveXykAndGjj(JSONObject params) throws Exception;

}
