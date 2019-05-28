package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface YiXingTongBankCardCheckService {
	
	public JSONObject bankCardCheck(JSONObject params) throws Exception;
	
	public JSONObject querySupportedBank(JSONObject params) throws Exception;
	
	public JSONObject lookBindBankinfo(JSONObject params) throws Exception;

	public JSONObject queryVerifyCardResult(JSONObject params) throws Exception;

}
