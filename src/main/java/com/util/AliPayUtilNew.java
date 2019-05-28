package com.util;

import java.util.HashMap;
import java.util.Map;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.model.AppAliPay;

public class AliPayUtilNew {
	
	public static String APPID;
	public static String SERVICE;
	public static String PARTNER;
	public static String SELLER;
	public static String METHOD;
	public static String CHARSET;
	public static String NOTIFY_URL;
	public static String PAYMENT_TYPE;
	public static String IT_B_PAY;
	public static String PRIVATEKEY;
	public static String PUBLICKEY;
	public static String GATEWAYURL;
	public static String PRODUCTCODE;
	public static String SIGNTYPE;

	/**
	 * 给客户端传值支付
	 * @param outTradeNo
	 * @param subject
	 * @param body
	 * @param price
	 * @return
	 */
	public static Map<String, String> getOrderInfoMap(String uuid, String subject, String body, String price) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderInfo", "");
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient(GATEWAYURL, PRODUCTCODE, PRIVATEKEY, "json", "utf-8", PUBLICKEY, SIGNTYPE);
		//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setOutTradeNo(uuid);
		model.setTimeoutExpress(IT_B_PAY);
		model.setTotalAmount(price);
		model.setProductCode("QUICK_MSECURITY_PAY");
		request.setBizModel(model);
		request.setNotifyUrl(NOTIFY_URL);
		try {
			// 这里和普通的接口调用不同，使用的是sdkExecute
			AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
			String orderInfo = response.getBody();
			map.put("orderInfo", orderInfo);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 支付宝订单交易状态查询接口（入参传一个即可）
	 * @param outTradeNo 外部订单号
	 * @param tradeNo 支付宝交易号
	 * @return
	 */
	public static Map<String, Object> getOrderStatus(String outTradeNo, String tradeNo, AppAliPay appAliPay){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tradeStatus", "");
		//实例化客户端
		AlipayClient alipayClient = new DefaultAlipayClient(appAliPay.getGateWayUrl(), appAliPay.getProductCode(), appAliPay.getPrivateKey2(), "json", "GBK", appAliPay.getPublicKey2(), appAliPay.getSignType());
		AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
		request.setBizContent("{" + "\"out_trade_no\":\"" + outTradeNo + "\"," + "\"trade_no\":\"" + tradeNo + "\"}");
		AlipayTradeQueryResponse response;
		try {
			response = alipayClient.execute(request);
			if("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())){ //交易号不存在
				map.put("tradeStatus", "ACQ.TRADE_NOT_EXIST");
			}
			if(response.isSuccess()){
				String tradeStatus = StringUtil.nvl(response.getTradeStatus()); //交易状态
				map.put("tradeStatus", tradeStatus);
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return map;
	}
	
}
