package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface WhiteKnightOperatorService {

	JSONObject whiteKnightOperatorLogin(JSONObject params) throws Exception;

	JSONObject whiteKnightOperatorSendLoginSms(JSONObject params) throws Exception;

	JSONObject whiteKnightOperatorSendAuthSms(JSONObject params) throws Exception;

//	JSONObject whiteKnightOperatorReportext(JSONObject params) throws Exception;

	JSONObject whiteKnightOperatorVerifyAuthSms(JSONObject params) throws Exception;

	JSONObject whiteKnightOperatorResetpwd(JSONObject params) throws Exception;

	JSONObject whiteKnightOperatorSendResetpwdSms(JSONObject params) throws Exception;

}
