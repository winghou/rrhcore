package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.MchInterviewMapper;
import com.model.MchInterview;
import com.service.intf.MchInterviewService;
@Service
public class MchInterviewServiceImpl  implements MchInterviewService{

	@Autowired
	public MchInterviewMapper  mchInterviewMapper;
	@Override 
	public JSONObject save(JSONObject params) throws Exception {

		MchInterview  mchInterview=	JSONObject.toJavaObject(params.getJSONObject("params"), MchInterview.class);
		
		mchInterviewMapper.save(mchInterview);
		return null;
	}

	
}
