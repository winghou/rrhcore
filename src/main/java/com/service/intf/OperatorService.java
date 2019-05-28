package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface OperatorService {
	public JSONObject operator(JSONObject params) throws Exception;
	
	public JSONObject operatorResetServiceNumber(JSONObject params) throws Exception;
	
	public JSONObject getBaseInfoToOperator(JSONObject params);
	
	public JSONObject getAccountStatus(JSONObject params);
}
