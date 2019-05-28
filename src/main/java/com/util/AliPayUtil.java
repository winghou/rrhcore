package com.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.alipay.config.AlipayConfig;
import com.alipay.sign.RSA;

public class AliPayUtil {
	
	public static  String APPID;
	public static  String SERVICE;
	public static  String PARTNER;
	public static  String SELLER;
	public static  String METHOD;
	public static  String CHARSET;
	public static  String NOTIFY_URL;
	public static  String PAYMENT_TYPE;
	public static  String IT_B_PAY;
	public static  String PRIVATEKEY;

	public static Map<String, String> getOrderInfoMap(String OutTradeNo, String subject, String body, String price) {
		Map<String, String> map = new HashMap<String, String>();
		// 签约合作者身份ID
		String orderInfo = "service=" + "\"" + SERVICE + "\"";
		// 签约卖家支付宝账号
		orderInfo += "&partner=" + "\"" + PARTNER + "\"";
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
		orderInfo += "&_input_charset=" + "\"" + CHARSET + "\"";
		orderInfo += "&it_b_pay=" + "\"" + IT_B_PAY + "\"";
		orderInfo += "&notify_url=" + "\"" + NOTIFY_URL + "\"";
		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + OutTradeNo + "\"";
		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";
		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";
		// 商品详情
		orderInfo += "&payment_type=" + "\"" + PAYMENT_TYPE + "\"";
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		try {
			//将待签名字符串使用私钥签名。
			String rsa_sign=URLEncoder.encode(RSA.sign(orderInfo, PRIVATEKEY, CHARSET),CHARSET);
			orderInfo=orderInfo+"&sign=\""+rsa_sign+"\"&sign_type=\""+AlipayConfig.sign_type+"\"";
			map.put("orderInfo", orderInfo);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return map;
		
	}
}
