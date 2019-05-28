package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface LoanApplyService {
	public JSONObject queryAmtAndDaysAndCoupons(JSONObject params);
	public JSONObject IneedMoney(JSONObject params) throws Exception;
	public JSONObject QureyUrlAndMonthpay(JSONObject params);
	public JSONObject myBill(JSONObject params);
	public JSONObject loanByOneStep(JSONObject params);
	public JSONObject loanOrderFail(JSONObject json);
	public JSONObject loanOrderSuccess(JSONObject json);
	
}
