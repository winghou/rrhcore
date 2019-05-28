package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AppLoanCtmCntService {
	public JSONObject appLoanCtmCnt(JSONObject params);

	public JSONObject platformLogin(JSONObject params);

	public JSONObject platformLoanCtmCntSave(JSONObject params);

	public JSONObject userPlatforms(JSONObject params);

	public JSONObject ctmcntLogin(JSONObject params);

	public JSONObject userEmails(JSONObject params);

	public JSONObject emailDetailByCode(JSONObject params);

	public JSONObject emailmPwdChange(JSONObject params);

	public JSONObject creditCardList(JSONObject params);

	public JSONObject creditCardDetail(JSONObject params);

	public JSONObject creditCardSaveOrUpdate(JSONObject params);

	public JSONObject depositCardList(JSONObject params);

	public JSONObject depositCardDetail(JSONObject params);

	public JSONObject depositCardSaveOrUpdate(JSONObject params);

	public JSONObject socialSecurityList(JSONObject params);

	public JSONObject socialSecurityDetail(JSONObject params);

	public JSONObject socialSecuritySaveOrUpdate(JSONObject params);

	public JSONObject housingFundList(JSONObject params);

	public JSONObject housingFundDetail(JSONObject params);

	public JSONObject housingFundSaveOrUpdate(JSONObject params);

	public JSONObject userProgressRate(JSONObject params);
}
