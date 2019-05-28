package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface SaveOrUpdatePersonInfo {
	public JSONObject QueryPersonInfo(JSONObject params);
	public JSONObject QuerySchool(JSONObject params);
	public JSONObject authenticationSchool(JSONObject params) throws Exception;
	public JSONObject authenticationFamily(JSONObject params) throws Exception;
	

}
