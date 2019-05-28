package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanOrgMapper;
import com.frame.Consts;
import com.model.AppLoanOrg;
import com.service.intf.QueryLoanOrgService;
import com.util.ErrorCode;

@Service
public class QueryLoanOrgImpl implements QueryLoanOrgService {
	@Autowired
	private AppLoanOrgMapper appLoanOrgMapper;
	

	@Override
	public JSONObject queryLoanOrg(JSONObject params) {
		JSONObject detail=new JSONObject();
		List<AppLoanOrg> orgs=appLoanOrgMapper.selectBystatus();
		JSONArray dataList=new JSONArray();
		if(null!=orgs&&orgs.size()>0){
		for (AppLoanOrg appLoanOrg : orgs) {
			JSONObject jo=new JSONObject();
			jo.put("loan_org_id", appLoanOrg.getLoanOrgId());
			jo.put("loan_org_name", appLoanOrg.getLoanOrgName());
			jo.put("org_pic_url", appLoanOrg.getOrgPicUrl());
			jo.put("org_pic_url_log", appLoanOrg.getOrgPicUrlLog());
			jo.put("org_interface_url", appLoanOrg.getOrgInterfaceUrl()+"");
			jo.put("org_desc", appLoanOrg.getOrgDesc());
			jo.put("status", appLoanOrg.getStatus());
			dataList.add(jo);
		}
		detail.put("dataList", dataList);
	}else{
		detail.put(Consts.RESULT, ErrorCode.FAILED);
		detail.put(Consts.RESULT_NOTE, "暂无合作商");
	}
		return detail;
	}

}
