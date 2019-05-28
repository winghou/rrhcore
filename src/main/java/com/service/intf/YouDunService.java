package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface YouDunService {

	public JSONObject saveYouDunOCRInfo(JSONObject params) throws Exception;
	public JSONObject saveYouDunIDCheckInfo(JSONObject params) throws Exception;
	/* @author yang.wu
	 * 类名称： getYouDunSign
	 * 创建时间：2017年5月11日 下午5:36:16
	 * @param params
	 * @return JSONObject
	 * 类描述：有盾签名
	 */
	public JSONObject getYouDunSign(JSONObject params) throws Exception;
	public JSONObject getYouDunVerifyScore(JSONObject params) throws Exception;
	public JSONObject checkIDUseOrNot(JSONObject params) throws Exception;
}
