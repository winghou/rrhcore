package com.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppUser;
import com.service.intf.QueryRepayDetailService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class QueryRepayDetailImpl implements QueryRepayDetailService {
	@Autowired
	private APPPayPlanMapper aPPPayPlanMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;

	/**
	 * 查询分期订单详情
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject queryRepayDetail(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId",null,false, detail);
		String withDrawId = JsonUtil.getJStringAndCheck(params, "withDrawId",null,false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			userId = appUser.getUserid() + "";
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			if(null != loanAppl){
				List<APPWithDrawAppl> withDrawAppls = aPPWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
				if(null != withDrawAppls && withDrawAppls.size() > 0){
					int count = 0;
					for(APPWithDrawAppl withDrawAppl : withDrawAppls){
						if(withDrawAppl.getId() == Integer.parseInt(withDrawId)){
							count++;
						}
					}
					if(count == 0){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "该订单不属于您");
						return detail;
					}
					APPWithDrawAppl appWithDrawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withDrawId));
					// 总期数
					String totalPeriods = "1";
					if(null != appWithDrawAppl.getBorrowPerions()){
						totalPeriods = appWithDrawAppl.getBorrowPerions();
					}
					List<APPPayPlan> payPlans = aPPPayPlanMapper.selectAllByWithDrawId(Integer.parseInt(withDrawId));
					if(null != payPlans && payPlans.size() > 0){
						String repayId = "";			// 还款ID
						String currentPeriod = "";		// 当期期数
						String monthPay = "";			// 月应还
						String fxje = "";				// 罚息金额
						String reducAmt = "";			// 优惠金额
						String repayAmt = "";			// 还款金额
						String repayDate = "";			// 还款时间
						String orderStatus = "0";		// 订单状态  0：正常，1：逾期
						JSONArray jsonArray = new JSONArray();
						JSONObject object = null;
						for(APPPayPlan payPlan : payPlans){
							if("0".equals(payPlan.getStatus())){ //只显示未还款
								repayId = StringUtil.nvl(payPlan.getId());
								currentPeriod = StringUtil.nvl(payPlan.getCurperiods());
								monthPay = null == payPlan.getMonthPay() ? "0" : payPlan.getMonthPay() + "";
								fxje = null == payPlan.getFxje() ? "0" : payPlan.getFxje() + "";
								reducAmt = null == payPlan.getReducAmt() ? "0" : payPlan.getReducAmt() + "";
								repayAmt = StringUtil.formatNumberToDecimals(new BigDecimal(monthPay).add(new BigDecimal(fxje).subtract(new BigDecimal(reducAmt))).toString(), 2);
								repayDate = DateUtil.format(payPlan.getRepayDate(), "yyyy/MM/dd");
								if(payPlan.getDays() > 0){
									orderStatus = "1";
								}else{
									orderStatus = "0";
								}
								object = new JSONObject();
								object.put("repayId", repayId);
								object.put("currentPeriod", currentPeriod);
								object.put("totalPeriods", totalPeriods);
								object.put("repayAmt", repayAmt);
								object.put("repayDate", repayDate);
								object.put("orderStatus", orderStatus);
								jsonArray.add(object);
							}
						}
						detail.put("repayLists", jsonArray);
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您还没有待还款的订单，请先借款");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 快速还款订单列表
	 */
	@Override
	public JSONObject fastRepayment(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			userId = mch.getUserid() + "";
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			if(null != appLoanAppl){
				String curperiodStatus = "0"; //订单分期状态  0：按天数，1：分期业务
				// 审核中订单
				JSONArray jsonArray0 = new JSONArray();
				Map<String, Object> map0 = new HashMap<String, Object>();
				map0.put("loanStatus", "('0','1','2','8')");
				map0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> maps0 = aPPWithDrawApplMapper.selectByapprIdAndLoanStatus(map0);
				if(null != maps0 && maps0.size() > 0){
					JSONObject jsonObject0 = null;
					for(Map<String, Object> map : maps0){
						jsonObject0 = new JSONObject();
						jsonObject0.put("orderStatus", "0");
						jsonObject0.put("orderNum", StringUtil.toString(map.get("contract_no")));
						jsonObject0.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jsonObject0.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy/MM/dd"));
						jsonObject0.put("borrowTips", "审核中");
						jsonObject0.put("curperiodStatus", "0");
						jsonObject0.put("repayId", "");
						jsonObject0.put("withDrawId", StringUtil.toString(map.get("id")));
						jsonArray0.add(jsonObject0);
					}
				}
				detail.put("underReviewOrder", jsonArray0);
				// 待还款订单
				JSONArray jsonArray1 = new JSONArray();
				// 根据还款时间正序排序订单
				Map<String, Object> map1 = new HashMap<String, Object>();
				map1.put("status", "('1','2')");
				map1.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> maps1 = aPPWithDrawApplMapper.selectByByapprIdAndStatusOrderByRepayDate(map1);
				if(null != maps1 && maps1.size() > 0){
					JSONObject jsonObject1 = null;
					APPPayPlan payPlan = null;
					APPPayPlan payPlan2 = null;
					List<APPPayPlan> payPlans = null;
					Date repayDate = null;
					APPWithDrawAppl withDrawAppl = null;
					for(Map<String, Object> map : maps1){
						jsonObject1 = new JSONObject();
						payPlan = new APPPayPlan();
						payPlan.setWithdrawId(Integer.parseInt(StringUtil.toString(map.get("id"))));
						payPlan.setStatus("0");
						payPlans = aPPPayPlanMapper.selectBywithDrawId(payPlan);
						payPlan2 = payPlans.get(0);
						repayDate = payPlan2.getRepayDate();
						int betweenDay = Math.abs(DateUtil.getDateDaysBetween(repayDate, new Date()));
						withDrawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(StringUtil.toString(map.get("id"))));
						if("2".equals(StringUtil.toString(map.get("status")))){ //逾期
							jsonObject1.put("orderStatus", "1");
							jsonObject1.put("borrowTips", "逾期" + payPlan2.getDays() + "天");
						}else{ //待还款
							if(0 == betweenDay){
								jsonObject1.put("orderStatus", "2"); //当天还款
								jsonObject1.put("borrowTips", "今日还款");
							}else {
								jsonObject1.put("orderStatus", "3"); //还剩**天还款
								jsonObject1.put("borrowTips", "距离还款日还剩" + betweenDay + "天");
							}
						}
						if(null != withDrawAppl.getBorrowPerions() && !"".equals(withDrawAppl.getBorrowPerions())){ //分期业务
							jsonObject1.put("curperiodStatus", "1");
							jsonObject1.put("repayId", "");
						}else{
							jsonObject1.put("curperiodStatus", "0");
							jsonObject1.put("repayId", payPlan2.getId() + "");
						}
						double monthPay = null == payPlan2.getMonthPay() ? 0 : payPlan2.getMonthPay();			// 月应还
						double fxje = null == payPlan2.getFxje() ? 0 : payPlan2.getFxje();				// 罚息金额
						double reducAmt = null == payPlan2.getReducAmt() ? 0 : payPlan2.getReducAmt();			// 优惠金额
						jsonObject1.put("shouldPayAmt", StringUtil.formatNumberToDecimals(new BigDecimal(monthPay).add(new BigDecimal(fxje).subtract(new BigDecimal(reducAmt))).toString(), 2));
						jsonObject1.put("orderNum", StringUtil.toString(map.get("contract_no")));
						jsonObject1.put("shouldRepayTime", DateUtil.format(payPlan2.getRepayDate(), "yyyy/MM/dd"));
						jsonObject1.put("withDrawId", StringUtil.toString(map.get("id")));
						jsonArray1.add(jsonObject1);
					}
				}
				detail.put("notRepayOrder", jsonArray1);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

}
