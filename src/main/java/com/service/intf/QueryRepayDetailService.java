package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryRepayDetailService {
	public JSONObject queryRepayDetail(JSONObject params) throws Exception;
	
	public JSONObject fastRepayment(JSONObject params);

}
