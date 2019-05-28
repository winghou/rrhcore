package com.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

import com.dao.*;
import com.frame.RequestTemplate;
import com.model.*;
import com.service.intf.ZhiMaCreditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.frame.Consts;
import com.service.intf.AppGoSumbitService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class AppGoSumbitImpl  implements AppGoSumbitService  {
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private IfmWfmStatusMapper ifmWfmStatusMapper;
	@Autowired
	private IfmWfmLogMapper ifmWfmLogMapper;
	@Autowired
	private IfmTemplateMapper ifmTemplateMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private IfmWfmScoreMapper ifmWfmScoreMapper;
	@Autowired
	private AppDecisionAndControlRecordMapper appDecisionAndControlRecordMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppWithDrawStatusMapper appWithDrawStatusMapper;
	@Autowired
	private AppZhimaScoreMapper ifmZhimaScoreMapper;
	@Autowired
	private ZhiMaCreditService zhiMaCreditService;

	/**
	 * 提交订单供风控审核(2.X版本逻辑)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject AppgoSumbit(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String withDrawId = JsonUtil.getJStringAndCheck(params, "withDrawId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		// 先保存instance表
		IfmWfmStatus wfs = new IfmWfmStatus();
		wfs.setBizid(withDrawId);
		wfs.setCreatedate(new Date());
		if ("0".equals(StringUtil.nvl(appLoanAppl.getPrvince()))) {
			wfs.setCustId(appLoanCtm.getCustomName() + "(学生)");
		} else if ("1".equals(StringUtil.nvl(appLoanAppl.getPrvince()))) {
			wfs.setCustId(appLoanCtm.getCustomName() + "(成人)");
		}
		wfs.setStatus("0");
		if ("0".equals(StringUtil.nvl(appLoanAppl.getPrvince()))) {
			wfs.setApplname("随心花APP端(学生)");
		} else if ("1".equals(StringUtil.nvl(appLoanAppl.getPrvince()))) {
			wfs.setApplname("随心花APP端(成人)");
		}
		wfs.setTpid(4);
		ifmWfmStatusMapper.insertSelective(wfs);
		// 复制模板表
		List<IfmTemplate> templates = ifmTemplateMapper.qryTemplate(4);
		for (IfmTemplate fp : templates) {
			IfmWfmLog wfm = new IfmWfmLog();
			wfm.setBtncode(StringUtil.toString(fp.getBtncode()));
			wfm.setWfId(wfs.getId() + "");
			wfm.setBtnname(StringUtil.toString(fp.getBtnname()));
			wfm.setCreatedate(new Date());
			wfm.setFlag(StringUtil.toString(fp.getFlag()));
			wfm.setNextrule(StringUtil.toString(fp.getNextrule()));
			wfm.setNodeid(StringUtil.toString(fp.getId()));
			wfm.setNodename(StringUtil.toString(fp.getNodename()));
			if (fp.getStage() == 1) {
				wfm.setResult("0");
			} else if (fp.getStage() == 0) {
				wfm.setResult("1");
				wfm.setFinalopruser(StringUtil.toString(userId));
				wfm.setFinaloprname(appLoanAppl.getItemCode());
				wfm.setStartdate(new Date());
				wfm.setFinishdate(new Date());
			} else {
				wfm.setResult("-1");
			}
			wfm.setCycle(0);
			wfm.setRoles(StringUtil.toString(fp.getRoles()));
			wfm.setStage(fp.getStage());
			wfm.setUri(StringUtil.toString(fp.getUri()));
			wfm.setWfId(StringUtil.toString(wfs.getId()));
			ifmWfmLogMapper.insertSelective(wfm);
		}
		List<Map<String, Object>> score = ifmBaseDictMapper.selectBaseDict("FKWD");
		for (Map<String, Object> map2 : score) {
			IfmWfmScore ifmWfmScore = new IfmWfmScore();
			ifmWfmScore.setWfId(StringUtil.toString(wfs.getId()));
			ifmWfmScore.setItemId(StringUtil.toString(map2.get("id")));
			ifmWfmScore.setItemDesc(StringUtil.toString(map2.get("ITEM_VALUE")));
			ifmWfmScore.setContent(StringUtil.toString(map2.get("ITEM_KEY")));
			if ("芝麻信用分".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
				ifmWfmScore.setOther_info((StringUtil.toString(appLoanAppl.getZhimaCreditScore())));
			}
			if ("反欺诈信息分数（蚂蚁金服）".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
				ifmWfmScore.setOther_info((StringUtil.toString(appLoanAppl.getZhimaIvsScore())));
			}
			if ("是否为行业关注人员".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
				if ("true".equals(StringUtil.toString(appLoanAppl.getWatchlistiiIsMatched()))) {
					ifmWfmScore.setOther_info("0");
					ifmWfmScore.setCreditDesc(StringUtil.toString(map2.get("watchlistii_detail")));
				} else if ("false".equals(StringUtil.toString(appLoanAppl.getWatchlistiiIsMatched()))) {
					ifmWfmScore.setOther_info("1");
				}
			}
			ifmWfmScore.setScore(0);
			ifmWfmScoreMapper.insertSelective(ifmWfmScore);
		}
		appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
		return null;
	}
	
	/**
	 *  生成用户资料审核订单(3.X版本逻辑)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject sumbitUserInfoToDecisionEngine(JSONObject param) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(param, "userId", null, false, detail);
		String engineType = JsonUtil.getJStringAndCheck(param, "engineType", null, false, detail);
		String withId = JsonUtil.getJStringAndCheck(param, "withDrawId", null, false, detail);
		String sign = JsonUtil.getJStringAndCheck(param, "sign", null, false, detail);
		try {
			sign = URLDecoder.decode(sign, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
			AppZhimaScore appZhimaScore = ifmZhimaScoreMapper.selectZhimaScoreByUserId(appUser.getUserid());
			if("0".equals(engineType)){ //授信
				String status = StringUtil.nvl(loanAppl.getStatus());
				if(!"1".equals(status)){ //只有授信中状态下的用户才能创建授信流程
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					return detail;
				}
//				String zhimaStatus = appZhimaScore.getStatus();
//				//用户授权芝麻信用给随心花OPENID未失效的情形
//				if(zhimaStatus =="" || zhimaStatus == null || zhimaStatus.equals("0")){
//					Date saveTime = appZhimaScore.getAuticDate();
//					Calendar cal = Calendar.getInstance();
//					cal.setTime(saveTime);
//					int day = cal.get(Calendar.DATE);
//					//获取系统当前时间
//					Date sysDate=new Date();
//			        Date lastDate=DateUtil.getLastDay(sysDate);
//					//请求芝麻信用时保存时间早于当月6号24：00,重新获取芝麻信息进行更新
//					//if(day < 7){
//					if((day<7 && sysDate.getDay()>=7)||(sysDate.getDay()<7&&saveTime.getTime()<lastDate.getTime())){	
//						JSONObject zhimaRepeatDetail = zhiMaCreditService.zhimaCallBack(param);
//						String zhimaCreditScore = zhimaRepeatDetail.getString("creditScore");
//						String ivsScore = zhimaRepeatDetail.getString("ivsScore");
//						String watchlistMatched = zhimaRepeatDetail.getString("watchlist");
//						if(zhimaCreditScore==null || zhimaCreditScore.equals("") || ivsScore==null || ivsScore.equals("")){
//							//根据openId重新请求芝麻信用，若芝麻分或者反欺诈分没有获取到将相应表中的openId改为失效状态
//							appZhimaScore.setAuticDate(new Date());
//							appZhimaScore.setStatus("1");
//							loanAppl.setZhimaStatus("1");
//							loanAppl.setStatus("8");
//							ifmZhimaScoreMapper.updateByPrimaryKeySelective(appZhimaScore);
//							appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//							detail.put(Consts.RESULT, ErrorCode.SUCCESS);
//							detail.put(Consts.RESULT_NOTE, "您的芝麻信息已失效，请重新授权");
//							return  detail;
//						}
//					}
//				}else{
//					appZhimaScore.setAuticDate(new Date());
//					appZhimaScore.setStatus("1");
//					loanAppl.setStatus("8");
//					loanAppl.setZhimaStatus("1");
//					ifmZhimaScoreMapper.updateByPrimaryKeySelective(appZhimaScore);
//					appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
//					detail.put(Consts.RESULT_NOTE, "您的芝麻信息已失效，请重新授权");
//					return detail;
//				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("tpid", "10");
				map.put("appr_id", loanAppl.getId());
				List<IfmWfmStatus> wfmStatus = ifmWfmStatusMapper.selectIfmWfmStatus(map);
				if(null != wfmStatus && wfmStatus.size() > 0){
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					return detail;
				}
			}else if("1".equals(engineType)){ //提现
//				String zhimaStatus = appZhimaScore.getStatus();
				//用户授权芝麻信用给随心花OPENID未失效的情形
//				if(zhimaStatus =="" || zhimaStatus == null || zhimaStatus.equals("0")){
//					Date saveTime = appZhimaScore.getAuticDate();
//					Calendar cal = Calendar.getInstance();
//					cal.setTime(saveTime);
//					int day = cal.get(Calendar.DATE);
//					//请求芝麻信用时保存时间早于当月6号24：00,重新获取芝麻信息进行更新
//					Date sysDate=new Date();
//				    Date lastDate=DateUtil.getLastDay(sysDate);
//					//if(day < 7){
//					if((day<7 && sysDate.getDay()>=7)||(sysDate.getDay()<7&&saveTime.getTime()<lastDate.getTime())){
//						//重新调用芝麻信息获取接口
//						JSONObject zhimaRepeatDetail = zhiMaCreditService.zhimaCallBack(param);
//						//获取芝麻信息分
//						String zhimaCreditScore = zhimaRepeatDetail.getString("creditScore");
//						//反欺诈分
//						String ivsScore = zhimaRepeatDetail.getString("ivsScore");
//						String watchlistMatched = zhimaRepeatDetail.getString("watchlist");
//						if(zhimaCreditScore==null || zhimaCreditScore.equals("") || ivsScore==null || ivsScore.equals("")){
//							//根据openId重新请求芝麻信用，若芝麻分或者反欺诈分没有获取到将相应表中的openId改为失效状态
//							appZhimaScore.setAuticDate(new Date());
//							appZhimaScore.setStatus("1");
//							loanAppl.setZhimaStatus("1");
//							loanAppl.setStatus("8");
//							ifmZhimaScoreMapper.updateByPrimaryKeySelective(appZhimaScore);
//							appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//							detail.put(Consts.RESULT, ErrorCode.SUCCESS);
//							detail.put(Consts.RESULT_NOTE, "您的芝麻信息已失效，请重新授权");
//							return  detail;
//						}
//					}
//				}else{
//					appZhimaScore.setAuticDate(new Date());
//					appZhimaScore.setStatus("1");
//					loanAppl.setZhimaStatus("1");
//					loanAppl.setStatus("8");
//					ifmZhimaScoreMapper.updateByPrimaryKeySelective(appZhimaScore);
//					appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//					detail.put(Consts.RESULT,ErrorCode.SUCCESS);
//					detail.put(Consts.RESULT_NOTE, "您的芝麻信息已失效，请重新授权");
//					return detail;
//				}
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("tpid", "4");
				map.put("appr_id", withId);
				List<IfmWfmStatus> wfmStatus = ifmWfmStatusMapper.selectIfmWfmStatus(map);
				if(null != wfmStatus && wfmStatus.size() > 0){
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					detail.put(Consts.RESULT_NOTE, "该笔订单已创建，请重试");
					return detail;
				}
			}
			// 先保存instance表
			IfmWfmStatus wfs = new IfmWfmStatus();
			if("0".equals(engineType)){
				wfs.setBizid(loanAppl.getId() + "");
				wfs.setTpid(10);
			}else if("1".equals(engineType)){
				wfs.setBizid(withId);
				wfs.setTpid(4);
			}
			wfs.setCreatedate(new Date());
			if ("0".equals(loanAppl.getPrvince())) {
				wfs.setCustId(appLoanCtm.getCustomName() + "(学生)");
				wfs.setApplname("随心花APP端(学生)");
			} else {
				wfs.setCustId(appLoanCtm.getCustomName() + "(成人)");
				wfs.setApplname("随心花APP端(成人)");
			}
			wfs.setStatus("0");
			ifmWfmStatusMapper.insertSelective(wfs);
			// 保存ifm_wfm_log表
			List<IfmTemplate> templates = null;
			if("0".equals(engineType)){
				templates = ifmTemplateMapper.qryTemplate(10);
			}else if("1".equals(engineType)){
				templates = ifmTemplateMapper.qryTemplate(4);
			}
			int wfmLogId = 0;
			// 决策引擎阈值开关
			String flag = "true";
			List<Map<String, Object>> lists1 = ifmBaseDictMapper.selectBaseDict("DECISION_THRESHOLD");
			if(null != lists1 && lists1.size() > 0){
				flag = StringUtil.nvl(lists1.get(0).get("ITEM_VALUE"));
			}
			if (null != templates && templates.size() > 0) {
				IfmWfmLog wfmLog = null;
				for (IfmTemplate template : templates) {
					wfmLog = new IfmWfmLog();
					wfmLog.setBtncode(StringUtil.toString(template.getBtncode()));
					wfmLog.setWfId(wfs.getId() + "");
					wfmLog.setBtnname(StringUtil.toString(template.getBtnname()));
					wfmLog.setCreatedate(new Date());
					wfmLog.setFlag(StringUtil.toString(template.getFlag()));
					wfmLog.setNextrule(StringUtil.toString(template.getNextrule()));
					wfmLog.setNodeid(StringUtil.toString(template.getId()));
					wfmLog.setNodename(StringUtil.toString(template.getNodename()));
					if (template.getStage() == 1) {
						wfmLog.setResult("0");
						if ("true".equals(flag)) {
							List<AppUser> users = appUserMapper.selectByUserName("决策引擎");
							AppUser user2 = null;
							if (null != users && users.size() > 0) {
								user2 = users.get(0);
								wfmLog.setFinalopruser(user2.getUserid() + "");
								wfmLog.setFinaloprname(user2.getUserName());
								wfmLog.setStartdate(new Date());
							}
						}
					} else if (template.getStage() == 0) {
						wfmLog.setResult("1");
						wfmLog.setFinalopruser(loanAppl.getUserId() + "");
						wfmLog.setFinaloprname(loanAppl.getItemCode());
						wfmLog.setStartdate(new Date());
						wfmLog.setFinishdate(new Date());
					} else {
						wfmLog.setResult("-1");
					}
					wfmLog.setCycle(0);
					wfmLog.setRoles(StringUtil.toString(template.getRoles()));
					wfmLog.setStage(template.getStage());
					wfmLog.setUri(StringUtil.toString(template.getUri()));
					ifmWfmLogMapper.insertSelective(wfmLog);
					if(1 == wfmLog.getStage()){
						wfmLogId = wfmLog.getId();
						if("true".equals(flag)){
							AppWithDrawStatus withDrawStatus = new AppWithDrawStatus();
							withDrawStatus.setApprId(loanAppl.getId());
							withDrawStatus.setSendCount(0);
							withDrawStatus.setStatus("0");
							withDrawStatus.setWfmLogId(wfmLog.getId());
							if("0".equals(engineType)){
								withDrawStatus.setType("0");
							}else if("1".equals(engineType)){
								withDrawStatus.setType("1");
								withDrawStatus.setWithdrawId(Integer.parseInt(withId));
							}
							appWithDrawStatusMapper.insertSelective(withDrawStatus);
						}
					}
				}
			}
			// 保存打分表
			List<Map<String, Object>> score = ifmBaseDictMapper.selectBaseDict("FKWD");
			IfmWfmScore ifmWfmScore = null;
			for (Map<String, Object> map2 : score) {
				ifmWfmScore = new IfmWfmScore();
				ifmWfmScore.setWfId(StringUtil.toString(wfs.getId()));
				ifmWfmScore.setItemId(StringUtil.toString(map2.get("id")));
				ifmWfmScore.setItemDesc(StringUtil.toString(map2.get("ITEM_VALUE")));
				ifmWfmScore.setContent(StringUtil.toString(map2.get("ITEM_KEY")));
				if ("芝麻信用分".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
					ifmWfmScore.setOther_info((StringUtil.toString(loanAppl.getZhimaCreditScore())));
				}
				if ("反欺诈信息分数（蚂蚁金服）".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
					ifmWfmScore.setOther_info((StringUtil.toString(loanAppl.getZhimaIvsScore())));
				}
				if ("是否为行业关注人员".equals(StringUtil.toString(map2.get("ITEM_VALUE")))) {
					if ("true".equals(StringUtil.toString(loanAppl.getWatchlistiiIsMatched()))) {
						ifmWfmScore.setOther_info("0");
						ifmWfmScore.setCreditDesc(StringUtil.toString(map2.get("watchlistii_detail")));
					} else if ("false".equals(StringUtil.toString(loanAppl.getWatchlistiiIsMatched()))) {
						ifmWfmScore.setOther_info("1");
					}
				}
				ifmWfmScore.setScore(0);
				ifmWfmScoreMapper.insertSelective(ifmWfmScore);
			}
			// 保存决策引擎
			if("0".equals(engineType)){
				AppDecisionAndControlRecord engineRecord = appDecisionAndControlRecordMapper.selectByOrderId(loanAppl.getId() + "#" + wfmLogId);
				if(null == engineRecord){
					engineRecord = new AppDecisionAndControlRecord();
					engineRecord.setApprId(loanAppl.getId());
					engineRecord.setOrderId(loanAppl.getId() + "#" + wfmLogId);
					engineRecord.setCreateTime(new Date());
					engineRecord.setWfId(wfs.getId());
					engineRecord.setWfmLogId(wfmLogId);
					if(appZhimaScore.getStatus().equals("1")){ //openID失效的情形
						engineRecord.setOpen_id_status("1");
					}else { //openID未失效的情形
						engineRecord.setCredit_score(appZhimaScore.getZhimaCreditScore());
						engineRecord.setIvs_score(appZhimaScore.getZhimaIvsScore());
						engineRecord.setWatchlist_matched(appZhimaScore.getWatchlistiiIsMatched());
						engineRecord.setOpen_id_status("0");
					}
					appDecisionAndControlRecordMapper.insertSelective(engineRecord);
					detail.put("order_id", loanAppl.getId() + "#" + wfmLogId);
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				}else{
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
					detail.put(Consts.RESULT_NOTE, "已进行授信");
				}
			}else if("1".equals(engineType)){
				AppDecisionAndControlRecord engineRecord = appDecisionAndControlRecordMapper.selectByWithId(Integer.parseInt(withId));
				if(null == engineRecord){
					engineRecord = new AppDecisionAndControlRecord();
					engineRecord.setOrderId(withId + "#" + wfmLogId);
					engineRecord.setWithdrawId(Integer.parseInt(withId));
					engineRecord.setCreateTime(new Date());
					engineRecord.setWfId(wfs.getId());
					engineRecord.setWfmLogId(wfmLogId);
					if(appZhimaScore.getStatus().equals("1")){ //openID失效的情形
						engineRecord.setOpen_id_status("1");
					}else { //openID未失效的情形
						engineRecord.setCredit_score(appZhimaScore.getZhimaCreditScore());
						engineRecord.setIvs_score(appZhimaScore.getZhimaIvsScore());
						engineRecord.setWatchlist_matched(appZhimaScore.getWatchlistiiIsMatched());
						engineRecord.setOpen_id_status("0");
					}
					appDecisionAndControlRecordMapper.insertSelective(engineRecord);
					detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "该笔订单已创建，请重试");
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录,请先登录");
		}
		return detail;
	}
	
	

}
