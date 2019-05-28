package com.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPContractTempleteMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppInsuranceMapper;
import com.dao.AppLevelMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoginMapper;
import com.dao.AppMessageMapper;
import com.dao.AppMessageStatusMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPContractTemplete;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppInsurance;
import com.model.AppLevel;
import com.model.AppLoanAppl;
import com.model.AppLogin;
import com.model.AppMessage;
import com.model.AppMessageStatus;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.OrderDetailAndMessageService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class OrderDetailAndMessageImpl implements OrderDetailAndMessageService {
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;
	@Autowired
	private AppMessageStatusMapper appMessageStatusMapper;
	@Autowired
	private APPContractTempleteMapper appTem;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLevelMapper appLevelMapper;
	@Autowired
	private AppInsuranceMapper appInsuranceMapper;
	/**
	 * 查询订单详情(订单消息)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject queryOrderDetail(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String withDrawId = JsonUtil.getJStringAndCheck(params, "withDrawId", null, false, detail);
		String mesId = JsonUtil.getJStringAndCheck(params, "mesId", null, false, detail);
		String isJump = JsonUtil.getJStringAndCheck(params, "isJump", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}

		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			if(null != appLoanAppl){
				List<APPWithDrawAppl> appWithDrawAppls = aPPWithDrawApplMapper.selectAllByapprId(appLoanAppl.getId());
				if(null == appWithDrawAppls){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您还没有订单，借款后才能查看");
					return detail;
				}else{
					Integer withId = 0;
					for(APPWithDrawAppl appWithDrawAppl : appWithDrawAppls){
						if(appWithDrawAppl.getId() == Integer.parseInt(withDrawId)){
							withId = appWithDrawAppl.getId();
							break;
						}
					}
					if(0 == withId){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "该订单不属于您，请重试");
						return detail;
					}
					APPWithDrawAppl drawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withDrawId));
					if("3".equals(drawAppl.getStatus()) && "3".equals(drawAppl.getLoanStatus())){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "该订单已结清");
						return detail;
					}
					// 合同
					List<APPContractTemplete> appContractTempletes = appTem.selectTmpUrl(drawAppl.getContractTempid());
					String contractUrl = "";
					String contractUrl2 = "";
					if (null != appContractTempletes && appContractTempletes.size() > 0) {
						for (APPContractTemplete appContractTemplete : appContractTempletes) {
							if (appContractTemplete.getContractType().equals("1")) {
								contractUrl = appContractTemplete.getUrl();
							}
							if(appContractTemplete.getContractType().equals("3")){
								contractUrl2 = appContractTemplete.getUrl();
							}
						}
					}
					if("1".equals(drawAppl.getLoanSource())){
						detail.put("contractUrl", contractUrl);
					}else{
						detail.put("contractUrl", contractUrl2);
					}
					IfmBaseDict ifmBaseDict = new IfmBaseDict();
					ifmBaseDict.setDataType("PURPOSE");
					ifmBaseDict.setItemKey(drawAppl.getPurpose());
					ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
					if(ifmBaseDict == null){
						detail.put("purposeName", "");
					}else{
						detail.put("purposeName", ifmBaseDict.getItemValue()); //借款用途
					}
//					List<AppInsurance> appInsurancesList = appInsuranceMapper.selectAppInsuranceByType(drawAppl.getInsurance_type());
					AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(appLoanAppl.getLevel()));
					String splitShouldPayAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(Double.parseDouble(drawAppl.getActualAmt())*(1+Double.parseDouble(appLevel.getMchRate())*Double.parseDouble(drawAppl.getBorrowDays()))), 2);
					String insuranceShouldPayAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(Double.parseDouble(drawAppl.getCommissionAmt())*(1+Double.parseDouble(appLevel.getMchRate())*Double.parseDouble(drawAppl.getBorrowDays()))), 2);
					detail.put("orderNum", drawAppl.getContract_no());
					detail.put("borrowAmt", StringUtil.formatNumberToDecimals(drawAppl.getBorrowAmt(), 0)+"元");
					detail.put("borrowTime", DateUtil.format(drawAppl.getBorrowTime(), "yyyy/MM/dd"));
					detail.put("actualAmt", StringUtil.formatNumberToDecimals(drawAppl.getActualAmt(),0)+"元"); //到账金额
					detail.put("commissionAmt", StringUtil.formatNumberToDecimals(drawAppl.getCommissionAmt(),0)+"元");  //保险金额
					detail.put("splitShouldPayAmt", splitShouldPayAmt+"元"); //拆分后应还金额(部分)
					detail.put("insuranceShouldPayAmt", insuranceShouldPayAmt+"元"); //保险应还金额
//					if(appInsurancesList.size() > 0){
//						detail.put("insurancesName", appInsurancesList.get(0).getInsurance_name());  //保险名称
//					}else{
//						detail.put("insurancesName", "");
//					}
					if("".equals(StringUtil.nvl(drawAppl.getCoupon_amt()))){
						detail.put("couponAmt", "0.00");
					}else{
						detail.put("couponAmt", drawAppl.getCoupon_amt()); //优惠卷金额
					}
					Map<String,Object> map = new HashMap<String,Object>();
					List<IfmBaseDict> ifmBaseDictsList = new ArrayList<IfmBaseDict>();
					map.put("dataType", "MCH_PROTOCOL");
					if("".equals(StringUtil.nvl(drawAppl.getProtocol_type()))){
						map.put("itemKey", "(1,2)");
						ifmBaseDictsList = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
					}else{
						map.put("itemKey", "("+drawAppl.getProtocol_type()+")");
						ifmBaseDictsList = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
					}
					JSONArray protocolList = new JSONArray();
					for (IfmBaseDict ifmBaseDict3 : ifmBaseDictsList) {
						JSONObject protocol = new JSONObject();
						protocol.put("protocolCode", ifmBaseDict3.getItemKey());
						protocol.put("protocolName", ifmBaseDict3.getItemValue());
						protocolList.add(protocol);
					}
					detail.put("protocolList", protocolList); //协议
					APPPayPlan plan = new APPPayPlan();
					plan.setWithdrawId(Integer.parseInt(withDrawId));
					plan.setStatus("0");
					List<APPPayPlan> payPlans = appPayPlanMapper.selectBywithDrawId(plan);
					String curperiod = ""; //期数
					String repayTime = ""; //还款时间
					String shouldPayAmt = ""; //还款金额
					String curperiodStatus = ""; //订单分期状态  0：按天数，1：分期业务
					String monthPay = "0.00";
					String fxje = "0.00";
					String reducAmt = "0.00";
					String repayId = "";
					if(null != payPlans && payPlans.size() > 0){
						repayTime = DateUtil.format(payPlans.get(0).getRepayDate(), "yyyy/MM/dd");
						BigDecimal repayAmt = new BigDecimal("0.00");
						if(null != payPlans.get(0).getMonthPay()){
							monthPay = payPlans.get(0).getMonthPay() + "";
						}
						if(null != payPlans.get(0).getFxje()){
							fxje = payPlans.get(0).getFxje() + "";
						}
						if(null != payPlans.get(0).getReducAmt()){
							reducAmt = payPlans.get(0).getReducAmt() + "";
						}
						repayId = payPlans.get(0).getId() + "";
						repayAmt = repayAmt.add(new BigDecimal(monthPay)).add(new BigDecimal(fxje).subtract(new BigDecimal(reducAmt)));
						shouldPayAmt = StringUtil.formatNumberToDecimals(repayAmt.toString(), 2);
						if(null != drawAppl.getBorrowPerions() && !"".equals(drawAppl.getBorrowPerions())){ //分期业务
							String minCurperiod = StringUtil.nvl(payPlans.get(0).getCurperiods());
							String maxCurperiod = drawAppl.getBorrowPerions();
							curperiod = minCurperiod + "-" + maxCurperiod + "期";
							curperiodStatus = "1";
						}else{ //按天数
							curperiod = drawAppl.getBorrowDays() + "天";
							curperiodStatus = "0";
						}
					}
					if (isJump.equals("1")) { //0：未读，1：已读
						Map<String, Object> map1 = new HashMap<>();
						map1.put("apprId", StringUtil.nvl(drawAppl.getApprId()));
						map1.put("mesId", StringUtil.nvl(mesId));
						AppMessageStatus messageStatus = appMessageStatusMapper.selectIsRead(map1);
						if (null == messageStatus) {
							AppMessageStatus appMessageStatus = new AppMessageStatus();
							appMessageStatus.setApprId(drawAppl.getApprId());
							appMessageStatus.setMessageId(Integer.parseInt(mesId));
							appMessageStatus.setType("2");
							appMessageStatusMapper.insertSelective(appMessageStatus);
						}
					}
					String status0 = drawAppl.getStatus();
					String loanStatus0 = drawAppl.getLoanStatus();
					String orderStatus = "";
					// 等待放款
					if ("1".equals(loanStatus0) || "2".equals(loanStatus0) || "8".equals(loanStatus0)) {
						orderStatus = "1";
					} else {
						if ("0".equals(loanStatus0)) { // 审核中
							orderStatus = "0";
						} else if ("3".equals(loanStatus0) && "1".equals(status0)) { // 已放款（待还款）
							orderStatus = "2";
						} else if ("2".equals(status0) && "3".equals(loanStatus0)) { // 逾期
							orderStatus = "4";
						} else if ("3".equals(status0) && "3".equals(loanStatus0)) { // 结清
							orderStatus = "3";
						} else if ("4".equals(loanStatus0)) { // 拒绝
							orderStatus = "5";
						} 
					}
					//获取消费订单信息
					detail = consume(detail,drawAppl);
					
					detail.put("loanStatus", orderStatus);
					detail.put("curperiod", curperiod);
					detail.put("repayTime", repayTime);
					detail.put("shouldPayAmt", shouldPayAmt);
					detail.put("curperiodStatus", curperiodStatus);
					detail.put("repayId", repayId);
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

	@Override
	public JSONObject queryMessaheCentre(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (user != null) {
			AppLogin login = appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
			int t0 = 0;
			int t1 = 0;
			int t2 = 0;
			List<Integer> mesIds0 = new ArrayList<>();
			List<Integer> mesIds1 = new ArrayList<>();
			List<Integer> mesIds2 = new ArrayList<>();
			long ptime0 = 0;
			long ptime1 = 0;
			long ptime2 = 0;
			Map<String, Object> map0 = new HashMap<>();
			Map<String, Object> map1 = new HashMap<>();
			Map<String, Object> map2 = new HashMap<>();
			Map<String, Object> p0 = new HashMap<>();
			p0.put("apprId", appLoanAppl.getId());
			p0.put("type", "0");
			List<AppMessage> m0 = appMessageMapper.selectByapprIdAndType(p0);
			if (null != m0 && m0.size() > 0) {
				for (AppMessage appMessage : m0) {
					mesIds0.add(appMessage.getId());
					t0 = t0 + 1;
					if (appMessage.getPublishTime().getTime() > ptime0) {
						ptime0 = appMessage.getPublishTime().getTime();
					}
				}
			}
			List<AppMessage> m1 = appMessageMapper.selectByType("0");
			if (null != m1 && m1.size() > 0) {
				for (AppMessage appMessage : m1) {
					mesIds0.add(appMessage.getId());
					t0 = t0 + 1;
					if (appMessage.getPublishTime().getTime() > ptime0) {
						ptime0 = appMessage.getPublishTime().getTime();
					}
				}
			}
			List<AppMessage> m2 = appMessageMapper.selectByType("1");
			if (null != m2 && m2.size() > 0) {
				for (AppMessage appMessage : m2) {
					mesIds1.add(appMessage.getId());
					t1 = t1 + 1;
					if (appMessage.getPublishTime().getTime() > ptime1) {
						ptime1 = appMessage.getPublishTime().getTime();
					}
				}
			}
			Map<String, Object> p3 = new HashMap<>();
			p3.put("apprId", appLoanAppl.getId());
			p3.put("type", "2");
			List<AppMessage> m3 = appMessageMapper.selectByapprIdAndType(p3);
			if (null != m3 && m3.size() > 0) {
				for (AppMessage appMessage : m3) {
					mesIds2.add(appMessage.getId());
					t2 = t2 + 1;
					if (appMessage.getPublishTime().getTime() > ptime2) {
						ptime2 = appMessage.getPublishTime().getTime();
					}
				}
			}
			JSONArray detaList = new JSONArray();
			JSONObject detail0 = new JSONObject();
			JSONObject detail1 = new JSONObject();
			JSONObject detail2 = new JSONObject();
			if (mesIds0.size() > 0 && null != mesIds0) {
				map0.put("ids", mesIds0);
				map0.put("apprId", appLoanAppl.getId());
				List<AppMessageStatus> ms0 = appMessageStatusMapper.selectByMessageId(map0);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				detail0.put("type", "0");
				detail0.put("time", format.format(ptime0));
				detail0.put("size", StringUtil.toString(t0 - ms0.size()));
			} else {
				detail0.put("type", "0");
				detail0.put("time", "暂无系统消息");
				detail0.put("size", "0");
			}
			if (mesIds1.size() > 0 && null != mesIds1) {
				map1.put("ids", mesIds1);
				map1.put("apprId", appLoanAppl.getId());
				List<AppMessageStatus> ms1 = appMessageStatusMapper.selectByMessageId(map1);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				detail1.put("type", "1");
				detail1.put("time", format.format(ptime1));
				detail1.put("size", StringUtil.toString(t1 - ms1.size()));
			} else {
				detail1.put("type", "1");
				detail1.put("time", "暂无活动消息");
				detail1.put("size", "0");
			}
			if (mesIds2.size() > 0 && null != mesIds2) {
				map2.put("ids", mesIds2);
				map2.put("apprId", appLoanAppl.getId());
				List<AppMessageStatus> ms2 = appMessageStatusMapper.selectByMessageId(map2);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				detail2.put("type", "2");
				detail2.put("time", format.format(ptime2));
				detail2.put("size", StringUtil.toString(t2 - ms2.size()));
			} else {
				detail2.put("type", "2");
				detail2.put("time", "暂无订单消息");
				detail2.put("size", "0");
			}
			detaList.add(detail0);
			detaList.add(detail1);
			detaList.add(detail2);
			detail.put("detaList", detaList);

		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "查询失败");
		}
		return detail;
	}

	/**
	 * 查询一种消息
	 */
	@Override
	public JSONObject queryOneTypeMessage(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);
		String mesType = JsonUtil.getJStringAndCheck(params, "mesType", null, false, detail);
		String userLogin = "";
		String apprId = "";
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null != mch){
			userLogin = "1"; //已登录
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			apprId = appLoanAppl.getId() + "";
		}else{
			userLogin = "0"; //未登录
		}
		JSONArray dataList = new JSONArray();
		int messCount = 0;
		if ("0".equals(mesType)) { //系统消息
			List<Map<String, Object>> m0 = null;
			Map<String, Object> map = new HashMap<>();
			if("1".equals(userLogin)){
				map.put("apprId", apprId);
				map.put("type", "0");
				m0 = appMessageMapper.selectByapprIdAndTypeIsRead2(map);
			}else{
				map.put("phoneId", phoneId);
				map.put("type", "0");
				m0 = appMessageMapper.selectByapprIdAndTypeIsRead3(map);
			}
			if (null != m0 && m0.size() > 0) {
				JSONObject jo = null;
				for (Map<String, Object> appMessage : m0) {
					jo = new JSONObject();
					jo.put("publishTime",DateUtil.format(DateUtil.parseDate(StringUtil.toString(appMessage.get("publish_time"))),"yyyy-MM-dd HH:mm"));
					jo.put("title", StringUtil.toString(appMessage.get("title")));
					jo.put("mesId", StringUtil.toString(appMessage.get("id")));
					jo.put("comtent", StringUtil.toString(appMessage.get("comtent")));
					if ("0".equals(StringUtil.toString(appMessage.get("isRead")))) {
						jo.put("isRead", "0"); //未读
						messCount++;
					} else {
						jo.put("isRead", "1"); //已读
					}
					jo.put("loanStatus", "");
					dataList.add(jo);
				}
			}
		}
		if ("2".equals(mesType)) { //个人消息
			List<Map<String, Object>> m2 = null;
			Map<String, Object> map2 = new HashMap<>();
			if("1".equals(userLogin)){
				map2.put("apprId", apprId);
				map2.put("type", "('2','3','4','5','6','7','8')");
				m2 = appMessageMapper.selectByapprIdAndTypeIsRead4(map2);
			}
			if (null != m2 && m2.size() > 0) {
				APPWithDrawAppl drawAppl = null;
				for (Map<String, Object> appMessage : m2) {
					JSONObject jo = new JSONObject();
					jo.put("publishTime",DateUtil.format(DateUtil.parseDate(StringUtil.toString(appMessage.get("publish_time"))),"yyyy-MM-dd HH:mm"));
					jo.put("title", StringUtil.toString(appMessage.get("title")));
					jo.put("mesId", StringUtil.toString(appMessage.get("id")));
					jo.put("comtent", StringUtil.toString(appMessage.get("comtent")));
					jo.put("withDrawId", StringUtil.toString(appMessage.get("withDraw_id")));
					if ("0".equals(StringUtil.toString(appMessage.get("isRead")))) {
						jo.put("isRead", "0");
						messCount++;
					} else {
						jo.put("isRead", "1");
					}
					if(!"".equals(StringUtil.nvl(appMessage.get("withDraw_id")))){
						drawAppl = aPPWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(StringUtil.toString(appMessage.get("withDraw_id"))));
						jo.put("loanStatus", drawAppl.getStatus());
					}else{
						jo.put("loanStatus", "");
					}
					jo.put("messageType", StringUtil.toString(appMessage.get("message_type")));
					dataList.add(jo);
				}
			}
		}
		detail.put("dataList", dataList);
		detail.put("messageCount", messCount + "");
		return detail;
	}

	/**
	 * 查看消息详情
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject queryOneMessageByMesId(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String mesId = JsonUtil.getJStringAndCheck(params, "mesId", null, false, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);
		String isLogin = "";
		String apprId = "";
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			isLogin = "1"; //已登录
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			apprId = appLoanAppl.getId() + "";
		}else{
			isLogin = "0"; //未登录
		}
		AppMessage appMessage = null;
		if(!"".equals(mesId)){
			appMessage = appMessageMapper.selectByPrimaryKey(Integer.parseInt(mesId));
		}
		if(null == appMessage){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "消息不存在，请重试");
			return detail;
		}
		if ("0".equals(appMessage.getMessageType())) {
			detail.put("title", appMessage.getTitle());
			detail.put("comtent", appMessage.getComtent());
			detail.put("publishTime",StringUtil.toString(DateUtil.format(appMessage.getPublishTime(), "yyyy-MM-dd HH:mm")));
		} else if ("1".equals(appMessage.getMessageType())) {
			detail.put("title", "[" + appMessage.getTitle() + "]");
			detail.put("comtent", appMessage.getComtent());
			detail.put("picUrl", appMessage.getPicUrl() + "");
			detail.put("h5url", appMessage.getH5Url() + "");
			detail.put("publishTime",StringUtil.toString(DateUtil.format(appMessage.getPublishTime(), "yyyy-MM-dd HH:mm")));
		}
		Map<String, Object> map = new HashMap<>();
		if("1".equals(isLogin)){
			map.put("apprId", apprId);
			map.put("mesId", StringUtil.nvl(appMessage.getId()));
		}else{
			map.put("phoneId", phoneId);
			map.put("mesId", StringUtil.nvl(appMessage.getId()));
		}
		AppMessageStatus status = appMessageStatusMapper.selectIsRead(map);
		if (null == status) {
			AppMessageStatus appMessageStatus = new AppMessageStatus();
			if("1".equals(isLogin)){
				appMessageStatus.setApprId(Integer.parseInt(apprId));
			}else{
				appMessageStatus.setPhoneId(phoneId);
			}
			appMessageStatus.setMessageId(Integer.parseInt(mesId));
			appMessageStatus.setType(appMessage.getMessageType());
			appMessageStatusMapper.insertSelective(appMessageStatus);
		}
		return detail;
	}
	
	/**
	 * 查询消息和公告
	 */
	@Override
	public JSONObject queryMessageAndAnnouncement(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);

		// 个人消息
		JSONArray jsonArray0 = new JSONArray();
		JSONObject object0 = new JSONObject();
		object0.put("userId", userId);
		object0.put("phoneId", phoneId);
		object0.put("mesType", "2");
		JSONObject jsonObject0 = queryOneTypeMessage(object0);
		jsonArray0 = jsonObject0.getJSONArray("dataList");
		String messageCount0 = StringUtil.nvl(jsonObject0.get("messageCount"));
		detail.put("orderMessLists", jsonArray0);
		detail.put("orderMessCounts", messageCount0);
		// 系统公告消息
		JSONArray jsonArray1 = new JSONArray();
		JSONObject object1 = new JSONObject();
		object1.put("userId", userId);
		object1.put("phoneId", phoneId);
		object1.put("mesType", "0");
		JSONObject jsonObject1 = queryOneTypeMessage(object1);
		jsonArray1 = jsonObject1.getJSONArray("dataList");
		String messageCount1 = StringUtil.nvl(jsonObject1.get("messageCount"));
		detail.put("AnnounceMessLists", jsonArray1);
		detail.put("AnnounceMessCounts", messageCount1);
		
		return detail;
	}
	
	//标记已读消息
	@Override
	public JSONObject signMessageIsRead(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String mesId = JsonUtil.getJStringAndCheck(params, "mesId", null, true, detail);
		String messType = JsonUtil.getJStringAndCheck(params, "messType", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		if("3".equals(messType) || "4".equals(messType) || "5".equals(messType) || "2".equals(messType) || "1".equals(messType) || "6".equals(messType)){
			AppUser mch = appUserMapper.selectByMchVersion(userId);
			if(null != mch){
				AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("apprId", loanAppl.getId());
				map.put("mesId", mesId);
				AppMessage message = appMessageMapper.selectByapprIdAndMessId(map);
				if(null != message){
					AppMessageStatus messageStatus = appMessageStatusMapper.selectIsRead(map);
					if(null == messageStatus){
						AppMessageStatus appMessageStatus = new AppMessageStatus();
						appMessageStatus.setApprId(loanAppl.getId());
						appMessageStatus.setMessageId(Integer.parseInt(mesId));
						appMessageStatus.setType(messType);
						appMessageStatusMapper.insertSelective(appMessageStatus);
					}
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "该消息不属于您");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "消息类型错误");
		}
		return detail;
	}
	
	/**
	 * @param detail
	 * @param appWithDrawAppl
	 * @return
	 * 消费订单信息
	 */
	public JSONObject consume(JSONObject detail,APPWithDrawAppl appWithDrawAppl){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dataType", "MCH_WITHDRAW_THEPAGETEXT");
		map.put("itemKey", "("+appWithDrawAppl.getConsumeType()+")");
		List<IfmBaseDict> thePageTestList = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
		List<AppInsurance> appInsurancesList = appInsuranceMapper.selectAppInsuranceByType(appWithDrawAppl.getInsurance_type());
		if(!"".equals(appWithDrawAppl.getConsumeType())){
			if(thePageTestList.size() > 0){
				if("1".equals(appWithDrawAppl.getConsumeType())){ 
					detail.put("thePageText1", thePageTestList.get(0).getItemValue());
					detail.put("thePageText2", thePageTestList.get(1).getItemValue());
					detail.put("thePageText3", thePageTestList.get(2).getItemValue());
					detail.put("thePageText4", thePageTestList.get(3).getItemValue());
					detail.put("thePageText5", thePageTestList.get(4).getItemValue());
					detail.put("thePageText6", appInsurancesList.get(0).getInsurance_name());
				}else{
					detail.put("thePageText1", thePageTestList.get(0).getItemValue());
					detail.put("thePageText2", thePageTestList.get(1).getItemValue());
					detail.put("thePageText3", thePageTestList.get(2).getItemValue());
					detail.put("thePageText4", thePageTestList.get(3).getItemValue());
					detail.put("thePageText5", thePageTestList.get(4).getItemValue());
					detail.put("thePageText6", thePageTestList.get(5).getItemValue());
				}
			}else{
				detail.put("thePageText1", "");
				detail.put("thePageText2", "");
				detail.put("thePageText3", "");
				detail.put("thePageText4", "");
				detail.put("thePageText5", "");
				detail.put("thePageText6", "");
			}
		}else if(!"".equals(appWithDrawAppl.getInsurance_type())){
			Map<String,Object> map2 = new HashMap<String,Object>();
			map2.put("dataType", "MCH_WITHDRAW_THEPAGETEXT");
			map2.put("itemKey", "1");
			List<IfmBaseDict> thePageTestList2 = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
			detail.put("thePageText1", thePageTestList2.get(0).getItemValue());
			detail.put("thePageText2", thePageTestList2.get(1).getItemValue());
			detail.put("thePageText3", thePageTestList2.get(2).getItemValue());
			detail.put("thePageText4", thePageTestList2.get(3).getItemValue());
			detail.put("thePageText5", thePageTestList2.get(4).getItemValue());
			detail.put("thePageText6", appInsurancesList.get(0).getInsurance_name());
		}else{
			detail.put("thePageText1", "");
			detail.put("thePageText2", "");
			detail.put("thePageText3", "");
			detail.put("thePageText4", "");
			detail.put("thePageText5", "");
			detail.put("thePageText6", "");
		}
		detail.put("consumeNumber", "BH"+appWithDrawAppl.getContract_no());
		return detail;
	}

}
