package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppInsuranceMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppInsurance;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppUser;
import com.service.intf.AppInsuranceService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
public class AppInsuranceServiceImpl implements AppInsuranceService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppInsuranceMapper appInsuranceMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	/* (non-Javadoc)
	 * @see com.service.intf.AppInsuranceService#getInsurance(com.alibaba.fastjson.JSONObject)
	 * 保险
	 */
	@Override
	public JSONObject getInsurance(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //用户id
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		List<AppInsurance> appInsurancesList = appInsuranceMapper.selectAppInsurance();
		JSONArray supportInsurances = new JSONArray();
		for (AppInsurance appInsurance : appInsurancesList) {
			JSONObject list = new JSONObject();
			list.put("insuranceName", StringUtil.nvl(appInsurance.getInsurance_name())); //保险名称
			list.put("insuranceType", StringUtil.nvl(appInsurance.getType())); //保险类型
			list.put("insuranceUrl", StringUtil.nvl(appInsurance.getInsurance_url())); //保险链接
			supportInsurances.add(list);
		}
		detail.put("appInsurancesList", supportInsurances);
		detail.put("customName", StringUtil.nvl(appLoanCtm.getCustomName()));
		return detail;
	}

}
