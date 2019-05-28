package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface DecisionEngineService {

	public JSONObject checkReportExsit(JSONObject params) throws Exception;
}
