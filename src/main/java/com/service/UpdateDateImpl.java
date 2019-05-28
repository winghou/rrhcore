package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.ForDateMapper;
import com.dao.IfmLoanApplMapper;
import com.frame.Consts;
import com.model.IfmLoanAppl;
import com.service.intf.UpdateDate4;
import com.util.DateUtil;
import com.util.StringUtil;
@Service
public class UpdateDateImpl implements UpdateDate4 {
	@Autowired 
	private IfmLoanApplMapper ifmLoanApplMapper;
	@Autowired
	private ForDateMapper forDateMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject modifyDate(JSONObject params) throws Exception {
		JSONObject detail=new JSONObject();
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		List<Map<String,Object>> ds=forDateMapper.selectDate4();
		for (Map<String, Object> map : ds) {
		String date=StringUtil.toString(map.get("finishdate"));
		String id=StringUtil.toString(map.get("bizid"));
		IfmLoanAppl ifmLoanAppl=ifmLoanApplMapper.selectByPrimaryKey(Integer.parseInt(id));
		ifmLoanAppl.setApprDate(DateUtil.parseDate(date));
		ifmLoanApplMapper.updateByPrimaryKeySelective(ifmLoanAppl);
		}
		return null;
	}

}
