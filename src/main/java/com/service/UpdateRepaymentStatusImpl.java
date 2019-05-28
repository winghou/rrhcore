package com.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.frame.Consts;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.service.intf.UpdateRepaymentStatusService;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class UpdateRepaymentStatusImpl implements UpdateRepaymentStatusService {
	@Autowired
	private APPPayPlanMapper aPPPayPlanMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateRepaymentStatus(JSONObject params) throws Exception {
		JSONObject detail=new JSONObject();
		String repayDetailIds=JsonUtil.getJStringAndCheck(params, "repayDetailIds",null,false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		String[] repayIds=repayDetailIds.split(",");
		for (String i : repayIds) {
			APPPayPlan aPPPayPlan=aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(i));
			APPWithDrawAppl aPPWithDrawAppl=aPPWithDrawApplMapper.selectByPrimaryKey(aPPPayPlan.getWithdrawId());
			aPPPayPlan.setStatus("1");
			aPPPayPlanMapper.updateByPrimaryKeySelective(aPPPayPlan);
			String cs=StringUtil.toString(aPPPayPlan.getCurperiods());
			if(cs.equals(aPPWithDrawAppl.getBorrowPerions())){
				aPPWithDrawAppl.setStatus("2");
				aPPWithDrawApplMapper.updateByPrimaryKeySelective(aPPWithDrawAppl);
			}
		}
		return null;
	}
}
