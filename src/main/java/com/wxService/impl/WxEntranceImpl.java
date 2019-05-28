package com.wxService.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoginMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppUserMapper;
import com.dao.AppWxBindMapper;
import com.dao.AppWxSessionMapper;
import com.dao.AppzfbOrderListMapper;
import com.dao.IfmAliPayOrderMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.frame.RedisService;
import com.model.APPCredit;
import com.model.APPPayPlan;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLogin;
import com.model.AppOprLog;
import com.model.AppPhoneValicode;
import com.model.AppUser;
import com.model.AppWxBind;
import com.model.AppWxSession;
import com.model.AppzfbOrderList;
import com.model.IfmAliPayOrder;
import com.model.IfmBaseDict;
import com.util.APIHttpClient;
import com.util.Constants;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.InvitationCodeUtil;
import com.util.JsonUtil;
import com.util.StringUtil;
import com.util.UUID;
import com.wxService.WxEntranceIntf;

@Service
public class WxEntranceImpl implements WxEntranceIntf {
	@Autowired
	private AppWxBindMapper appWxBindMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoginMapper ifmLoginMapper;
	@Autowired
	private AppWxSessionMapper   appWxSessionMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private APPCreditMapper aPPCreditMapper;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppzfbOrderListMapper appzfbOrderListMapper;
	@Autowired
	private IfmAliPayOrderMapper ifmAliPayOrderMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Autowired
	private RedisService redisService;

	@Override
	public JSONObject bind(JSONObject params) {
		JSONObject detail = new JSONObject();
		String openId = JsonUtil.getJStringAndCheck(params, "openId", null, true, detail);
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);
		String passWord = JsonUtil.getJStringAndCheck(params, "passWord", null, true, detail);
		/*String iphone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
		params.put("phone", iphone);*/
		
		
		List<AppWxBind> AppWxBind=appWxBindMapper.selectByPhone(phone);
		AppLoanAppl appLoanAppl=appLoanApplMapper.selectByitemCode(phone);
		AppLogin ifmLogin=new AppLogin();
		ifmLogin.setPassword(passWord);
		ifmLogin.setUserCode(phone);
		
		List<AppWxBind> appWxBind = appWxBindMapper.selectOpenId(openId);
		if(appWxBind!=null && !appWxBind.isEmpty()){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该微信帐号已绑定过，请先解绑！");
			/*params.put("phone", iphone);*/
			return detail;
		}
		
		AppLogin chechPhone=ifmLoginMapper.selectByPhone(phone);
		if(chechPhone==null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "您还未注册,请先注册!");
			/*params.put("phone", iphone);*/
			return detail;
		}
		
		AppLogin login=ifmLoginMapper.login(ifmLogin);
		if(login==null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "您的账号与密码不匹配，请重新输入");
			/*params.put("phone", iphone);*/
			return detail;
		}				
		AppWxSession wxs=appWxSessionMapper.selectBywxUserId(appLoanAppl.getId());
		if(null==wxs){
			AppWxSession ws=new AppWxSession();
			ws.setLgTime(new Date());
			ws.setSession(openId);
			ws.setWxApprid(appLoanAppl.getId());
			appWxSessionMapper.insertSelective(ws);
		}else{
			wxs.setLgTime(new Date());
			wxs.setSession(openId);
			appWxSessionMapper.updateByPrimaryKeySelective(wxs);
		}
		if(AppWxBind.size()<1){
				AppWxBind wx=new AppWxBind();
				wx.setApprId(appLoanAppl.getId());
				String uuid=UUID.generate();
				wx.setUuid(uuid);
				wx.setCreatTime(new Date());
				wx.setOpenId(openId);
				wx.setPhone(appLoanAppl.getItemCode());
				wx.setBindTime(new Date());
				appWxBindMapper.insertSelective(wx);
				detail.put("apprId", uuid);
				/*params.put("phone", iphone);*/
				return detail;
		}else{
			AppWxBind bind=AppWxBind.get(0);
			bind.setOpenId(openId);
			bind.setBindTime(new Date());
			appWxBindMapper.updateByPrimaryKeySelective(bind);
			detail.put("apprId", bind.getUuid());
			/*params.put("phone", iphone);*/
			return detail;
		}
	}

	@Override
	public JSONObject relieveBind(JSONObject params) {
		JSONObject detail = new JSONObject();
		//String apprId = JsonUtil.getJStringAndCheck(params, "apprId", null, true, detail);
		String openId = JsonUtil.getJStringAndCheck(params, "openId", null, true, detail);
		//Map<String, String> map=new HashMap<>();
		//map.put("apprId", apprId);
		//map.put("openId", openId);
		AppWxBind bind=appWxBindMapper.selectByopenId(openId);
		if(null==bind){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "解绑失败，请检查是否已绑定");
			return detail;
		}else{
			bind.setOpenId("");
			//bind.setBindTime("");
			//appWxBindMapper.updateByPrimaryKeySelective(bind);
			appWxBindMapper.deleteByPrimaryKey(bind.getId());
			appWxSessionMapper.deleteByApprid(bind.getApprId());
			detail.put(Consts.RESULT_NOTE, "解绑成功");
			return detail;
		}
	}

	@Override
	public JSONObject qurMyQuota(JSONObject params) {
		JSONObject detail = new JSONObject();
		String apprId = JsonUtil.getJStringAndCheck(params, "apprId", null, true, detail);
		List<AppWxBind> bind=appWxBindMapper.selectByUuid(apprId);
		if(null==bind||bind.size()<=0){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "你还未绑定，请先绑定");
			return detail;
		}else{
			AppWxBind bi=bind.get(0);
			AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(bi.getApprId());
			APPCredit amt=aPPCreditMapper.selectByApprId(bi.getApprId());
			AppLoanAppl appl=appLoanApplMapper.selectByPrimaryKey(bi.getApprId());
			if(null!=amt&&appl.getStatus().equals("2")){
				detail.put("status", "3");
				detail.put("creditAmt", StringUtil.formatNumberToDecimals(StringUtil.nvl(amt.getCreditAmt()), 2)+"");
				detail.put("useAmt", StringUtil.formatNumberToDecimals(StringUtil.nvl(amt.getUseAmt()), 2)+"");//授信成功				
				//detail.put("creditAmt", StringUtil.toString(amt.getCreditAmt())+"");
				//detail.put("useAmt", StringUtil.toString(amt.getUseAmt())+"");//授信成功
			}else if(appl.getStatus().equals("3")){
				detail.put("status", "4");//被拒				
				//long i=30-(new Date().getTime()-appl.getAuthenTime().getTime())/86400000;
				long i=30-DateUtil.getDateDaysBetween(appl.getAuthenTime(), new Date());
				if(i<0||i==0){
					i=1;
				}
				detail.put("surplusDays",StringUtil.toString(i));
			}else if(ctm.getSchedule_status().equals("8")&&appl.getStatus().equals("1")){
				detail.put("status", "2");//授信中
			}else if(Integer.parseInt(ctm.getSchedule_status())>3&&Integer.parseInt(ctm.getSchedule_status())<8||(appl.getStatus().equals("8"))){
				detail.put("status", "1");//完善资料中
			}else {
				detail.put("status", "0");//注册
			}
			return detail;
		}
	}

	@Override
	public JSONObject queryWxOrder(JSONObject params) {	
		JSONObject detail = new JSONObject();
		String apprId = JsonUtil.getJStringAndCheck(params, "apprId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		List<AppWxBind> bind=appWxBindMapper.selectByUuid(apprId);
		if(null != bind&&bind.size()>0){
			AppWxBind wx=bind.get(0);
			AppLoanAppl loanAppl = appLoanApplMapper.selectByPrimaryKey(wx.getApprId());
			if(null != loanAppl){
				JSONArray jsonArray = new JSONArray();
				List<Map<String, Object>> lists = aPPWithDrawApplMapper.selectOrderRecords(loanAppl.getId());
				if(null != lists && lists.size() > 0){
					String withId = "";
					String status = "";
					String contractNo = "";
					String borrowAmt = "";
					String moneyPay = "";
					String borrowTime = "";
					String repayTime = "";
					String fxje = "";
					String repayId = "";
					List<APPPayPlan> payPlans = null;
					APPPayPlan appPayPlan = null;
					JSONObject object = null;
					for(Map<String, Object> list : lists){
						if(list.get("status").equals("1")||list.get("status").equals("2")){
						withId = StringUtil.nvl(list.get("id"));
						status = StringUtil.nvl(list.get("status"));
						contractNo = StringUtil.nvl(list.get("contract_no"));
						repayId = StringUtil.nvl(list.get("repay_id"));
						fxje = StringUtil.formatNumberToDecimals(StringUtil.nvl(list.get("fxje")), 2);					
						borrowAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(list.get("borrow_amt")), 2);
						moneyPay = StringUtil.formatNumberToDecimals(StringUtil.nvl(list.get("money_pay")), 2);
						java.text.DecimalFormat   df   = new   java.text.DecimalFormat("0.00");
						String borrowAmtTotal = df.format(Double.valueOf(fxje)+ Double.valueOf(moneyPay));
						//String borrowAmtTotal = Double.toString(ss);
						borrowTime = DateUtil.format(DateUtil.parseDate(StringUtil.nvl(list.get("borrow_time"))), "yyyy/MM/dd");
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
						object = new JSONObject();
						//object.put("withId", withId);
						object.put("loanStatus", status);
						object.put("contractNo", contractNo);
						object.put("repayId", repayId);
						object.put("borrowAmt", borrowAmt);
						object.put("borrowTime", borrowTime);
						object.put("repayTime", repayTime);
						object.put("borrowAmtTotal", borrowAmtTotal);
						jsonArray.add(object);
					}
				}
					}
				detail.put("orderRecordLists", jsonArray);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未绑定，请先登录");
		}
		return detail;
	}

	@Override
	public JSONObject welogin(JSONObject params) {
		JSONObject detail = new JSONObject();
		String openId = JsonUtil.getJStringAndCheck(params, "wxOpenId", null, true, detail);
		if(openId.equals("")){
			detail.put("status", "-1");
			return detail;
		}
		AppWxBind appWxBind=appWxBindMapper.selectByopenId(openId);
		if(null!=appWxBind){
			detail.put("status", "1");
			detail.put("apprId", appWxBind.getUuid());
		}else{
			detail.put("status", "-1");
			
		}
		return detail;
	}
	
	@Override
	public JSONObject qurMyPay(JSONObject params) {
		JSONObject detail = new JSONObject();
		String apprId = JsonUtil.getJStringAndCheck(params, "apprId", null, true, detail);
		List<AppWxBind> bind=appWxBindMapper.selectByUuid(apprId);
		if(null==bind||bind.size()<=0){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "你还未绑定，请先绑定");
			return detail;
		}else{
			AppWxBind bi=bind.get(0);
			AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(bi.getApprId());
			APPCredit amt=aPPCreditMapper.selectByApprId(bi.getApprId());
			AppLoanAppl appl=appLoanApplMapper.selectByPrimaryKey(bi.getApprId());
			if(null!=amt&&appl.getStatus().equals("2")){
				detail.put("status", "3");
				detail.put("creditAmt", StringUtil.toString(amt.getCreditAmt())+"");
				detail.put("useAmt", StringUtil.toString(amt.getUseAmt())+"");//授信成功
			}else if(appl.getStatus().equals("3")){
				detail.put("status", "4");//被拒
				long i=30-(new Date().getTime()-appl.getAuthenTime().getTime())/86400000;
				if(i<0||i==0){
					i=1;
				}
				detail.put("surplusDays",StringUtil.toString(i));
			}else if(ctm.getSchedule_status().equals("8")&&appl.getStatus().equals("1")){
				detail.put("status", "2");//授信中
			}else if(Integer.parseInt(ctm.getSchedule_status())>3&&Integer.parseInt(ctm.getSchedule_status())<8||(appl.getStatus().equals("8"))){
				detail.put("status", "1");//完善资料中
			}else {
				detail.put("status", "0");//注册
			}
			return detail;
		}
	}
				
	@Override
	public JSONObject doPayToWx(JSONObject params) {
		JSONObject detail = new JSONObject();
		String apprId = JsonUtil.getJStringAndCheck(params, "apprId", null, true, detail);
		String openId = JsonUtil.getJStringAndCheck(params, "openId", null, true, detail);
		String repayId = JsonUtil.getJStringAndCheck(params, "repayId", null, true, detail);
		List<AppWxBind> bind = appWxBindMapper.selectByUuid(apprId);
		if (null == bind || bind.size() <= 0) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "你还未绑定，请先绑定");
			return detail;
		} else {
			// 防止有人故意窜改数据
			APPPayPlan plan = appPayPlanMapper.selectByPrimaryKey(Integer.parseInt(repayId));
			if (null == plan) {
				// 恶意修改还款ID的
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您当前没有此订单");
				return detail;
			} else if (bind.get(0).getApprId().intValue() != plan.getApprId().intValue()) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "只能支付属于自己的订单");
				return detail;
			}

			// 宝付微信支付URL
			List<IfmBaseDict> wxpay = ifmBaseDictMapper.fetchDictsByType("WX_PAY_URL");
			String wxPayUrl = wxpay.get(0).getItemValue();
			// 获取公众号ID
			List<IfmBaseDict> IfmBaseDict2 = ifmBaseDictMapper.fetchDictsByType("MCH_WX_MES_TMP");
			IfmBaseDict base = IfmBaseDict2.get(0);
			String appid = base.getItemValue();
			JSONObject retrunResult = new JSONObject();
			try {
				List<AppzfbOrderList> inOrder = new ArrayList<AppzfbOrderList>();
				AppzfbOrderList io = new AppzfbOrderList();
				String monthPay = StringUtil.formatNumberToDecimals(StringUtil.nvl(plan.getMonthPay()), 2);
				String fxje = StringUtil.formatNumberToDecimals(StringUtil.nvl(plan.getFxje()), 2);
				java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
				String total = df.format(Double.valueOf(fxje) + Double.valueOf(monthPay));

				if (!"".equals(openId)) {
					String textStr = "微信支付（宝付）";
					double paycmtCount = 0.00;
					String uuId = StringUtil.nvl(UUID.generate());
					String uuIdStr = uuId;
					Date date = new Date();
					List<IfmBaseDict> baseDicts_callback_url = ifmBaseDictMapper
							.qryIfmBaseDict("WX_PAY_XS_CALLBACK_URL");
					List<AppzfbOrderList> appzfbOrderList = appzfbOrderListMapper
							.selectZfbOrderListAndPaySouce(repayId);

					long now = new Date().getTime();
					if (appzfbOrderList.size() > 0) {
						for (AppzfbOrderList app : appzfbOrderList) {
							long create = app.getCreateDate().getTime();
							if (!"".equals(StringUtil.nvl(app.getZfbOrderNo()))
									&& !"".equals(StringUtil.nvl(app.getZfbPayDate()))) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "您的订单存在已支付完成的记录");
								return detail;
							} else if (now >= create && (now - create) < 60000) {// 1分钟有效
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "操作频繁，请于一分钟后重试！");
								return detail;
							} else if (now >= create && (now - create) > 60000) {// 超过1分钟并且回调方法失败的情况下

								Map<String, Object> vamap = new HashMap<String, Object>();
								vamap.put("appKey", Constants.APPKEY);
								vamap.put("appSecrect", Constants.APPSECRECT);
								vamap.put("timeSpan", com.util.DateUtils.getCurrentDateAndTime());
								vamap.put("txn_sub_type", "03");
								vamap.put("orig_trans_id", app.getWidoutTradeNo());
								String signStr = StringUtil.md5Sign(vamap, true);
								vamap.put("sign", signStr);
								vamap.remove("appSecrect");
								JSONObject cut = new JSONObject();
								cut.put("cmd", "boofPayQuery");
								cut.put("token", "245Y7BSfDHIWEie34");
								cut.put("version", "1");
								cut.put("params", vamap);
								String result = APIHttpClient.doPost(wxPayUrl + "/boofPayQuery", cut.toJSONString());
								JSONObject returnJson = JSON.parseObject(result);// 宝付接口返回的一些参数集合
								if ("1".equals(returnJson.getString("result"))) {
									retrunResult = (JSONObject) JSON.parse(returnJson.getString("resultNote"));
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "该笔订单已支付过！");
									return detail;
								} else {
									// appzfbOrderListMapper.deleteByPrimaryKey(app.getOrderId());
									io.setOrderId(appzfbOrderList.get(0).getOrderId());
									io.setWidtotalFee(total);
									io.setCreateDate(date);
									io.setWidoutSubject(textStr);
									io.setPaySource("0");
									inOrder.add(io);

									double borrowAmtTotalDouble = Double.valueOf(total);
									paycmtCount += borrowAmtTotalDouble;
									if (paycmtCount == 0.00) {
										detail.put(Consts.RESULT, ErrorCode.FAILED);
										detail.put(Consts.RESULT_NOTE, "待支付总金额为零!");
										return detail;
									} else {// 更新支付宝订单信息
										for (AppzfbOrderList iod : inOrder) {
											appzfbOrderListMapper.updateByPrimaryKeySelectiveAndPaySouce(iod);
										}
									}
								}
							}
						}
					} else {
						io.setWidoutTradeNo(uuIdStr);
						io.setWidtotalFee(total);
						io.setCreateDate(date);
						io.setWidoutSubject(textStr);
						io.setRepayPlanId(repayId);
						io.setPaySource("0");
						inOrder.add(io);

						double borrowAmtTotalDouble = Double.valueOf(total);
						paycmtCount += borrowAmtTotalDouble;
						if (paycmtCount == 0.00) {
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "待支付总金额为零!");
							return detail;
						} else {// 插入支付宝订单信息
							for (AppzfbOrderList iod : inOrder) {
								appzfbOrderListMapper.insertSelective(iod);
							}
						}

					}

					synchronized (this) {
						if (!"".equals(StringUtil.nvl(redisService.get("MCH_PAY_ORDER_PAYID-" + repayId)))) {
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "该订单正在支付中，请稍后再试");
							return detail;
						} else {
							// 设置存活时间1分钟
							redisService.set("MCH_PAY_ORDER_PAYID-" + repayId, "MCH_PAY_ORDER_PAYID-" + repayId, 60);
						}
					}

					List<AppzfbOrderList> appzfbOrderListOut = appzfbOrderListMapper
							.selectZfbOrderListAndPaySouce(repayId);
					if(appzfbOrderListOut.size() > 0){											
					// 日志
					IfmAliPayOrder order = new IfmAliPayOrder();
					order.setCallbackUrl(baseDicts_callback_url.get(0).getItemValue());
					order.setOrderNo(appzfbOrderListOut.get(0).getWidoutTradeNo());
					ifmAliPayOrderMapper.deleteIfmAliPayOrder(order.getOrderNo());
					ifmAliPayOrderMapper.insertIfmAliPayOrder(order);

					// 传到pay里的boofPay方法
					Map<String, Object> vamap = new HashMap<String, Object>();
					vamap.put("appKey", Constants.APPKEY);
					vamap.put("appSecrect", Constants.APPSECRECT);
					vamap.put("out_trade_no", appzfbOrderListOut.get(0).getWidoutTradeNo());
					vamap.put("total_amount", df.format(paycmtCount));
					vamap.put("txn_sub_type", "15");
					vamap.put("open_id", openId);
					vamap.put("appid", appid);
					vamap.put("subject", textStr);
					vamap.put("commodity_amount", appzfbOrderList.size());
					vamap.put("user_id", "123");
					vamap.put("user_name", "测试");
					vamap.put("return_url", baseDicts_callback_url.get(0).getItemValue());
					vamap.put("notify_url", baseDicts_callback_url.get(0).getItemValue());
					vamap.put("timeSpan", com.util.DateUtils.getCurrentDateAndTime());
					String signStr = StringUtil.md5Sign(vamap, true);
					vamap.put("sign", signStr);
					vamap.remove("appSecrect");
					JSONObject object = new JSONObject();
					object.put("cmd", "boofPay");
					object.put("token", "245Y7BSfDHIWEie34");
					object.put("version", "1");
					object.put("params", vamap);
					String result = APIHttpClient.doPost(wxPayUrl + "/boofPay", object.toJSONString());
					JSONObject returnJson = JSON.parseObject(result);// 宝付接口返回的一些参数集合
					if ("1".equals(returnJson.getString("result"))) {
						retrunResult = (JSONObject) JSON.parse(returnJson.getString("resultNote"));
						detail.put("retrunResult", retrunResult);
					} else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, returnJson.getString("resultNote"));
						return detail;
					}
				}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "发起微信支付失败;openID为空！");
					return detail;
				}
			} catch (Exception e) {
				e.printStackTrace();
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "发起微信支付失败");
				return detail;
			}
			return detail;
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject registerWx(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String openId = JsonUtil.getJStringAndCheck(params, "openId", null, true, detail);
		String phone1 = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);
		String password = JsonUtil.getJStringAndCheck(params, "passWord", null, true, detail);
		String valiCode = JsonUtil.getJStringAndCheck(params, "code", null, true, detail);
		String version = JsonUtil.getJStringAndCheck(params, "version", null, true, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail);
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone1);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		String phone = phone1.replace(" ", "");
		AppLogin appLogin = ifmLoginMapper.selectByPhone(phone);
		if(appLogin == null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("phone",phone);
			map.put("status", type);
			AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map);
			if(appPhoneValicode != null){
				Date date = new Date();
				long nowTime = date.getTime();  //获取系统时间
				long creatTime = appPhoneValicode.getCreatTime().getTime();  //验证码时间
				if(nowTime - creatTime <= 180000){
					String code = appPhoneValicode.getValicode();
					if(valiCode.equals(code)){
						AppLogin appLogin02 = new AppLogin();
						appLogin02.setPassword(password);
						appLogin02.setUserCode(phone);
						ifmLoginMapper.insertSelective(appLogin02);
						AppUser user = new AppUser();
						user.setPhone(phone);
						user.setUserName(phone);
						user.setStatus("2");
						user.setRoleFilter("2");
						user.setJobNum(version);
						user.setLgnId(appLogin02.getLgnid() + "");
						user.setMch_version(StringUtil.MD5(phone + UUID.generate().toString().replace("-", "") + System.currentTimeMillis()));
						appUserMapper.insertSelective(user);
						AppLoanAppl appLoanAppl = new AppLoanAppl();
						appLoanAppl.setItemCode(phone);
						appLoanAppl.setStatus("0");
						//appLoanAppl.setCity(version); //渠道来源
						appLoanAppl.setUserId(user.getUserid());
						appLoanApplMapper.insertSelective(appLoanAppl);
						AppLoanCtm appLoanCtm = new AppLoanCtm();
						appLoanCtm.setApprId(appLoanAppl.getId());
						appLoanCtm.setSchedule_status("0");
						appLoanCtmMapper.insertSelective(appLoanCtm);
						
						List<AppWxBind> AppWxBind=appWxBindMapper.selectByPhone(phone);
						AppLoanAppl appLoanAppll=appLoanApplMapper.selectByitemCode(phone);
						AppWxSession wxs=appWxSessionMapper.selectBywxUserId(appLoanAppll.getId());
						if(null==wxs){
							AppWxSession ws=new AppWxSession();
							ws.setLgTime(new Date());
							ws.setSession(openId);
							ws.setWxApprid(appLoanAppll.getId());
							appWxSessionMapper.insertSelective(ws);
						}else{
							wxs.setLgTime(new Date());
							wxs.setSession(openId);
							appWxSessionMapper.updateByPrimaryKeySelective(wxs);
						}
						if(AppWxBind.size()<1){
							AppWxBind wx=new AppWxBind();
							wx.setApprId(appLoanAppll.getId());
							String uuid=UUID.generate();
							wx.setUuid(uuid);
							wx.setCreatTime(new Date());
							wx.setOpenId(openId);
							wx.setPhone(appLoanAppll.getItemCode());
							wx.setBindTime(new Date());
							appWxBindMapper.insertSelective(wx);
					}else{
						AppWxBind bind=AppWxBind.get(0);
						bind.setOpenId(openId);
						bind.setBindTime(new Date());
						appWxBindMapper.updateByPrimaryKeySelective(bind);
					}												
						AppOprLog appOprLog = new AppOprLog();
						appOprLog.setModuleId("0");
						appOprLog.setOprContent("注册成功！");
						appOprLog.setUserid(StringUtil.toString(user.getUserid()));
						appOprLogMapper.insertSelective(appOprLog);
						detail.put(Consts.RESULT_NOTE, "注册成功！");
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码不正确");
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "当前短信验证码已失效，请重新获取");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "手机号与验证码不配对");
			}			
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该手机号已注册，可直接登录");
		}				
		return detail;
	}

	/* (non-Javadoc)
	 * @see com.wxService.WxEntranceIntf#wxShare(com.alibaba.fastjson.JSONObject)
	 * 微信二次分享
	 */
	@Override
	public JSONObject wxShare(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String url = JsonUtil.getJStringAndCheck(params, "url", null, true, detail);
	    List<Map<String,Object>> list= ifmBaseDictMapper.selectBaseDict("MCH_WXSHARE_API_URL"); 
		JSONObject json = new JSONObject();
		json.put("url", url);
		String result = APIHttpClient.doPost(StringUtil.nvl(list.get(0).get("ITEM_VALUE")), json.toJSONString());
		JSONObject res =(JSONObject) JSON.parse(result);
		if(null == res){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			detail.put("timestamp",res.getString("timestamp"));
			detail.put("nonceStr", res.getString("nonceStr"));
			detail.put("signature", res.getString("signature"));
			detail.put("appId", res.getString("appId"));	
		}
		return detail;
	}
}
