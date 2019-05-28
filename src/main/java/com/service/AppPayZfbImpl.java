package com.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppAgreementPayBankMapper;
import com.dao.AppAliPayMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppUserMapper;
import com.dao.AppzfbOrderListMapper;
import com.dao.IfmBaseDictMapper;
import com.dao.IfmWithholdMapper;
import com.dao.IfmYxtSignMapper;
import com.frame.Consts;
import com.frame.RedisService;
import com.model.APPCredit;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppAgreementPayBank;
import com.model.AppAliPay;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppUser;
import com.model.AppzfbOrderList;
import com.model.IfmBaseDict;
import com.model.IfmWithhold;
import com.model.IfmYxtSign;
import com.service.intf.AppPayZfbService;
import com.util.APIHttpClient;
import com.util.AliPayUtilNew;
import com.util.Constants;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class AppPayZfbImpl implements AppPayZfbService {
	@Autowired
	private APPPayPlanMapper aPPPayPlanMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private AppAliPayMapper appAliPayMapper;
	@Autowired
	private AppzfbOrderListMapper appzfbOrderListMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private APPCreditMapper appCreditMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private IfmWithholdMapper ifmWithholdMapper;
	@Autowired
	private IfmYxtSignMapper ifmYxtSignMapper;
	@Autowired
	private RedisService redisService;
	@Autowired
	private AppAgreementPayBankMapper appAgreementPayBankMapper;


	/**
	 * 创建支付宝订单
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject payZfb(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String repayIds = JsonUtil.getJStringAndCheck(params, "repayIds", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		// 判断是否在正确的支付时间内
		if(!isPayAtRightTime()){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "财务正在结算中");
			return detail;
		}
		JSONArray datelist = params.getJSONArray("repayIds");
		List<Map<String,Object>> list= ifmBaseDictMapper.selectBaseDict("PAY_ZFB_SWITCH");
		List<Map<String,Object>> whiteList = ifmBaseDictMapper.selectBaseDict("PAY_ZFB_WHITE_LIST");
		if("1".equals(list.get(0).get("ITEM_VALUE"))){
			String whitelist = StringUtil.nvl(whiteList.get(0).get("ITEM_VALUE"));
			StringTokenizer st = new StringTokenizer(whitelist,",");
			String repay_id = StringUtil.toString(((JSONObject) datelist.get(0)).get("repayId"));
			APPPayPlan appPayPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repay_id));
			String withdrawid = StringUtil.nvl(appPayPlan.getWithdrawId());
			boolean flag = true;
	        while(st.hasMoreTokens()){
	        	if(withdrawid.equals(st.nextToken())){
	        		flag = false;
	        	}
	        }
			if(flag){
				if("1".equals(list.get(0).get("ITEM_VALUE"))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "维护中,请使用银行卡还款！");
					return detail;
				}
			}
		}
		
		if(null != datelist && datelist.size() > 0){
			if(datelist.size() == 1){ //限制只能单笔还款
				AppUser appUser = appUserMapper.selectByMchVersion(userId);
				if(null != appUser){
					AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
					String repayId0 = StringUtil.toString(((JSONObject) datelist.get(0)).get("repayId"));
					APPPayPlan payPlan0 = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId0));
					// 还款校验
					JSONObject checkObject = checkPayOrder(appLoanAppl, datelist, payPlan0);
					if(null != checkObject){
						return checkObject;
					}
					// 根据资方确定支付宝账号
					Integer withDrawId = payPlan0.getWithdrawId();
					APPWithDrawAppl appl = aPPWithDrawApplMapper.selectByPrimaryKey(withDrawId);
					AppAliPay appAliPay = getAliPayByCoorpration(appl);
					
					AliPayUtilNew.SERVICE = appAliPay.getService();
					AliPayUtilNew.PARTNER = appAliPay.getPartner();
					AliPayUtilNew.SELLER = appAliPay.getSellerId();
					AliPayUtilNew.CHARSET = appAliPay.getInputCharset();
					AliPayUtilNew.NOTIFY_URL = appAliPay.getNotifyUrl2();
					AliPayUtilNew.PAYMENT_TYPE = appAliPay.getPaymentType();
					AliPayUtilNew.PRIVATEKEY = appAliPay.getPrivateKey2();
					AliPayUtilNew.IT_B_PAY = appAliPay.getTimeoutExpress();
					AliPayUtilNew.GATEWAYURL = appAliPay.getGateWayUrl();
					AliPayUtilNew.SIGNTYPE = appAliPay.getSignType();
					AliPayUtilNew.PRODUCTCODE = appAliPay.getProductCode();
					AliPayUtilNew.PUBLICKEY = appAliPay.getPublicKey2();
					
					Double sumAmt = 0.00;
					String repayDate = "";
					String uuid = UUID.randomUUID().toString().replace("-", "");
					synchronized (this) {
						JSONObject s = null;
						APPPayPlan aPPPayPlan = null;
						AppzfbOrderList order = null;
						for (Object obj : datelist) {
							s = (JSONObject) obj;
							aPPPayPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(StringUtil.toString(s.get("repayId"))));
							// 支付ID加入缓存
							if (redisService.exists("MCH_PAY_REPAYID_" + StringUtil.toString(s.get("repayId")))) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
								return detail;
							} else {
								redisService.set("MCH_PAY_REPAYID_" + StringUtil.toString(s.get("repayId")), "orderId", 20);
							}
							Double fxje = aPPPayPlan.getFxje();
							if (null == fxje) {
								fxje = 0.00;
							}
							double reducAmt = null == aPPPayPlan.getReducAmt() ? 0.0 : aPPPayPlan.getReducAmt();
							double parseDouble = StringUtil.parseDouble(aPPPayPlan.getMonthPay() + fxje - reducAmt);
							sumAmt += parseDouble;
							List<AppzfbOrderList> orderLists = appzfbOrderListMapper.selectAllByRepayPlanId(aPPPayPlan.getId());
							repayDate = StringUtil.toString(DateUtil.format(aPPPayPlan.getRepayDate(), "yyyy-MM"));
							if(null != orderLists && orderLists.size() > 0){
								order = orderLists.get(orderLists.size() - 1);
								// 先判断该笔订单支付状态
								String outTradeNo = order.getWidoutTradeNo();
								// 检查订单是否可以支付
								Boolean createBoofOrderResult = isCreateBoofOrder(outTradeNo, ifmBaseDictMapper);
								Boolean createAgreementPayResult = isCreateAgreementPayOrder(outTradeNo,order.getZfbPayDate(),ifmBaseDictMapper);
								Boolean createZfbOrderResult = isCreateZfbOrder(outTradeNo, "", appAliPay);
								if(null == createBoofOrderResult){
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "请求超时，请稍后重试！");
									return detail;
								}
								if(createBoofOrderResult && createZfbOrderResult && createAgreementPayResult){
									createZfbListOrder4Zfb(sumAmt, aPPPayPlan.getId() + "", uuid, repayDate);
								}else{
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
									return detail;
								}
							}else{
								createZfbListOrder4Zfb(sumAmt, aPPPayPlan.getId() + "", uuid, repayDate);
							}
						}
					}
					appAliPay.setWIDbody("sxh");
					appAliPay.setWIDsubject(repayDate + "账单还款(随心花还款)");
					appAliPay.setWIDtotal_fee(StringUtil.parseDouble(sumAmt) + "");
					appAliPay.setWIDout_trade_no(uuid);
					Map<String, String> orderInfoMap = AliPayUtilNew.getOrderInfoMap(appAliPay.getWIDout_trade_no(),
							appAliPay.getWIDsubject(), appAliPay.getWIDbody(), appAliPay.getWIDtotal_fee());
					String orderInfo = orderInfoMap.get("orderInfo");
					detail.put("orderInfo", orderInfo);
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
				}
				for(Object obj : datelist){
					JSONObject s = (JSONObject) obj;
					redisService.del("MCH_PAY_REPAYID_" + StringUtil.toString(s.get("repayId")));
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "目前只支持单期还款，请重试！");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "期数不能为空");
		}
		return detail;
	}
	
	/**
	 * 还款结果
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject payResult(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String outTradeNo = JsonUtil.getJStringAndCheck(params, "outTradeNo", null, false, detail); //支付宝外部订单号
		String totalFee = JsonUtil.getJStringAndCheck(params, "totalFee", null, false, detail);
		String withholdTradeNo = JsonUtil.getJStringAndCheck(params, "withholdTradeNo", null, false, detail); //代扣支付订单号
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			userId = appUser.getUserid() + "";
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			String orderNo = "";
			double totalAmt = 0.0;
			// 支付宝订单
			if(!"".equals(outTradeNo) && !"".equals(outTradeNo) && "".equals(withholdTradeNo)){
				orderNo = outTradeNo;
			}else if("".equals(outTradeNo) && "".equals(outTradeNo) && !"".equals(withholdTradeNo)){//代扣订单
				orderNo = withholdTradeNo;
			}
			List<AppzfbOrderList> appzfbOrderLists = appzfbOrderListMapper.selectByUUID(orderNo);
			if(null != appzfbOrderLists && appzfbOrderLists.size() > 0){
				APPPayPlan payPlan = null;
				for(AppzfbOrderList appzfbOrderList : appzfbOrderLists){
					payPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(appzfbOrderList.getRepayPlanId()));
					if(null != payPlan){
						if(appLoanAppl.getId().intValue() == payPlan.getApprId().intValue()){
							totalAmt += Double.parseDouble(appzfbOrderList.getWidtotalFee());
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "该订单不属于您，请重试");
							break;
						}
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "账单异常，请联系微信客服");
						break;
					}
				}
				APPCredit appCredit = appCreditMapper.selectByApprId(appLoanAppl.getId());
				double useAmt = appCredit.getUseAmt();
				List<APPWithDrawAppl> applWithDrawApplList = aPPWithDrawApplMapper.selectAllByapprId(appLoanAppl.getId());
				double auditAmt = 0.00;
				if(applWithDrawApplList.size() > 0){
					for(APPWithDrawAppl appWithDrawAppl : applWithDrawApplList){
						if(!("3").equals(appWithDrawAppl.getLoanStatus()) && !("4").equals(appWithDrawAppl.getLoanStatus())){
							auditAmt = auditAmt + Double.parseDouble(appWithDrawAppl.getBorrowAmt());
						}
					}
				}
				double freezeAmt = auditAmt + appCredit.getUseAmt() + appCredit.getWait_pay_amt() - appCredit.getCreditAmt();
				if(freezeAmt > 0){
					double actualUseAmt = 0.00;
					if(useAmt - freezeAmt > 0){
						actualUseAmt = useAmt - freezeAmt;
					}
					detail.put("useAmt", StringUtil.formatNumberToDecimals(actualUseAmt + "", 2));
				}else{
					detail.put("useAmt", StringUtil.formatNumberToDecimals(useAmt + "", 2));
				}
				detail.put("repayAmt", totalAmt + "");
				List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("MCH_PAY_RESULT_THEPAGETEXT");
				if(list.size() > 0){
					detail.put("thePageText1", list.get(0).get("ITEM_VALUE"));
					detail.put("thePageText2", list.get(1).get("ITEM_VALUE"));
					detail.put("thePageText3", list.get(2).get("ITEM_VALUE"));
				}else{
					detail.put("thePageText1", "");
					detail.put("thePageText2", "");
					detail.put("thePageText3", "");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "账单不存在");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 快捷代扣
	 * @param jo
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject fastWithholding(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String repayIds = JsonUtil.getJStringAndCheck(params, "repayIds", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		// 判断是否在正确的支付时间内
		if(!isPayAtRightTime()){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "财务正在结算中");
			return detail;
		}
		JSONArray datelist = params.getJSONArray("repayIds");
		if(null != datelist && datelist.size() > 0){
			if(datelist.size() == 1){
				AppUser appUser = appUserMapper.selectByMchVersion(userId);
				if (null != appUser) {
					AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
					String repayId0 = StringUtil.toString(((JSONObject) datelist.get(0)).get("repayId"));
					APPPayPlan payPlan0 = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId0));
					// 还款校验
					JSONObject checkObject = checkPayOrder(appLoanAppl, datelist, payPlan0);
					if(null != checkObject){
						return checkObject;
					}
					Integer appPlanWithId = payPlan0.getWithdrawId();
					// 根据资方确定支付宝账号
					APPWithDrawAppl drawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(appPlanWithId);
					AppAliPay appAliPay = getAliPayByCoorpration(drawAppl);
					// 判断该订单的代扣次数
					IfmYxtSign yxtSign = ifmYxtSignMapper.selectByOrderIdAndType(appPlanWithId + "", "1");
					if(null != yxtSign && "0".equals(yxtSign.getDeductStatus()) && yxtSign.getDeductCount().intValue() > 2){
						List<Map<String,Object>> whiteList = ifmBaseDictMapper.selectBaseDict("PAY_ZFB_WHITE_LIST");
						String whitelist = StringUtil.nvl(whiteList.get(0).get("ITEM_VALUE"));
						StringTokenizer st = new StringTokenizer(whitelist,",");
						String withdrawid = StringUtil.nvl(drawAppl.getId());
						boolean flag = true;
				        while(st.hasMoreTokens()){
				        	if(withdrawid.equals(st.nextToken())){
				        		flag = false;
				        	}
				        }
				        if(flag){
				        		String white = whitelist + "," + withdrawid;
				        		IfmBaseDict ifmBaseDict = new IfmBaseDict();
				        		ifmBaseDict.setDataType("PAY_ZFB_WHITE_LIST");
				        		ifmBaseDict.setItemValue(white);
				        		try{
				        			ifmBaseDictMapper.updateByDataType(ifmBaseDict);
				        			detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "代扣次数超限，已为您该笔订单开通支付宝还款通道，请使用支付宝还款");
				        		}catch(Exception e){
				        			detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "代扣次数超限，请联系客服还款");
				        		}
				        }else{
				        	detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "代扣次数超限，已为您该笔订单开通支付宝还款通道，请使用支付宝还款");
				        } 
						return detail;
					}
					String out_trade_no = UUID.randomUUID().toString();
					synchronized (this) {
						JSONObject jObject = null;
						APPPayPlan payPlan = null;
						AppLoanCtm loanCtm = null;
						// 宝付请求参数
						String notifyUrl = "";
						String appKey = "";
						String appSecrect = "";
						String boofUrl = "";
						// 获取宝付请求参数
						JSONObject boofObject = getBoofRequestParam();
						if(null != boofObject){
							notifyUrl = StringUtil.nvl(boofObject.get("notifyUrl"));
							appKey = StringUtil.nvl(boofObject.get("appKey"));
							appSecrect = StringUtil.nvl(boofObject.get("appSecrect"));
							boofUrl = StringUtil.nvl(boofObject.get("boofUrl"));
						}
						double totalAmt = 0.0;
						for (Object obj : datelist) {
							jObject = (JSONObject) obj;
							int repayPlanId = Integer.parseInt(StringUtil.nvl(jObject.get("repayId")));
							// 支付ID加入缓存
							if (redisService.exists("MCH_PAY_REPAYID_" + repayPlanId)) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
								return detail;
							} else {
								redisService.set("MCH_PAY_REPAYID_" + repayPlanId, "orderId", 20);
							}
							payPlan = aPPPayPlanMapper.selectByPrimaryKey(repayPlanId);
							double fxje = null == payPlan.getFxje() ? 0 : payPlan.getFxje();
							double monthPay = null == payPlan.getMonthPay() ? 0 : payPlan.getMonthPay();
							double sumAmt = StringUtil.getCalculatorResult(fxje, monthPay, 0);
							// 创建支付订单
							List<AppzfbOrderList> orderLists = appzfbOrderListMapper.selectAllByRepayPlanId(payPlan.getId());
							if (null != orderLists && orderLists.size() > 0) {
								AppzfbOrderList order = orderLists.get(orderLists.size() - 1);
								// 先判断该笔订单支付状态
								String outTradeNo = order.getWidoutTradeNo();
								// 检查订单是否可以支付
								Boolean createBoofOrderResult = isCreateBoofOrder(outTradeNo, ifmBaseDictMapper);
								Boolean createZfbOrderResult = isCreateZfbOrder(outTradeNo, "", appAliPay);
								if(null == createBoofOrderResult){
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "请求超时，请稍后重试！");
									return detail;
								}
								if(createBoofOrderResult && createZfbOrderResult){
									createZfbListOrder4Boof(sumAmt, payPlan.getId() + "", out_trade_no, drawAppl);
								}else{
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
									return detail;
								}
							}else{
								createZfbListOrder4Boof(sumAmt, payPlan.getId() + "", out_trade_no, drawAppl);
							}
							totalAmt += sumAmt;
						}
						loanCtm = appLoanCtmMapper.selectByapprId(payPlan.getApprId());
						String bankCode = getBankCodeByBank(loanCtm.getBank());
						// 创建代扣订单
						createWithholdOrder(totalAmt, payPlan, loanCtm, out_trade_no, bankCode);
						// 获取宝付请求数据
						JSONObject object = getBoofReqParam(loanCtm, appKey, appSecrect, out_trade_no, totalAmt + "", notifyUrl, bankCode, drawAppl);
						String str =  APIHttpClient.doPost(boofUrl, object, 60000);
						if(null != str){
							JSONObject object2 = (JSONObject) JSON.parse(str);
							String result = StringUtil.nvl(object2.get("result"));
							String resultNote = StringUtil.nvl(object2.get("resultNote"));
							if("1".equals(result)){
								detail.put(Consts.RESULT, ErrorCode.SUCCESS);
								detail.put(Consts.RESULT_NOTE, resultNote);
								detail.put("withholdTradeNo", out_trade_no);
							}else{
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, resultNote);
							}
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "代扣失败，请重试");
						}
						//删除redis
						for(Object obj : datelist){
							jObject = (JSONObject) obj;
							if(redisService.exists("MCH_PAY_REPAYID_" + StringUtil.nvl(jObject.get("repayId")))){
							redisService.del("MCH_PAY_REPAYID_" + StringUtil.nvl(jObject.get("repayId")));
						}
					}
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "目前只支持单期还款，请重试！");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "期数不能为空");
		}
		return detail;
	}
	
	/**
	 * 支付选择页面
	 */
	@Override
	public JSONObject payWayChoosonPage(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String repayIds = JsonUtil.getJStringAndCheck(params, "repayIds", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null != appUser) {
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			JSONArray datelist = params.getJSONArray("repayIds");
			if (null != datelist && datelist.size() > 0) {
				JSONObject jo = null;
				APPPayPlan aPlan = null;
				String repayId = StringUtil.toString(((JSONObject) datelist.get(0)).get("repayId"));
				APPPayPlan payPlan0 = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId));
				if(null == payPlan0){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "该支付订单不存在");
					return detail;
				}
				Integer appPlanWithId = payPlan0.getWithdrawId();
				for (Object obj : datelist) {
					jo = (JSONObject) obj;
					aPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(StringUtil.toString(jo.get("repayId"))));
					if (aPlan.getApprId().intValue() != appLoanAppl.getId().intValue()) {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "只能支付属于自己的订单");
						return detail;
					}
					if (appPlanWithId.intValue() != aPlan.getWithdrawId().intValue()) {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "只能支付同一笔订单的账单");
						return detail;
					}
					if (aPlan.getStatus().equals("1")) {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "其中" + aPlan.getCurperiods() + "期已还款");
						return detail;
					}
				}
				APPWithDrawAppl withDrawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(appPlanWithId);
				AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(withDrawAppl.getApprId());
				String bank = loanCtm.getBank();
				// 获取银行名称
				Map<String, Object> map = new HashMap<>();
				map.put("dataType", "BANK_NAME");
				map.put("perion", bank);
				IfmBaseDict ifmBaseDict = ifmBaseDictMapper.selectfWFAndRate(map);
				String bankName = "";
				if(null != ifmBaseDict){
					bankName = StringUtil.nvl(ifmBaseDict.getItemValue());
				}else{
					AppAgreementPayBank agreementPayBank = appAgreementPayBankMapper.selectByCode(bank);
					if(null != agreementPayBank){
						bankName = StringUtil.nvl(agreementPayBank.getBankName());
					}
				}
				// 莫愁花微信支付引导URL
				map = new HashMap<>();
				map.put("dataType", "MCH_WECHAT_PAY_URL");
				map.put("perion", "0");
				IfmBaseDict ifmBaseDict1 = ifmBaseDictMapper.selectfWFAndRate(map);
				String weChatUrl = "";
				if(null != ifmBaseDict1){
					weChatUrl = StringUtil.nvl(ifmBaseDict1.getItemValue());
				}
				String bankCrad = StringUtil.nvl(loanCtm.getBankCard());
				if(!"".equals(bankCrad)){
					bankCrad = bankCrad.substring(bankCrad.length() - 4);
				}
				APPPayPlan payPlan = null;
				double totalAmt = 0.0;
				for (Object obj : datelist) {
					jo = (JSONObject) obj;
					int payId = Integer.parseInt(StringUtil.toString(jo.get("repayId")));
					payPlan = aPPPayPlanMapper.selectByPrimaryKey(payId);
					double fxje = null == payPlan.getFxje() ? 0 : payPlan.getFxje();
					double monthPay = null == payPlan.getMonthPay() ? 0 : payPlan.getMonthPay();
					double sumAmt = StringUtil.getCalculatorResult(fxje, monthPay, 0);
					totalAmt += sumAmt;
				}
				detail.put("orderNo", "SXH" + withDrawAppl.getContract_no());
				detail.put("payAmt", totalAmt + "");
				detail.put("bankInfo", "(" + bankName + bankCrad + ")");
				detail.put("weChatUrl", weChatUrl);
				//客服电话
				List<Map<String, Object>> ifmBaseDict2 = ifmBaseDictMapper.selectBaseDict("SXH_CUSTOMER_SERVICE_TELEPHONE");
				if(ifmBaseDict2!=null&&ifmBaseDict2.size()>0){
					detail.put("customerServiceTelephone", "".equals(StringUtil.nvl(ifmBaseDict2.get(0).get("ITEM_VALUE")))?"025-83583616":StringUtil.nvl(ifmBaseDict2.get(0).get("ITEM_VALUE")));
				}else{
					detail.put("customerServiceTelephone", "025-83583616");
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "期数不能为空");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 支付完成过渡页查询还款结果
	 */
	@Override
	public JSONObject qryPayResultFrequently(JSONObject params){
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String repayIds = JsonUtil.getJStringAndCheck(params, "repayIds", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		JSONArray datelist = params.getJSONArray("repayIds");
		int failedNum = 0; //订单支付成功返回标记
		boolean result = true; //默认没有错误
		if (null != datelist && datelist.size() > 0) {
			AppUser appUser = appUserMapper.selectByMchVersion(userId);
			if (null != appUser) {
				AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
				JSONObject jo = null;
				APPPayPlan aPlan = null;
				// 校验订单属于本人
				for (Object obj : datelist) {
					jo = (JSONObject) obj;
					aPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(StringUtil.toString(jo.get("repayId"))));
					if(null != aPlan){
						if (aPlan.getApprId().intValue() != loanAppl.getId().intValue()) {
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "");
							return detail;
						}
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "");
						return detail;
					}
				}
				JSONObject jsonObject = null;
				List<AppzfbOrderList> orderLists = null;
				AppzfbOrderList orderList = null;
				// 校验
				for (Object obj : datelist) {
					jsonObject = (JSONObject) obj;
					int payId = Integer.parseInt(StringUtil.nvl(jsonObject.get("repayId")));
					orderLists = appzfbOrderListMapper.selectAllByRepayPlanId(payId);
					if(null != orderLists && orderLists.size() > 0){
						orderList = orderLists.get(orderLists.size() - 1);
						String zfbOrderNo = StringUtil.nvl(orderList.getZfbOrderNo());
						if("".equals(zfbOrderNo)){ //判断是否已产生支付回调（未支付）
							failedNum++;
						}
					}else{
						result = false;
					}
				}
			}else{
				result = false;
			}
		}else{
			result = false;
		}
		if(result && failedNum == 0){
			detail.put(Consts.RESULT, ErrorCode.SUCCESS);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
		}
		detail.put(Consts.RESULT_NOTE, "");
		return detail;
	}
	
	
	
	// 是否在正确支付时间（00:00-00:20 不可还款）
	private boolean isPayAtRightTime(){
		boolean flag = true;
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.set(year, month, day, 0, 0, 0);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(year, month, day, 0, 20, 0);
		Date startDate = calendar1.getTime();
		Date endDate = calendar2.getTime();
		Date currentDate = new Date();
		long startTime = startDate.getTime();
		long endTime = endDate.getTime();
		long currentTime = currentDate.getTime();
		if(startTime < endTime && currentTime >= startTime && currentTime < endTime){
			flag = false;
		}
		return flag;
	}
	
	// 获取宝付请求参数
	private JSONObject getBoofRequestParam(){
		JSONObject detail = new JSONObject();
		List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("BOOF_FAST_WITHHOLD_PARAM");
		if(null != maps && maps.size() > 0){
			for(Map<String, Object> m : maps){
				String itemKey = StringUtil.nvl(m.get("ITEM_KEY"));
				String itemValue = StringUtil.nvl(m.get("ITEM_VALUE"));
				switch (itemKey) {
				case "0": //请求URL
					String boofUrl = itemValue;
					detail.put("boofUrl", boofUrl);
					break;
				case "1": //appKey
					String appKey = itemValue;
					detail.put("appKey", appKey);
					break;
				case "2": //appSecrect
					String appSecrect = itemValue;
					detail.put("appSecrect", appSecrect);
					break;
				case "3": //回调URL
					String notifyUrl = itemValue;
					detail.put("notifyUrl", notifyUrl);
					break;
				}
			}
		}
		return detail;
	}
	
	//创建宝付支付订单
	private void createZfbListOrder4Boof(double sumAmt, String replanId, String uuid, APPWithDrawAppl drawAppl){
		List<IfmBaseDict> baseDicts = ifmBaseDictMapper.fetchDictsByType("CHECKCARD_SWITCH");
		String baseDict = "".equals(StringUtil.nvl(baseDicts.get(0).getItemValue()))?"2":StringUtil.nvl(baseDicts.get(0).getItemValue());
		// 添加支付订单
		AppzfbOrderList orderList = new AppzfbOrderList();
		orderList.setWidoutTradeNo(uuid);
		orderList.setWidtotalFee(sumAmt + "");
		orderList.setCreateDate(new Date());
		if("2".equals(baseDict)){
			orderList.setWidoutSubject("随心花还款(协议支付)");
		}else{
			orderList.setWidoutSubject("随心花还款(银行卡代扣)");
		}
		orderList.setRepayPlanId(replanId);
		if("8".equals(StringUtil.nvl(drawAppl.getCooperation()))){
			orderList.setPaySource("6");	//纵横主动代扣
		}else{
			orderList.setPaySource("2");	//泰山宝
		}
		appzfbOrderListMapper.insertSelective(orderList);
	}
	
	//创建支付宝支付订单
	private void createZfbListOrder4Zfb(double sumAmt, String replanId, String uuid, String repayDate){
		// 添加支付订单
		AppzfbOrderList order = new AppzfbOrderList();
		order.setRepayPlanId(replanId);
		order.setWidtotalFee(sumAmt + "");
		order.setWidoutSubject(repayDate + "账单还款（随心花还款）");
		order.setWidoutTradeNo(uuid);
		order.setCreateDate(new Date());
		appzfbOrderListMapper.insertSelective(order);
	}
	
	//创建代扣订单
	private void createWithholdOrder(double sumAmt, APPPayPlan payPlan, AppLoanCtm loanCtm, String uuid, String bankCode){
		// 创建代扣回调记录
		IfmWithhold ifmWithhold = new IfmWithhold();
		ifmWithhold.setAppr_id(payPlan.getWithdrawId() + "");
		ifmWithhold.setBankCardNo(loanCtm.getBankCard());
		ifmWithhold.setBankCode(bankCode);
		ifmWithhold.setBankName(bankCode);
		ifmWithhold.setDeductAmount(sumAmt);
		ifmWithhold.setMerchOrderNo(uuid);
		ifmWithhold.setName(loanCtm.getCustomName());
		ifmWithhold.setPlan_id(payPlan.getId() + "");
		ifmWithhold.setReceiptTime(new Date());
		ifmWithholdMapper.insertSelective(ifmWithhold);
		// 创建签约表
		IfmYxtSign ifmYxtSign = ifmYxtSignMapper.selectByOrderIdAndType(payPlan.getWithdrawId() + "", "1");
		if(null == ifmYxtSign){
			ifmYxtSign = new IfmYxtSign();
			ifmYxtSign.setBankCardNo(loanCtm.getBankCard());
			ifmYxtSign.setBankCode(bankCode);
			ifmYxtSign.setBankName(bankCode);
			ifmYxtSign.setDeductStatus("0");
			ifmYxtSign.setMerchOrderNo(uuid);
			ifmYxtSign.setName(loanCtm.getCustomName());
			ifmYxtSign.setSignType("1");
			ifmYxtSign.setApprId(payPlan.getWithdrawId() + "");
			ifmYxtSign.setDeductDateTime(new Date());
			ifmYxtSignMapper.insertSelective(ifmYxtSign);
		}else{
			ifmYxtSign.setDeductCount((null == ifmYxtSign.getDeductCount() ? 0 : ifmYxtSign.getDeductCount()) + 1);
			ifmYxtSign.setMerchOrderNo(ifmWithhold.getMerchOrderNo());
			ifmYxtSign.setDeductDateTime(new Date());
			ifmYxtSignMapper.updateByPrimaryKeySelective(ifmYxtSign);
		}
	}

	/**
	 * @param appLoanAppl
	 * @param datelist 还款ID数组
	 * @param payPlan0 第一个还款计划
	 * @return 错误信息
	 */
	private JSONObject checkPayOrder(AppLoanAppl appLoanAppl, JSONArray datelist, APPPayPlan payPlan){
		JSONObject detail = null;
		if ("3".equals(StringUtil.nvl(appLoanAppl.getAccountStatus()))) { // 账户关闭
			detail = new JSONObject();
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
			return detail;
		}
		JSONObject jo = null;
		APPPayPlan aPlan = null;
		String repayId0 = payPlan.getId() + "";
		for (int i = 1; i < datelist.size(); i++) {
			if (repayId0.equals(StringUtil.nvl(((JSONObject) datelist.get(i)).get("repayId")))) {
				detail = new JSONObject();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "支付订单重复");
				return detail;
			}
		}
		APPPayPlan payPlan0 = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId0));
		Integer appPlanWithId = payPlan0.getWithdrawId();
		for (Object obj : datelist) {
			jo = (JSONObject) obj;
			String repayId = StringUtil.toString(jo.get("repayId"));
			aPlan = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId));
			if(null == aPlan){
				detail = new JSONObject();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该支付订单不存在");
				return detail;
			}
			if(aPlan.getApprId().intValue() != appLoanAppl.getId().intValue()) {
				detail = new JSONObject();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "只能支付属于自己的订单");
				return detail;
			}
			if(appPlanWithId.intValue() != aPlan.getWithdrawId().intValue()) {
				detail = new JSONObject();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "只能支付同一笔订单的账单");
				return detail;
			}
			if (aPlan.getStatus().equals("1")) {
				detail = new JSONObject();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "其中" + aPlan.getCurperiods() + "期已还款");
				return detail;
			}
		}
		return detail;
	}
	
	/**
	 * 是否可以创建宝付订单
	 * @param outTradeNo
	 * @param ifmBaseDictMapper
	 * @return false（不能创建，订单支付中或支付成功） true（可以创建）
	 */
	private Boolean isCreateBoofOrder(String outTradeNo, IfmBaseDictMapper ifmBaseDictMapper){
		Boolean flag = true;
		String result = qryBoofOrder(outTradeNo, ifmBaseDictMapper);
		if(!"".equals(StringUtil.nvl(result))){
			JSONObject object = (JSONObject) JSON.parse(result);
			String resultNote = StringUtil.nvl(object.get("resultNote"));
			JSONObject reObject = (JSONObject) JSON.parse(resultNote);
			String order_stat = StringUtil.nvl(reObject.get("order_stat"));
			if("S".equals(order_stat) || "I".equals(order_stat)){ //S:交易成功，I:处理中
				flag = false;
			}
		}else{
			flag = null;
		}
		return flag;
	}
	
	/**
	 * 根据外部订单号查询宝付订单状态
	 * @param outTradeNo
	 * @param ifmBaseDictMapper
	 * @return
	 */
	public static String qryBoofOrder(String outTradeNo, IfmBaseDictMapper ifmBaseDictMapper){
		// 宝付微信支付URL
		List<IfmBaseDict> wxpay = ifmBaseDictMapper.fetchDictsByType("WX_PAY_URL");
		String wxPayUrl = wxpay.get(0).getItemValue();
		Map<String, Object> vamap = new HashMap<String, Object>();
		vamap.put("appKey", Constants.APPKEY);
		vamap.put("appSecrect", Constants.APPSECRECT);
		vamap.put("timeSpan", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		vamap.put("orig_trans_id", outTradeNo);
		String signStr = StringUtil.md5Sign(vamap, true);
		vamap.put("sign", signStr);
		vamap.remove("appSecrect");
		JSONObject cut = new JSONObject();
		cut.put("cmd", "boofPayHoldingQuery");
		cut.put("token", "245Y7BSfDHIWEie34");
		cut.put("version", "1");
		cut.put("params", vamap);
		String result = APIHttpClient.doPost(wxPayUrl + "/boofPayHoldingQuery", cut.toJSONString());
		return result;
	}
	
	/**
	 * 是否可以创建支付宝订单
	 * @param outTradeNo
	 * @param tradeNo
	 * @param appAliPay
	 * @return false（不能创建，交易创建，等待买家付款或订单支付成功） true（可以创建）
	 */
	private boolean isCreateZfbOrder(String outTradeNo, String tradeNo, AppAliPay appAliPay){
		boolean flag = true;
		Map<String, Object> orderStatusMap = AliPayUtilNew.getOrderStatus(outTradeNo, tradeNo, appAliPay);
		String tradeStatus = StringUtil.nvl(orderStatusMap.get("tradeStatus"));
		if("TRADE_SUCCESS".equals(tradeStatus) || "WAIT_BUYER_PAY".equals(tradeStatus)){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 根据资方确定支付宝账号
	 * @param withDrawAppl
	 * @return
	 */
	private AppAliPay getAliPayByCoorpration(APPWithDrawAppl withDrawAppl){
		AppAliPay appAliPay = new AppAliPay();
		if(null != withDrawAppl){
			String cooperation = withDrawAppl.getCooperation();
			if ("1".equals(cooperation)) {
				appAliPay = appAliPayMapper.selectByPrimaryKey(1); //泰山创投
			} else {
				appAliPay = appAliPayMapper.selectByPrimaryKey(3); //其他
			}
		}
		return appAliPay;
	}
	
	/**
	 * 获取宝付请求数据
	 * @param loanCtm
	 * @param appKey
	 * @param appSecrect
	 * @param out_trade_no
	 * @param totalAmt
	 * @param notifyUrl
	 * @return
	 */
	private JSONObject getBoofReqParam(AppLoanCtm loanCtm, String appKey, String appSecrect, String out_trade_no, String totalAmt, String notifyUrl, String bankCode, APPWithDrawAppl drawAppl){
		String customName = loanCtm.getCustomName();
		String idCard = loanCtm.getIdentityCard();
		String bankCard = loanCtm.getBankCard();
		String bankPhone = loanCtm.getBankPhone();
		String cardType = "01";
		// 获取bankCode
		String payCm = "2";
		String bizType = "0000";
		String additionalInfo = "随心花代扣";
		//访问代扣接口
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("appKey", appKey);
		map2.put("appSecrect", appSecrect);
		map2.put("out_trade_no", out_trade_no);
		map2.put("deductAmount", totalAmt);
		map2.put("name", customName);
		map2.put("certNo", idCard);
		map2.put("bankCardNo", bankCard);
		map2.put("id_card_type", cardType);
		map2.put("bankCode", bankCode);
		map2.put("pay_cm", payCm);
		map2.put("mobileno", bankPhone);
		map2.put("biz_type", bizType);
		map2.put("additional_info", additionalInfo);
		map2.put("notifyUrl", notifyUrl);
		if("8".equals(StringUtil.nvl(drawAppl.getCooperation()))){
			map2.put("dk_type", "zhqb"); 	//纵横账号
		}else{
			map2.put("dk_type", "tsb");		//泰山宝账号
		}
		JSONObject object = new JSONObject();
		object.put("cmd", "fastBoofWithholding");
		object.put("token", "245Y7BSfDHIWEie34");
		object.put("version", "1");
		object.put("params", map2);
		return object;
	}
	
	/**
	 * 根据bank获取bankCode
	 * @param bank
	 * @return
	 */
	private String getBankCodeByBank(String bank){
		String bankCode = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dataType", "BANK_NAME");
		map.put("perion", bank);
		IfmBaseDict ifmBaseDict = ifmBaseDictMapper.selectfWFAndRate(map);
		if (null != ifmBaseDict) {
			bankCode = ifmBaseDict.getOutDataFrom();
		}else{
			AppAgreementPayBank agreementPayBank = appAgreementPayBankMapper.selectByCode(bank);
			if(null != agreementPayBank){
				bankCode = StringUtil.nvl(agreementPayBank.getBankCode());
			}
		}
		return bankCode;
	}
	
	
	/**
	 * 验卡  支付开关
	 */
	@Override
	public String withholdingSwitch() {
		List<IfmBaseDict> baseDicts = ifmBaseDictMapper.fetchDictsByType("CHECKCARD_SWITCH");
		String baseDict = "".equals(StringUtil.nvl(baseDicts.get(0).getItemValue()))?"2":StringUtil.nvl(baseDicts.get(0).getItemValue());
		return baseDict;
	}
	
	
	/**
     * @author zw 
     * 直接支付接口
     * @param params
     * @return
     * @throws Exception
     */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject fastBoofProtocolPay(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String repayIds = JsonUtil.getJStringAndCheck(params, "repayIds", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		// 判断是否在正确的支付时间内
		if(!isPayAtRightTime()){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "财务正在结算中");
			return detail;
		}
		JSONArray datelist = params.getJSONArray("repayIds");
		if(null != datelist && datelist.size() > 0){
			if(datelist.size() == 1){
				AppUser appUser = appUserMapper.selectByMchVersion(userId);
				if (null != appUser) {
					AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
					String repayId0 = StringUtil.toString(((JSONObject) datelist.get(0)).get("repayId"));
					APPPayPlan payPlan0 = aPPPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId0));
					// 还款校验
					JSONObject checkObject = checkPayOrder(appLoanAppl, datelist, payPlan0);
					if(null != checkObject){
						return checkObject;
					}
					Integer appPlanWithId = payPlan0.getWithdrawId();
					// 根据资方确定支付宝账号
					APPWithDrawAppl drawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(appPlanWithId);
					AppAliPay appAliPay = getAliPayByCoorpration(drawAppl);
					// 判断该订单的代扣次数
					List<IfmWithhold> ifmWithholds = ifmWithholdMapper.selectByPlanId(payPlan0.getId()+"");
					
					List<Map<String,Object>> nums = ifmBaseDictMapper.selectBaseDict("AGREEMENT_TO_PAY_NUM");
					String num = StringUtil.nvl(nums.get(0).get("ITEM_VALUE"));
					num = "".equals(num)?"3":num;
					if(ifmWithholds!=null && ifmWithholds.size()>=Integer.parseInt(num)){
						List<Map<String,Object>> whiteList = ifmBaseDictMapper.selectBaseDict("PAY_ZFB_WHITE_LIST");
						String whitelist = StringUtil.nvl(whiteList.get(0).get("ITEM_VALUE"));
						StringTokenizer st = new StringTokenizer(whitelist,",");
						String withdrawid = StringUtil.nvl(drawAppl.getId());
						boolean flag = true;
				        while(st.hasMoreTokens()){
				        	if(withdrawid.equals(st.nextToken())){
				        		flag = false;
				        	}
				        }
				        if(flag){
				        		String white = whitelist + "," + withdrawid;
				        		IfmBaseDict ifmBaseDict = new IfmBaseDict();
				        		ifmBaseDict.setDataType("PAY_ZFB_WHITE_LIST");
				        		ifmBaseDict.setItemValue(white);
				        		try{
				        			ifmBaseDictMapper.updateByDataType(ifmBaseDict);
				        			detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "代扣次数超限，已为您该笔订单开通支付宝还款通道，请使用支付宝还款");
				        		}catch(Exception e){
				        			detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "代扣次数超限，请联系客服还款");
				        		}
				        }else{
				        	detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "代扣次数超限，已为您该笔订单开通支付宝还款通道，请使用支付宝还款");
				        } 
						return detail;
					}
					String out_trade_no = UUID.randomUUID().toString();
					synchronized (this) {
						JSONObject jObject = null;
						APPPayPlan payPlan = null;
						AppLoanCtm loanCtm = null;
						double totalAmt = 0.0;
						for (Object obj : datelist) {
							jObject = (JSONObject) obj;
							int repayPlanId = Integer.parseInt(StringUtil.nvl(jObject.get("repayId")));
							// 支付ID加入缓存
							if (redisService.exists("MCH_PAY_REPAYID_" + repayPlanId)) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
								return detail;
							} else {
								redisService.set("MCH_PAY_REPAYID_" + repayPlanId, "orderId", 20);
							}
							payPlan = aPPPayPlanMapper.selectByPrimaryKey(repayPlanId);
							double fxje = null == payPlan.getFxje() ? 0 : payPlan.getFxje();
							double monthPay = null == payPlan.getMonthPay() ? 0 : payPlan.getMonthPay();
							double sumAmt = StringUtil.getCalculatorResult(fxje, monthPay, 0);
							// 创建支付订单
							List<AppzfbOrderList> orderLists = appzfbOrderListMapper.selectAllByRepayPlanId(payPlan.getId());
							if (null != orderLists && orderLists.size() > 0) {
								AppzfbOrderList order = orderLists.get(orderLists.size() - 1);
								// 先判断该笔订单支付状态
								String outTradeNo = order.getWidoutTradeNo();
								//查询协议订单支付状态
								Boolean createBoofOrderResult = isCreateAgreementPayOrder(outTradeNo,order.getZfbPayDate(),ifmBaseDictMapper);
								//支付宝支付状态
								Boolean createZfbOrderResult = isCreateZfbOrder(outTradeNo, "", appAliPay);
								// 检查订单是否可以支付
								if(null == createBoofOrderResult){
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "请求超时，请稍后重试！");
									return detail;
								}
								if(createBoofOrderResult && createZfbOrderResult){
									createZfbListOrder4Boof(sumAmt, payPlan.getId() + "", out_trade_no, drawAppl);
								}else{
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "订单正在支付中，请稍后重试！");
									return detail;
								}
							}else{
								createZfbListOrder4Boof(sumAmt, payPlan.getId() + "", out_trade_no, drawAppl);
							}
							totalAmt += sumAmt;
						}
						loanCtm = appLoanCtmMapper.selectByapprId(payPlan.getApprId());
						String bankCode = getBankCodeByBank(loanCtm.getBank());
						String merchSignOrderNo= StringUtil.nvl(loanCtm.getMerch_order_no());
						// 创建代扣订单
						IfmWithhold ifmWithhold = createAgreementPayOrder(totalAmt, payPlan, loanCtm, out_trade_no, bankCode,merchSignOrderNo);
						if("".equals(merchSignOrderNo)){
							ifmWithhold.setResultMessage("代扣失败(没有协议ID)");
							ifmWithholdMapper.updateByPrimaryKeySelective(ifmWithhold);
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "请更新您的绑卡信息，再次支付");
							return detail;
						}
						// 获取宝付请求数据
						JSONObject param = getAgreementPayReqParam(loanCtm,out_trade_no,totalAmt + "", drawAppl);
						List<IfmBaseDict> baseDicts = ifmBaseDictMapper.fetchDictsByType("AGREEMENT_TO_PAY_URL");
						String url = StringUtil.nvl(baseDicts.get(0).getItemValue());
						String res = APIHttpClient.doPost(url+"/fastBoofProtocolPay", param,60000);
						if(res==null||"".equals(res)){
							ifmWithhold.setResultMessage("代扣失败，请重试(请求异常返回---"+res+"---)");
							ifmWithholdMapper.updateByPrimaryKeySelective(ifmWithhold);
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "代扣失败，请重新绑卡/重新支付");
						}else{
							JSONObject object = (JSONObject) JSON.parse(res);
							String result = StringUtil.nvl(object.get("result"));//0：失败;1成功
							String resultNote = StringUtil.nvl(object.get("resultNote"));//返回结果说明
							String resultCode = StringUtil.nvl(object.get("resultCode"));//
							if("1".equals(result)){
								detail.put(Consts.RESULT, ErrorCode.SUCCESS);
								detail.put(Consts.RESULT_NOTE, resultNote);
								detail.put("withholdTradeNo", out_trade_no);
							}else{
								ifmWithhold.setNotifyTime(new Date());
								ifmWithhold.setServiceStatus(resultCode);
								ifmWithhold.setResultMessage(resultNote);
								ifmWithholdMapper.updateByPrimaryKeySelective(ifmWithhold);
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, resultNote);
							}
						}
						//删除redis
						for(Object obj : datelist){
							jObject = (JSONObject) obj;
							if(redisService.exists("MCH_PAY_REPAYID_" + StringUtil.nvl(jObject.get("repayId")))){
								redisService.del("MCH_PAY_REPAYID_" + StringUtil.nvl(jObject.get("repayId")));
							}
						}
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "目前只支持单期还款，请重试！");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "期数不能为空");
		}
		return detail;
		
	}

	/**
	 * 创建协议支付 代扣记录
	 * @param totalAmt
	 * @param payPlan
	 * @param loanCtm
	 * @param out_trade_no
	 * @param bankCode
	 */
	private IfmWithhold createAgreementPayOrder(double totalAmt, APPPayPlan payPlan, AppLoanCtm loanCtm, String out_trade_no,
			String bankCode,String merchSignOrderNo) {
		// 创建代扣回调记录
		IfmWithhold ifmWithhold = new IfmWithhold();
		ifmWithhold.setAppr_id(payPlan.getWithdrawId() + "");
		ifmWithhold.setBankCardNo(loanCtm.getBankCard());
		ifmWithhold.setBankCode(bankCode);
		ifmWithhold.setBankName(bankCode);
		ifmWithhold.setDeductAmount(totalAmt);
		ifmWithhold.setMerchOrderNo(out_trade_no);
		ifmWithhold.setMerchSignOrderNo(merchSignOrderNo);
		ifmWithhold.setName(loanCtm.getCustomName());
		ifmWithhold.setPlan_id(payPlan.getId() + "");
		ifmWithhold.setReceiptTime(new Date());
		ifmWithhold.setCreate_date(new Date());
		ifmWithholdMapper.insertSelective(ifmWithhold);
		return ifmWithhold;
	}
	
	/**
	 * 是否可以创建协议支付订单
	 * @param outTradeNo
	 * @param ifmBaseDictMapper
	 * @return false（不能创建，订单支付中或支付成功） true（可以创建）
	 */
	private Boolean isCreateAgreementPayOrder(String outTradeNo,Date tradeDate, IfmBaseDictMapper ifmBaseDictMapper){
		Boolean flag = true;
		String result = qryAgreementPayOrder(outTradeNo,tradeDate, ifmBaseDictMapper);
		if(!"".equals(StringUtil.nvl(result))){
			JSONObject object = (JSONObject) JSON.parse(result);
			String resultNote = StringUtil.nvl(object.get("resultNote"));
			String res = StringUtil.nvl(object.get("result"));
			if(!"1".equals(res)){
				return flag;
			}
			JSONObject reObject = (JSONObject) JSON.parse(resultNote);
			String order_stat = StringUtil.nvl(reObject.get("resp_code"));
			if("S".equals(order_stat) || "I".equals(order_stat)){ //S:交易成功，I:处理中
				flag = false;
			}
		}else{
			flag = null;
		}
		return flag;
	}
	
	/**
	 * 根据外部订单号查询协议支付订单状态
	 * @param outTradeNo
	 * @param ifmBaseDictMapper
	 * @return
	 */
	public static String qryAgreementPayOrder(String outTradeNo,Date tradeDate,IfmBaseDictMapper ifmBaseDictMapper){
		// 宝付协议支付URL
		List<IfmBaseDict> wxpay = ifmBaseDictMapper.fetchDictsByType("AGREEMENT_PAY_QUERY_URL");
		String wxPayUrl = wxpay.get(0).getItemValue();
		String uuid = StringUtil.nvl(UUID.randomUUID()).replace("-", "");
		Map<String, Object> vamap = new HashMap<String, Object>();
		vamap.put("appKey", Constants.APPKEY);
		vamap.put("appSecrect", Constants.APPSECRECT);
		vamap.put("timeSpan", DateUtil.format(new Date(), "yyyyMMddHHmmss"));
		vamap.put("msg_id", uuid);
		vamap.put("orig_trans_id", outTradeNo);
		if(tradeDate==null||"".equals(tradeDate)){
			tradeDate = new Date();
		}
		vamap.put("orig_trade_date", DateUtil.format(tradeDate,"yyyy-MM-dd HH:mm:ss"));
		String signStr = StringUtil.md5Sign(vamap, true);
		vamap.put("sign", signStr);
		vamap.remove("appSecrect");
		JSONObject cut = new JSONObject();
		cut.put("cmd", "fastBoofProtocolQuery");
		cut.put("token", "245Y7BSfDHIWEie34");
		cut.put("version", "1");
		cut.put("params", vamap);
		String result = APIHttpClient.doPost(wxPayUrl + "/fastBoofProtocolQuery", cut.toJSONString());
		return result;
	}
	
	/**
	 * 获取协议支付请求数据
	 * @param loanCtm
	 * @param appKey
	 * @param appSecrect
	 * @param out_trade_no
	 * @param totalAmt
	 * @param notifyUrl
	 * @return
	 */
	private JSONObject getAgreementPayReqParam(AppLoanCtm loanCtm,String outOrderNo, String totalAmt, APPWithDrawAppl drawAppl){
		String merchOrderNo = loanCtm.getMerch_order_no();
		String dk_type = "";
		if("8".equals(StringUtil.nvl(drawAppl.getCooperation()))){
			dk_type = "zhqb"; 	//纵横账号
		}else{
			dk_type = "tsb";		//泰山宝账号
		}
		//访问代扣接口
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("merchOrderNo", merchOrderNo);
		map2.put("signType", "3");
		map2.put("trans_id", outOrderNo);
		map2.put("txn_amt", totalAmt);
		map2.put("bankCardNo", StringUtil.nvl(loanCtm.getBankCard()));
		map2.put("dk_type", dk_type);
		JSONObject object = new JSONObject();
		object.put("cmd", "fastBoofProtocolPay");
		object.put("token", "245Y7BSfDHIWEie34");
		object.put("version", "1");
		object.put("params", map2);
		return object;
	}
	
}
