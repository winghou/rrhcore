package com.frame;

import com.alibaba.fastjson.JSONObject;

/**
 * 请求结构模板类，规范请求的格式
 * 
 * @author bill
 */
public class RequestTemplate {

	public String command = null;
	public String version = null;
	public String userId = null;
	public String token = null;
	public JSONObject params = null;

	public RequestTemplate(JSONObject reqJson) {
		if (reqJson != null) {
			params = reqJson.getJSONObject(Consts.PARAMS_LABEL);
			command = reqJson.getString(Consts.CMD_LABEL);
			version = reqJson.getString(Consts.VERSION);
			token = reqJson.getString(Consts.TOKEN);
			userId = reqJson.getString(Consts.USERID);
		}
	}
	
	public String getUserId() {
		return userId;
	}

	public String getCommand() {
		return command;
	}

	public String getVersion() {
		return version;
	}
	
	public String getToken() {
		return token;
	}

	public JSONObject getParams() {
		return params;
	}

}
