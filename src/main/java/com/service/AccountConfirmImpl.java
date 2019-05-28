package com.service;


import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.service.intf.AccountConfirmService;
@Service
public class AccountConfirmImpl implements AccountConfirmService {
	
	@Override
	public JSONObject accountConfirm(JSONObject params) {
		/*List<Map<String, Object>> maps=personalDataMapper.selectNotwithDrawId();
		for (Map<String, Object> map : maps) {
			InsertAllDataImpl t1=new InsertAllDataImpl(insertDataMapper,map);
			Thread m1=new Thread(t1,"Window 1");
			m1.start();
		}
		Map<String ,Object> mao=new HashMap<>();
		mao.put("withDrawId", aPPWithDrawAppl.getId());
		mao.put("apprId", appLoanAppl.getId());
		mao.put("userId", userId);
		InsertAllDataImpl t1=new InsertAllDataImpl(insertDataMapper,mao);
		Thread m1=new Thread(t1,"Window 1");
		m1.start();*/
		
		
		return null;
	}

}
