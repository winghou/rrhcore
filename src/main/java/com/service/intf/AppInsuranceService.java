package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppInsuranceService {

	public JSONObject getInsurance(JSONObject params) throws Exception;
}
