package com.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.model.SmsBean;

/**
* 项目名称：rrhcore   
* 类名称：PhoneValicodeUtil   
* 类描述：   
* 创建人：Administrator   
* 创建时间：2017年5月16日 下午4:59:53   
* 修改人：lizhongwei   
* 修改时间：2017年5月16日 下午4:59:53   
* 修改备注： 发送短信
* @version    
 */
public class PhoneValicodeUtil {
	
	/**
	* 创建人：lizhongwei   
	* 创建时间：2017年5月16日 下午5:02:49   
	* 修改人：lizhongwei  
	* 修改时间：2017年5月16日 下午5:02:49   
	* 修改备注：   phone:发送手机号，sendId:发送码(ifm_sys_sms主键)，url:短信发送项目路径(SELECT * FROM `ifm_base_dict` t where t.DATA_TYPE = 'OUT_WEB_SMS';)，m：参数值
	* @version
	 */
	public static JSONObject sendMessage(String phone, String sendId, String url, Map<String , Object> m) {
		JSONObject object = new JSONObject();

		SmsBean bean = new SmsBean();
		bean.setId(sendId);
		List<SmsBean.SmsParams> mutils = new ArrayList<SmsBean.SmsParams>();
		Map<String, String> maps = new HashMap<String, String>();
		SmsBean.SmsParams sp = new SmsBean.SmsParams();
		sp.setTo(phone);
		if (null != m && m.size() > 0) {
			List<String> keys = new ArrayList<String>(m.keySet());
			String value = "";
			for(String k : keys){
				value = StringUtil.nvl(m.get(k));
				maps.put(k, value);
			}
		}
		sp.setVars(maps);
		mutils.add(sp);
		bean.setMulti(mutils);
		String reString = URLInvoke.postForJson(url + "/" + Constants.SMS_METHOD,
				JsonUtil.covertSms(bean).toJSONString());
		JSONObject restring = (JSONObject) JSON.parse(reString);
		if ("Success".equals(restring.get("resultNote"))) {
			object.put("success", "true");
		} else {
			object.put("success", "false");
		}
		return object;
	}
}
