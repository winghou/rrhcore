package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface ZhiMaCreditService {
	public JSONObject authorizeQry(JSONObject params) throws Exception ;

	public JSONObject zhimaCallBack(JSONObject params) throws Exception;
}
