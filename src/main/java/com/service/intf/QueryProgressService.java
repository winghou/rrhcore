package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface QueryProgressService {
	public JSONObject queryProgress(JSONObject params);
	public JSONObject queryMyStatus(JSONObject params);
	public JSONObject queryPercent(JSONObject params);
	public JSONObject queryTbAndJdStutus(JSONObject params);
	public JSONObject queryXykAndYx(JSONObject params);

}
