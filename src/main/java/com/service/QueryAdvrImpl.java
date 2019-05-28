package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppAdvertMapper;
import com.frame.Consts;
import com.model.AppAdvert;
import com.service.intf.QueryAdvrService;
import com.util.JsonUtil;

@Service
public class QueryAdvrImpl implements QueryAdvrService {
	
	@Autowired
	private AppAdvertMapper appAdvertMapper;

	@Override
	public JSONObject queryAdvr(JSONObject params_) {
		JSONObject detail=new JSONObject();
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		JSONArray dataList = new JSONArray();
		List<AppAdvert> appAdverts=appAdvertMapper.queryAllAdvr();
		for (AppAdvert appAdvert : appAdverts) {
			JSONObject jo=new JSONObject();
			jo.put("url", appAdvert.getUrl());
			dataList.add(jo);
		}
		detail.put("dataList", dataList);
		return detail;
	}
	
	@Override
	public JSONObject queryAdvrByModule(JSONObject params) {
		JSONObject detail=new JSONObject();
		String module=JsonUtil.getJStringAndCheck(params, "module", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		JSONArray dataList = new JSONArray();
		List<AppAdvert> appAdverts=appAdvertMapper.queryAdvrByModule(module);
		for (AppAdvert appAdvert : appAdverts) {
			JSONObject jo=new JSONObject();
			jo.put("url", appAdvert.getUrl());
			jo.put("picJumpUrl", appAdvert.getPicJumpUrl()+"");
			dataList.add(jo);
		}
		detail.put("dataList", dataList);
		return detail;
	}

}
