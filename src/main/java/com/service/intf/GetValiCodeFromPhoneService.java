package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface GetValiCodeFromPhoneService {
	public JSONObject getValiCodeFromPhone(JSONObject params) throws Exception;
	/**
	 * 获取语音验证码
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject getVoiceMsgFromPhone(JSONObject params) throws Exception;
	/**
	 * 语音验证码提示语
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JSONObject getVoiceMsgClues(JSONObject params) throws Exception;

}
