package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface OldUserToNewService {
	
	public JSONObject oldUserToNew(JSONObject params) throws Exception;
	public JSONObject createInviteCode(JSONObject params) throws Exception;

}
