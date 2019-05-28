package com.util;

import java.util.LinkedHashMap;

import org.nutz.http.Http;

import com.alibaba.fastjson.JSONObject;




public class XinYinUtil {

	public static String XinYin(String out_trade_no, String acc_no, String name, String id_card,
			String mobile) {
		LinkedHashMap<String, Object> vamap = new LinkedHashMap<String, Object>();
	    vamap.put("appKey", "VbLnVxYcyxSstLFqOK");
	    vamap.put("appSecrect", "fMG8ZvyJOs69UWsnY2iFcdZgn0XLVSgjE");
	    vamap.put("version", "2.0.1");
	    vamap.put("timeSpan", DateUtil.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMddHHmmss"));
        vamap.put("out_trade_no", out_trade_no);
        vamap.put("name", name);
        vamap.put("id_card", id_card);
        vamap.put("acc_no", acc_no);
        vamap.put("mobile", mobile);
        String signStr = StringUtil.md5Sign(vamap, true);
        vamap.put("sign", signStr);
        JSONObject object = new JSONObject();

        object.put("cmd", "auth");
        object.put("token", "839df5c560414663bc0c72aac012c031");
        object.put("version", "1");
        object.put("params", vamap);
        object.put("data_type", "json");
        //String result = HttpClientUtil.doPostJson("http://yuecai.yicp.io/pay/auth",object.toJSONString());
        String result = Http.post("http://yuecai.yicp.io/pay/auth", object, 60000);
        System.out.println("result=============="+result);
		return result;
	}
	
}
