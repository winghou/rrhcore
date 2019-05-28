package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface RegisterService {
	public  JSONObject register(JSONObject params) throws Exception;
	public  JSONObject appRegister(JSONObject params) throws Exception;
}
