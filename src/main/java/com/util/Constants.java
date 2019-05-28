package com.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public final static String SMS_METHOD = "batchSendMsg";
	
	public final static String SMS_VOICE_METHOD = "batchVoiceSendMsg";
	
	public final static Map<String,String> MAP = new HashMap<String,String>();
	
	public static String APPKEY = "VbLnVxYcyxSstLFqOK";
	
	public static String APPSECRECT = "fMG8ZvyJOs69UWsnY2iFcdZgn0XLVSgjE";
	
	//public static String PAY_URL = "http://apitest.yuecaifq.com:8080/pay";
	
	public static String PAY_URL = "http://10.25.250.117:6080/pay";
	
	//public static String PAY_URL = "http://yuecai.yicp.io/pay";
	/**
	 * 存消息liebiao
	 */

	public static final String yyb_mq_topic = "pushDataToYYBTopicTest";
	public static final String yyb_mq_tag = "pushDataToYYBTagTest";
	
}
