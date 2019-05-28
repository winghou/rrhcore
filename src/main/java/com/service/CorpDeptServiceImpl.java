package com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.CorpDeptMapper;
import com.frame.Consts;
import com.model.CorpDept;
import com.service.intf.CorpDeptServiceIntf;
import com.util.JsonUtil;
//import com.dao.TVarDefMapper;
//import com.dao.TVarPriceMapper;
//import com.model.TVarDef;
//import com.model.TVarPrice;
import com.util.pagehelper.PageHelper;
import com.util.pagehelper.PageInfo;

@Service("corpDeptService")
public class CorpDeptServiceImpl implements CorpDeptServiceIntf {

	@Autowired
	private CorpDeptMapper corpDeptMapper;

	@Override
	public JSONObject queryDeptList(JSONObject params) {
		JSONObject detail = new JSONObject();
		Integer pageNo = JsonUtil.getJIntAndCheck(params, "pageNo", 0, false, detail);
		Integer onePageNum = JsonUtil.getJIntAndCheck(params, "onePageNum", 10, false, detail);
		String corpId = JsonUtil.getJStringAndCheck(params, "corpId", null, true, detail);
		String deptName = JsonUtil.getJStringAndCheck(params, "deptName", null, false, detail);
		String parentDeptId = JsonUtil.getJStringAndCheck(params, "parentDeptId", null, false, detail);
		String contact = JsonUtil.getJStringAndCheck(params, "contact", null, false, detail);
		String mobile = JsonUtil.getJStringAndCheck(params, "mobile", null, false, detail);
		//String operUserId = JsonUtil.getJString(params, "operUserId");


		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		CorpDept corpDept = new CorpDept();
		corpDept.setCorpId(corpId);
		corpDept.setDeptName(deptName);
		corpDept.setStatus(Consts.STATUS_ENABLE);
		corpDept.setContactName(contact);
		corpDept.setContactPhone(mobile);
		corpDept.setParentId(parentDeptId);
		PageHelper.startPage(pageNo, onePageNum);
		List<CorpDept> corpDeptList = corpDeptMapper.selectCorpDept(corpDept);
		PageInfo<CorpDept> page = new PageInfo<CorpDept>(corpDeptList);
		List<CorpDept> corpDeptListByPage = page.getList();
		JSONArray dataList = new JSONArray();
		for (CorpDept cd : corpDeptListByPage) {
			JSONObject jo = new JSONObject();
			jo.put("deptId", cd.getDeptId());
			jo.put("deptName", cd.getDeptName());
			jo.put("parentDeptId", cd.getParentId());
			jo.put("parentDeptName", cd.getParentDeptName());
			jo.put("contact", cd.getContactName());
			jo.put("mobile", cd.getContactPhone());
			jo.put("email", cd.getContactEmail());
			jo.put("address", cd.getAddress());
			jo.put("isAuth", cd.getIsAuth());
			jo.put("isSuperDept", cd.getIsSuperDept());
			dataList.add(jo);
		}
		detail.put("dataList", dataList);
		detail.put(Consts.PAGE_NO, page.getPageNum());
		detail.put(Consts.PAGE_AMT, page.getPages());
		detail.put(Consts.RECORD_AMT, page.getTotal());
		return detail;
	}
}
