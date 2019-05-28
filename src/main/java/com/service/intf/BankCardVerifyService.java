package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface BankCardVerifyService {
	public JSONObject bankCardVerify(JSONObject params);
	
	/**
	 * 小安银行卡校验外部调用方法
	 * @param params
	 * @return
	 */
	public JSONObject xiaoanBankCardCheck(JSONObject params);
}
