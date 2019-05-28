package com.service.intf;

import com.alibaba.fastjson.JSONObject;

public interface ContactsConfirmService {
	public JSONObject contactsConfirm(JSONObject params) throws Exception;
	public JSONObject queryContacts(JSONObject params);
	public JSONObject saveContacts(JSONObject params) throws Exception;
	public JSONObject savePhoneList(JSONObject params) throws Exception;

}
