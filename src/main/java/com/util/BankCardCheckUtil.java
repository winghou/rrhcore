package com.util;

import java.util.HashMap;
import java.util.Map;

import org.nutz.http.Http;

public class BankCardCheckUtil {

	/**
	 * 银行卡验证
	 * 
	 * @param name
	 *            用户名
	 * @param identityCard
	 *            身份证号
	 * @param bankCard
	 *            银行卡号
	 * @param phone
	 *            银行预留手机号
	 * @param outOrderNo
	 *            外部订单号（唯一）
	 * @param url
	 *            易行通URL
	 * @param privateKey
	 *            易行通安全码
	 * @param partnerId
	 *            易行通商户ID
	 * @param returnUrl
	 *            易行通同步回调URL
	 * @param notifyUrl
	 *            易行通异步回调URL
	 * @return 请求结果
	 */
	public static String bankCardCheck(String name, String identityCard, String bankCard, String phone,
			String outOrderNo, String url, String privateKey, String partnerId, String returnUrl, String notifyUrl) {
		// 银行卡验证请求参数
		boolean isFourElement = true;

		Map<String, Object> object = new HashMap<String, Object>();
		object.put("realName", name);
		object.put("certNo", identityCard);
		object.put("bankCardNo", bankCard);
		object.put("mobileNo", phone);
		object.put("isFourElement", isFourElement);
		object.put("outOrderNo", outOrderNo);

		// 公共请求参数
		object.put("protocol", "httpPost");
		object.put("service", "installmentBankCardVerify");
		object.put("version", "1.0");
		object.put("partnerId", partnerId);
		object.put("orderNo", outOrderNo);
		object.put("merchOrderNo", "");
		object.put("signType", "MD5");
		object.put("returnUrl", returnUrl);
		object.put("notifyUrl", notifyUrl);
		String sign = StringUtil.md5Sign(object, false, privateKey);
		object.put("sign", sign);
		String result = Http.post(url, object, 60000);
		System.out.println("result==============" + result);
		return result;
	}
}
