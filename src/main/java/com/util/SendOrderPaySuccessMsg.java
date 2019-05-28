package com.util;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class SendOrderPaySuccessMsg {

	public static void send_template_message(String appId, String appSecret, String openId, String templateId,String currurl,Map<String, TemplateData> m) {
		AccessToken token = WeixinUtil.getAccessToken(appId, appSecret);
		String access_token = token.getAccess_token();
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
		WxTemplate temp = new WxTemplate();
		temp.setUrl(currurl);
		temp.setTouser(openId);
		temp.setTopcolor("#000000");
		temp.setTemplate_id(templateId);
		temp.setData(m);
		String jsonString = JSONObject.toJSONString(temp);
		JSONObject jsonObject = WeixinUtil.httpRequest(url, "POST", jsonString);
		System.out.println(jsonObject);
		int result = 0;
		if (null != jsonObject) {
			if (0 != jsonObject.getInteger("errcode")) {
				result = jsonObject.getInteger("errcode");
			}
			System.out.println(result+"===消息code");
		}
	}

	public static void main(String[] args) {
		Map<String, TemplateData> m = new HashMap<String, TemplateData>();
		TemplateData first = new TemplateData();
		first.setColor("#000000");
		first.setValue("饶瑞文，您的04月信用账单已出，请于2017/04/18前还款，以免逾期影响您的信用记录");
		m.put("first", first);
		TemplateData name = new TemplateData();
		name.setColor("#000000");
		name.setValue("医美");
		m.put("keyword1", name);
		TemplateData wuliu = new TemplateData();
		wuliu.setColor("#000000");
		wuliu.setValue("1234元");
		m.put("keyword2", wuliu);
		TemplateData status = new TemplateData();
		status.setColor("#000000");
		status.setValue("2017/04/18");
		m.put("keyword3", status);
		TemplateData remark = new TemplateData();
		remark.setColor("#000000");
		remark.setValue("");
		m.put("remark", remark);
		send_template_message("wxadefeb8aaf282a1b", "8c9d0b8d2fbf5fdfee1de69b8dc1e443", "opQCDwCyiI1MH9M98V0CkHAvhjSw", "D3juVVyWarw5B3UqNZjt1n3YFgsUMgChh9G1lxOT358", "",m);
	}

}
