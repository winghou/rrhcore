package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface OrderDetailAndMessageService {
	public JSONObject queryOrderDetail(JSONObject params) throws Exception ;
	public JSONObject queryMessaheCentre(JSONObject params);
	public JSONObject queryOneTypeMessage(JSONObject params);
	public JSONObject queryOneMessageByMesId(JSONObject params) throws Exception;
	public JSONObject queryMessageAndAnnouncement(JSONObject params) throws Exception;
	public JSONObject signMessageIsRead(JSONObject params) throws Exception;

}
