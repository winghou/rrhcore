package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPContractTempleteMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoginMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.APPContractTemplete;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppLogin;
import com.model.AppUser;
import com.service.intf.QueryPostDetailService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
public class QueryPostDetailImpl implements QueryPostDetailService {
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private APPContractTempleteMapper appTem;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	
	@Override
	public JSONObject queryPostDetail(JSONObject params) {
		JSONObject detail=new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail); 
		String type=JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser user=appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLogin login=appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
		AppLoanAppl appLoanAppl=appLoanApplMapper.selectByitemCode(login.getUserCode());
		if(null!=appLoanAppl){
			//要还款和逾期的
			if("0".equals(type)){
				JSONArray DataList=new JSONArray();
				Map<String, Object> m0=new HashMap<>();
				m0.put("status", "('1','2')");
				m0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> dhk=aPPWithDrawApplMapper.selectByapprIdAndStatus(m0);
				if(dhk!=null&&dhk.size()>0){
					for (Map<String, Object> map : dhk) {
						JSONObject jo=new JSONObject();
						jo.put("withDrawId", StringUtil.toString(map.get("id")));
						jo.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy-MM-dd"));
						jo.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jo.put("perions", StringUtil.toString(map.get("borrow_perions")));
						jo.put("actualAmt", StringUtil.toString(map.get("actual_amt")));
						jo.put("monthPay", StringUtil.toString(map.get("month_pay")));
						jo.put("contractNo", StringUtil.toString(map.get("contract_no")));
						String status=StringUtil.toString(map.get("status"));
						if("1".equals(status)){
							jo.put("isDelay", "正常");
						}else{
							jo.put("isDelay", "逾期");
						}
						jo.put("waitingLoan", "");
						DataList.add(jo);
					}
					detail.put("DataList", DataList);
				}else{
					detail.put("DataList", DataList);
				}
				
			}//待放款、审核中
			else if("1".equals(type)){
				JSONArray DataList=new JSONArray();
				Map<String, Object> m0=new HashMap<>();
				m0.put("loanStatus", "('0','1','2','8')");
				m0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> dfk=aPPWithDrawApplMapper.selectByapprIdAndLoanStatus(m0);
				if(dfk!=null&&dfk.size()>0){
					for (Map<String, Object> map : dfk) {
						JSONObject jo=new JSONObject();
						jo.put("withDrawId", StringUtil.toString(map.get("id")));
						jo.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy-MM-dd"));
						jo.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jo.put("perions", StringUtil.toString(map.get("borrow_perions")));
						jo.put("actualAmt", StringUtil.toString(map.get("actual_amt")));
						jo.put("monthPay", StringUtil.toString(map.get("month_pay")));
						jo.put("contractNo", StringUtil.toString(map.get("contract_no")));
						jo.put("isDelay", "");
						if("0".equals(StringUtil.toString(map.get("loan_status")))){
							jo.put("waitingLoan", "0");
						}else{
							jo.put("waitingLoan", "1");
						}
						detail.put("DataList", DataList);
						DataList.add(jo);
					}
					detail.put("DataList", DataList);
				}else{
					detail.put("DataList", DataList);
				}
			}else if("2".equals(type)){ //结清
				JSONArray overDataList=new JSONArray();
				Map<String, Object> m0=new HashMap<>();
				m0.put("status", "('3')");
				m0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> jq=aPPWithDrawApplMapper.selectByapprIdAndStatus(m0);
				if(jq!=null&&jq.size()>0){
					for (Map<String, Object> map : jq) {
						JSONObject jo=new JSONObject();
						jo.put("withDrawId", StringUtil.toString(map.get("id")));
						jo.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy-MM-dd"));
						jo.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jo.put("perions", StringUtil.toString(map.get("borrow_perions")));
						jo.put("actualAmt", StringUtil.toString(map.get("actual_amt")));
						jo.put("monthPay", StringUtil.toString(map.get("month_pay")));
						jo.put("contractNo", StringUtil.toString(map.get("contract_no")));
						jo.put("isDelay", "");
						jo.put("waitingLoan", "");
						overDataList.add(jo);
					}
					detail.put("DataList", overDataList);
				}else{
					detail.put("DataList", overDataList);
				}
			}else if("3".equals(type)){ //驳回
				JSONArray jujueDataList=new JSONArray();
				Map<String, Object> m0=new HashMap<>();
				m0.put("status", "('4')");
				m0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> jj=aPPWithDrawApplMapper.selectByapprIdAndStatus(m0);
				if(jj!=null&&jj.size()>0){
					for (Map<String, Object> map : jj) {
						JSONObject jo=new JSONObject();
						jo.put("withDrawId", StringUtil.toString(map.get("id")));
						jo.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy-MM-dd"));
						jo.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jo.put("perions", StringUtil.toString(map.get("borrow_perions")));
						jo.put("actualAmt", StringUtil.toString(map.get("actual_amt")));
						jo.put("monthPay", StringUtil.toString(map.get("month_pay")));
						jo.put("contractNo", StringUtil.toString(map.get("contract_no")));
						jo.put("isDelay", "");
						jo.put("waitingLoan", "");
						jujueDataList.add(jo);
					}
					detail.put("DataList", jujueDataList);
				}else{
					detail.put("DataList", jujueDataList);
				}
			}else if("4".equals(type)){ //待签约
				JSONArray qianyueDataList=new JSONArray();
				Map<String, Object> m0=new HashMap<>();
				m0.put("loanStatus", "('5')");
				m0.put("apprId", appLoanAppl.getId());
				List<Map<String, Object>> qy=aPPWithDrawApplMapper.selectByapprIdAndLoanStatus(m0);
				if(qy!=null&&qy.size()>0){
					for (Map<String, Object> map : qy) {
						JSONObject jo=new JSONObject();
						jo.put("withDrawId", StringUtil.toString(map.get("id")));
						jo.put("borrowTime", DateUtil.format(DateUtil.parseDate(StringUtil.toString(map.get("borrow_time"))), "yyyy-MM-dd"));
						jo.put("borrowAmt", StringUtil.toString(map.get("borrow_amt")));
						jo.put("perions", StringUtil.toString(map.get("borrow_perions")));
						jo.put("actualAmt", StringUtil.toString(map.get("actual_amt")));
						jo.put("monthPay", StringUtil.toString(map.get("month_pay")));
						jo.put("contractNo", StringUtil.toString(map.get("contract_no")));
						jo.put("isDelay", "");
						jo.put("waitingLoan", "");
						qianyueDataList.add(jo);
					}
					detail.put("DataList", qianyueDataList);
				}else{
					detail.put("DataList", qianyueDataList);
				}
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "查询错误");
		}
		return detail;
	}
	@Override
	public JSONObject queryContractTemplete(JSONObject params) {
		JSONObject detail=new JSONObject();
		String withDrawId=JsonUtil.getJStringAndCheck(params, "withDrawId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		APPWithDrawAppl withDraw=aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt((withDrawId)));
		if(null!=withDraw){
			List<APPContractTemplete>  appContractTempletes=appTem.selectTmpUrl(withDraw.getContractTempid());
			JSONArray details=new JSONArray();
			for (APPContractTemplete appContractTemplete : appContractTempletes) {
				JSONObject jo=new JSONObject();
				jo.put("contractType", appContractTemplete.getContractType());
				jo.put("contractUrl", appContractTemplete.getUrl()+withDrawId);
				details.add(jo);
			}
			detail.put("dateList", details);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "没有此借款id");
		}
		
		return detail;
	}
	
	/**
	* 创建人：lizhongwei 
	* 创建时间：2017年5月19日 上午10:14:22   
	* 修改人：lizhongwei  
	* 修改时间：2017年07月07日 14:14:22   
	* 修改备注：   查询用户订单记录
	* @version
	 */
	@Override
	public JSONObject queryOrderRecords(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null != mch){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			if(null != loanAppl){
				JSONArray jsonArray = new JSONArray();
				List<Map<String, Object>> lists = aPPWithDrawApplMapper.selectOrderRecords(loanAppl.getId());
				if(null != lists && lists.size() > 0){
					String withId = "";
					String status = "";
					String contractNo = "";
					String borrowAmt = "";
					String borrowTime = "";
					String repayTime = "";
					List<APPPayPlan> payPlans = null;
					APPPayPlan appPayPlan = null;
					JSONObject object = null;
					for(Map<String, Object> list : lists){
						withId = StringUtil.nvl(list.get("id"));
						status = StringUtil.nvl(list.get("status"));
						contractNo = StringUtil.nvl(list.get("contract_no"));
						borrowAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(list.get("borrow_amt")), 2);
						borrowTime = DateUtil.format(DateUtil.parseDate(StringUtil.nvl(list.get("borrow_time"))), "yyyy/MM/dd");
						if(!"0".equals(status)){
							appPayPlan = new APPPayPlan();
							appPayPlan.setWithdrawId(Integer.parseInt(withId));
							appPayPlan.setStatus("0");
							payPlans = appPayPlanMapper.selectBywithDrawId(appPayPlan);
							if(null != payPlans && payPlans.size() > 0){
								appPayPlan = payPlans.get(0);
								repayTime = DateUtil.format(appPayPlan.getRepayDate(), "yyyy/MM/dd");
							}else{
								payPlans = appPayPlanMapper.selectAllByWithDrawId(Integer.parseInt(withId));
								appPayPlan = payPlans.get(payPlans.size() - 1);
								repayTime = DateUtil.format(appPayPlan.getRepayDate(), "yyyy/MM/dd");
							}
						}else{
							repayTime = "";
						}
						object = new JSONObject();
						object.put("withId", withId);
						object.put("loanStatus", status);
						object.put("contractNo", contractNo);
						object.put("borrowAmt", borrowAmt);
						object.put("borrowTime", borrowTime);
						object.put("repayTime", repayTime);
						jsonArray.add(object);
					}
				}
				detail.put("orderRecordLists", jsonArray);
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

}
