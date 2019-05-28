package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface AgreementToPayService {
	/**
	 * @author zw 
	 * 协议支付预绑卡
	 * @param params
	 * @return
	 * @throws Exception
	 */
     public JSONObject preBindingCard(JSONObject params) throws Exception;
     
     /**
      * @author zw 
      * 确认绑卡协议支付接口
      * @param params
      * @return
      * @throws Exception
      */
     public JSONObject preBindingCardPhoneValicode(JSONObject params) throws Exception;

	JSONObject queryAgreementPayBank(JSONObject params) throws Exception; 
      
}
