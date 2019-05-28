package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface GetProtocolUrlService {

	public JSONObject getProtocolUrl(JSONObject params) throws Exception;
	public JSONObject getProtocolInfo(JSONObject params) throws Exception;
}
