package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryPostDetailService {
	public JSONObject queryPostDetail(JSONObject params);
	
	public JSONObject queryContractTemplete(JSONObject params);
	
	public JSONObject queryOrderRecords(JSONObject params);

}
