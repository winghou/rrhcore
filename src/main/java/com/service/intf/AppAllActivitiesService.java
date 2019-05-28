package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppAllActivitiesService {

	public JSONObject queryUserRewardAmt(JSONObject params);
	public JSONObject showLotteryTable(JSONObject params);
	public JSONObject lotteryActivity(JSONObject params) throws Exception;
	JSONObject loverActivity(JSONObject params);
}
