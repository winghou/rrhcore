package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.dao.IfmWfmStatusMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppUser;
import com.model.IfmWfmStatus;
import com.service.intf.DecisionEngineService;
import com.util.APIHttpClient;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class DecisionEngineServiceImpl implements DecisionEngineService{

	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private IfmWfmStatusMapper ifmWfmStatusMapper;
	
	/**
	 * 检查用户在决策引擎是否已查到报告
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject checkReportExsit(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String token = JsonUtil.getJStringAndCheck(params, "token", null, false, detail);
		String report_id = JsonUtil.getJStringAndCheck(params, "report_id", null, false, detail);
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if(null != appUser){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			if("3".equals(StringUtil.nvl(loanAppl.getStatus()))){ //已被拒绝用户
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "授信未通过，暂不可重复授信");
				return detail;
			}else if("1".equals(StringUtil.nvl(loanAppl.getStatus()))){ //正在授信用户
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "正在授信中，不可重复授信");
				return detail;
			}else if("2".equals(StringUtil.nvl(loanAppl.getStatus()))){ //授信通过用户
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "授信已通过，不可重复授信");
				return detail;
			}
			AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("cntType", "2");
			m.put("apprId", loanAppl.getId());
			AppLoanCtmCnt loanCtmCnt = appLoanCtmCntMapper.queryByType(m);
			if(null != loanCtmCnt){
				// 没有审核中授信订单的情况下才能查询报告
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("tpid", "10");
				map.put("appr_id", loanAppl.getId());
				List<IfmWfmStatus> wfmStatus = ifmWfmStatusMapper.selectIfmWfmStatus(map);
				if(null == wfmStatus || wfmStatus.size() <= 0){
					// 决策引擎URL
					List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("DECISION_ENGINE_CREDIT_URL");
					List<Map<String, Object>> list = ifmBaseDictMapper.selectBaseDict("DECISION_ENGINE_ASYNCHRONOUS_URL");  //决策引擎运营商报告回调URL
					String url = StringUtil.nvl(maps.get(0).get("ITEM_VALUE")) + "/putOrderID";
	//				String url = "http://192.168.1.236/action/engine/putOrderID";
					JSONObject object = new JSONObject();
					object.put("phone", loanCtmCnt.getCntCommt());
					object.put("order_id", appUser.getMch_version());
					object.put("postUrl", list.get(0).get("ITEM_VALUE"));
					object.put("token", token);
					object.put("report_id", report_id);
					String result = APIHttpClient.doPost(url, object, 30000);
					JSONObject object2 = (JSONObject) JSON.parse(result);
					String success = StringUtil.nvl(object2.get("success"));
					if("true".equals(success)){
						loanAppl.setStatus("1");
						appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
						detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					}else if("false".equals(success)){ //决策引擎无记录
						loanCtm.setSchedule_status("7");
						loanAppl.setStatus("0");
						appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
						appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "请重新认证运营商");
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "正在授信中，不可重复授信");
				}
			}else{ //无运营商记录
				loanCtm.setSchedule_status("7");
				loanAppl.setStatus("0");
				appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
				appLoanCtmMapper.updateByPrimaryKeySelective(loanCtm);
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请重新认证运营商");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
		}
		return detail;
	}

}
