package com.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bestsign.config.BestSignConfig;
import com.creditx.xbehavior.Application;
import com.creditx.xbehavior.BasicInfo;
import com.creditx.xbehavior.Contact;
import com.creditx.xbehavior.CreditxConstants;
import com.creditx.xbehavior.RiskInvoker;
import com.creditx.xbehavior.RiskRequest;
import com.creditx.xbehavior.RiskResponse;
import com.dao.APPContractTempleteMapper;
import com.dao.APPCreditMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppAccountInfoMapper;
import com.dao.AppCouponMapper;
import com.dao.AppInsuranceMapper;
import com.dao.AppLevelMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppLoginMapper;
import com.dao.AppMessageMapper;
import com.dao.AppMorphoDeviceInfoMapper;
import com.dao.AppSSQContractMapper;
import com.dao.AppUserCouponMapper;
import com.dao.AppUserMapper;
import com.dao.AppWhiteKnightInfoMapper;
import com.dao.AppWithDrawLogMapper;
import com.dao.AppWxBindMapper;
import com.dao.AppWxTempletMapper;
import com.dao.AppXiaoanCheckcardRecordMapper;
import com.dao.AppZhimaScoreMapper;
import com.dao.IfmBaseDictMapper;
import com.dao.InsertDataMapper;
import com.frame.Consts;
import com.frame.RedisService;
import com.model.APPContractTemplete;
import com.model.APPCredit;
import com.model.APPWithDrawAppl;
import com.model.AppAccountInfo;
import com.model.AppCoupon;
import com.model.AppInsurance;
import com.model.AppLevel;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmShip;
import com.model.AppLogin;
import com.model.AppMessage;
import com.model.AppMorphoDeviceInfo;
import com.model.AppSSQContract;
import com.model.AppUser;
import com.model.AppUserCoupon;
import com.model.AppWhiteKnightInfo;
import com.model.AppWithDrawLog;
import com.model.AppWxBind;
import com.model.AppWxTemplet;
import com.model.IfmBaseDict;
import com.service.intf.AppBestSignService;
import com.service.intf.AppGoSumbitService;
import com.service.intf.LoanApplyService;
import com.util.APIHttpClient;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.GetInfoFromIDUtil;
import com.util.JsonUtil;
import com.util.SendOrderPaySuccessMsg;
import com.util.StringUtil;
import com.util.TemplateData;
import com.util.ThreadPoolUtil;
import com.util.XiaoAnCheckCardUtil;

@Service
public class LoanApplyImpl implements LoanApplyService {
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private APPCreditMapper aPPCreditMapper;
	@Autowired
	private APPContractTempleteMapper appTem;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppWithDrawLogMapper appWithDrawLogMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;
	@Autowired
	private InsertDataMapper insertDataMapper;
	@Autowired
	private AppGoSumbitService appGoSumbitService;
	@Autowired
	private AppUserCouponMapper appUserCouponMapper;
	@Autowired
	private AppCouponMapper appCouponMapper;
	@Autowired
	private AppLevelMapper appLevelMapper;
	@Autowired
	private AppXiaoanCheckcardRecordMapper appXiaoanCheckcardRecordMapper;
	@Autowired
	private RedisService redisService;
	@Autowired
	private AppWxTempletMapper appWxTempletMapper;
	@Autowired
	private AppWxBindMapper appWxBindMapper;
	@Autowired
	private AppMorphoDeviceInfoMapper appMorphoDeviceInfoMapper;
	@Autowired
	private AppZhimaScoreMapper appZhimaScoreMapper;
	@Autowired
	private AppInsuranceMapper appInsuranceMapper;
	@Autowired
	private AppBestSignService appBestSignService;//上上签业务
	@Autowired
	private AppSSQContractMapper appSSQContractMapper;
	@Autowired
	AppWhiteKnightInfoMapper appWhiteKnightInfoMapper;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private AppAccountInfoMapper appAccountInfoMapper;
	/**
	 *  点击我要借款查询借款金额、天数和优惠券
	 */
	@Override
	public JSONObject queryAmtAndDaysAndCoupons(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String insuranceType = JsonUtil.getJStringAndCheck(params, "insuranceType", null, false, detail);  //保险类型
		JSONArray couponIdLists = params.getJSONArray("couponIds");
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null == mch) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid() + "";
		AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		if (null != loanAppl) {
			
//			AppZhimaScore appZhimaScore=appZhimaScoreMapper.selectZhimaScoreByUserId(Integer.parseInt(userId));
//			//获取芝麻信用授权时间
//			Date saveDate=appZhimaScore.getAuticDate();
//			//获取系统当前时间
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(saveDate);
//			int days1 = cal.get(Calendar.DATE);
//	        Date sysDate=new Date();
//	        Date lastDate1=DateUtil.getLastDay(sysDate);
//	        	//如果芝麻信用授权时间小于当月6号24点之前，且系统时间大于此时间则重新进行授权
//			if((days1<7 && sysDate.getDay()>=7)||(sysDate.getDay()<7&&saveDate.getTime()<lastDate1.getTime())||"1".equals(appZhimaScore.getStatus())){
////				loanAppl.setStatus("8");
//				loanAppl.setZhimaStatus("1");
//				appZhimaScore.setStatus("1");
//				appZhimaScoreMapper.updateByPrimaryKeySelective(appZhimaScore);
//				appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
//				detail.put("openidStatus", "0");
//				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
//				detail.put(Consts.RESULT_NOTE, "请重新进行芝麻授权");
//				return detail;
//			}else{
//				detail.put("openidStatus", "1");
//			}
			detail.put("openidStatus", "1");
			// 必须先填写基本资料
			if("".equals(StringUtil.nvl(mch.getOrgId()))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请先填写基本资料");
				return detail;
			}
			//学生借款功能暂时关闭
			if("0".equals(StringUtil.nvl(mch.getOrgId()))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "暂不提供该功能");
				return detail;
			}
			// 有审核中的订单不能发起借款
			List<APPWithDrawAppl> withDrawAppls = aPPWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
			if(null != withDrawAppls && withDrawAppls.size() > 0){
				int num = 0;
				for(APPWithDrawAppl withDrawAppl : withDrawAppls){
					if("0".equals(withDrawAppl.getStatus())){
						num++;
						break;
					}
				}
				if(num > 0){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您有订单正在审核中，暂不能发起借款");
					return detail;
				}
			}
			// 授信等级额度
			AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(loanAppl.getLevel()));
			if(null == appLevel){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您当前需要授信，请完善资料授信！");
				return detail;
			}
			String mchRate = appLevel.getMchRate();
			String cutRate = appLevel.getFwfRate();
			// 查看用户可用的优惠券
			Map<String, Object> map0 = new HashMap<String, Object>();
			map0.put("apprId", loanAppl.getId());
			map0.put("status", "0");
			List<AppUserCoupon> userCoupons = appUserCouponMapper.selectEffectiveByApprIdAndStatus(map0);
			// 优惠券
			String couponNum = "0";
			String preferentialAmt = "0.00"; //优惠的金额
			String couponExistStatus = "1";
			if (null == couponIdLists || couponIdLists.size() <= 0) { // 未选择优惠券
				if (null != userCoupons) {
					if(userCoupons.size() == 0){
						couponNum = "无";
						couponExistStatus = "0";
					}else{
						couponNum = userCoupons.size() + "张可用";
					}
				}
			} else { // 已选择优惠券
				if (null == userCoupons || userCoupons.size() <= 0) {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您暂无可用优惠券");
					return detail;
				}
				// 只能选择一张优惠券
				if (couponIdLists.size() > 1) {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "只能选择一张优惠券");
					return detail;
				}
				//校验优惠券的有效性
				int usefulCouponId = 0;
				int choosonCouponId = 0;
				int count = 0;
				JSONObject jo1 = null;
				AppUserCoupon appUserCoupon = null;
				for (AppUserCoupon userCoupon : userCoupons) {
//					usefulCouponId = userCoupon.getCouponId();
					usefulCouponId = userCoupon.getId();
					for (Object couponIdList : couponIdLists) {
						jo1 = (JSONObject) couponIdList;
						choosonCouponId = Integer.parseInt(StringUtil.nvl(jo1.get("couponId")));
						appUserCoupon = appUserCouponMapper.selectByPrimaryKey(choosonCouponId);
						if(null != appUserCoupon){
							if(appUserCoupon.getApprId().intValue() != loanAppl.getId().intValue()){
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "只能使用自己的优惠券");
								return detail;
							}
						}
						if (usefulCouponId == choosonCouponId) {
							count++;
						}
					}
				}
				if (count != couponIdLists.size()) {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "不能选择无效的优惠券");
					return detail;
				}
				// 返回选择的优惠券信息
				String userCouponId = StringUtil.nvl(((JSONObject) couponIdLists.get(0)).get("couponId"));
				String couponType = StringUtil.nvl(((JSONObject) couponIdLists.get(0)).get("couponType"));
				AppUserCoupon userCoupon = appUserCouponMapper.selectByPrimaryKey(Integer.parseInt(userCouponId));
				AppCoupon coupon = appCouponMapper.selectByPrimaryKey(userCoupon.getCouponId());
				if (null != coupon) {
					if ("0".equals(coupon.getType())) {
						couponNum = "-￥" + coupon.getUses();
						preferentialAmt = coupon.getUses();
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请选择有效的优惠券");
					return detail;
				}
			}
			// 最小借款金额
			int minBorrowAmt = 100;
			List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("MCH_MIN_BORROW_AMT");
			if(null != maps && maps.size() > 0){
				minBorrowAmt = (int) Double.parseDouble(StringUtil.nvl(maps.get(0).get("ITEM_VALUE")));
			}
			// 最大借款金额
			List<APPWithDrawAppl> applWithDrawApplList = aPPWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
			//重新计算可用额度
			double auditAmt = 0.00;
			if(applWithDrawApplList.size() > 0){
				for(APPWithDrawAppl appWithDrawAppl : applWithDrawApplList){
					if(!("3").equals(appWithDrawAppl.getLoanStatus()) && !("4").equals(appWithDrawAppl.getLoanStatus())){
						auditAmt = auditAmt + Double.parseDouble(appWithDrawAppl.getBorrowAmt());
					}
				}
			}
			APPCredit appCredit = aPPCreditMapper.selectByApprId(loanAppl.getId());
//			double useAmt = appCredit.getUseAmt();
			double useAmt = appCredit.getCreditAmt() - auditAmt - appCredit.getWait_pay_amt();		
			List<String> amtLists = new ArrayList<String>();
			int maxBorrowAmt = ((int) useAmt) / 100 * 100;
			if (maxBorrowAmt < minBorrowAmt) {
				amtLists.add(minBorrowAmt + "");
			} else {
				for (int i = minBorrowAmt; i <= maxBorrowAmt; i += 100) {
					amtLists.add(i + "");
				}
				if(((int) useAmt)%100!=0){
					amtLists.add((int)(useAmt/100 * 100) + "");
				}
			}
			detail.put("borrowAmtLists", amtLists);
			// 查询借款天数
			List<String> daysLists = new ArrayList<String>();
			String days = appLevel.getStatus();
			if(!"".equals(days)){
				String[] day = days.split("\\|");
				if(null != day && day.length > 0){
					for(int i = 0; i < day.length; i++){
						daysLists.add(day[i]);
					}
				}
			}
			// 合同
			List<APPContractTemplete> appContractTempletes = appTem.selectTmpUrl("0");
			String contractUrl = "";
			if (null != appContractTempletes && appContractTempletes.size() > 0) {
				for (APPContractTemplete appContractTemplete : appContractTempletes) {
					if (appContractTemplete.getContractType().equals("1")) {
						contractUrl = appContractTemplete.getUrl();
					}
				}
			}
			// 借款用途
			List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("PURPOSE");
			JSONArray purpose = new JSONArray();
			for (Map<String, Object> map : list) {
				JSONObject purposeList = new JSONObject();
				purposeList.put("purposeCode", map.get("ITEM_KEY"));
				purposeList.put("purposeName", map.get("ITEM_VALUE"));
				purpose.add(purposeList);
			}
			//保险
			List<AppInsurance> appInsurancesList = new ArrayList<AppInsurance>();
			if("".equals(insuranceType)){
				appInsurancesList = appInsuranceMapper.selectAppInsurance();
			}else{
				appInsurancesList = appInsuranceMapper.selectAppInsuranceByType(insuranceType);
			}
			//协议
			List<Map<String,Object>> ifmBaseDictList = ifmBaseDictMapper.selectBaseDict("MCH_PROTOCOL_ALLOCATION");
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("dataType", "MCH_PROTOCOL");
			map.put("itemKey", "("+ifmBaseDictList.get(0).get("ITEM_VALUE")+")");
			List<IfmBaseDict> ifmBaseDictsList = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
			JSONArray protocolList = new JSONArray();
			for (IfmBaseDict ifmBaseDict : ifmBaseDictsList) {
				JSONObject protocol = new JSONObject();
				protocol.put("protocolCode", ifmBaseDict.getItemKey());
				protocol.put("protocolName", ifmBaseDict.getItemValue());
				protocolList.add(protocol);
			}
			//保险跳转url
			List<Map<String,Object>> mchInsuranceUrl = ifmBaseDictMapper.selectBaseDict("MCH_INSURANCE_URL");
			// 从redis中获取合同号
			String curDate = DateUtil.format(new Date(), "yyyyMMdd");
			String lastDate = DateUtil.format(DateUtil.addDays(new Date(), -1), "yyyyMMdd");
			String contractNo = getOrderNoFromRedis(curDate, lastDate);
			// 消费订单数据
			detail = consume(detail,appInsurancesList.get(0).getInsurance_name(),contractNo);
			
			detail.put("contractNo", contractNo);
			detail.put("contractUrl", contractUrl);
			detail.put("borrowdaysLists", daysLists);
			detail.put("mchRate", mchRate);
			detail.put("cutRate", cutRate);
			detail.put("couponNum", couponNum);
			detail.put("preferentialAmt", preferentialAmt);
			detail.put("couponExistStatus", couponExistStatus);
			detail.put("purpose", purpose);
			detail.put("insuranceType", appInsurancesList.get(0).getType()); //保险类型
			detail.put("insuranceName", appInsurancesList.get(0).getInsurance_name());  //保险名称
			detail.put("protocolList", protocolList);  //协议列表
			detail.put("mchInsuranceUrl", mchInsuranceUrl.get(0).get("ITEM_VALUE"));
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
		}
		return detail;
	}


	/**
	 * 立即借款
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject IneedMoney(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String loanAmt = JsonUtil.getJStringAndCheck(params, "loanAmt", null, false, detail);
		String loanPerion = JsonUtil.getJStringAndCheck(params, "loanPerion", null, false, detail);
		String purposeCode = JsonUtil.getJStringAndCheck(params, "purposeCode", null, true, detail);
		String idfa = JsonUtil.getJStringAndCheck(params, "idfa", null, false, detail);
		String imei = JsonUtil.getJStringAndCheck(params, "imei", null, false, detail);
		String ip = JsonUtil.getJStringAndCheck(params, "ip", null, false, detail);
		String longitude = JsonUtil.getJStringAndCheck(params, "longitude", null, false, detail);
		String latitude = JsonUtil.getJStringAndCheck(params, "latitude", null, false, detail);
		String contraNo = JsonUtil.getJStringAndCheck(params, "contractNo", null, true, detail);
		String projectUrl = JsonUtil.getJStringAndCheck(params, "projectUrl", null, false, detail);
		JSONArray couponIdLists = params.getJSONArray("couponIds");
		String insuranceType = JsonUtil.getJStringAndCheck(params, "insuranceType", null, true, detail);  //保险类型
		JSONArray protocolList = params.getJSONArray("protocolList");   //协议 
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);	
		String consumeType = JsonUtil.getJStringAndCheck(params, "consumeType", null, false, detail);  //消费类型	
		
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		if(Integer.parseInt(loanAmt)%100 != 0){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "必须整百借款");
			return detail;
		}
		if(!"7".equals(loanPerion)&&!"14".equals(loanPerion)&&!"28".equals(loanPerion)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "借款天数不在选择范围内");
			return detail;
		}
		userId = mch.getUserid() + "";
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		String protocolType = "";
		if(protocolList.size() > 0){
			for(int i = 0;i<protocolList.size();i++){
				if(new Integer(protocolList.size()-1).equals(i)){
					protocolType =protocolType + protocolList.getJSONObject(i).get("protocolCode");
				}else{
					protocolType =protocolType + protocolList.getJSONObject(i).get("protocolCode")+",";
				}
			}
		}
		if (user != null) {
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			if(null == appLoanAppl){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
				return detail;
			}
			if(!"".equals(type)){
				String address = JsonUtil.getJStringAndCheck(params, "address", null, false, detail);
				String phoneModel = JsonUtil.getJStringAndCheck(params, "phoneModel", null, false, detail);
				String verCode = JsonUtil.getJStringAndCheck(params, "verCode", null, false, detail);
				JSONArray appList = params.getJSONArray("appList");
				AppWhiteKnightInfo appWhiteKnightInfo = appWhiteKnightInfoMapper.selectByApprId(appLoanAppl.getId());
				if(null != appWhiteKnightInfo){
					appWhiteKnightInfo.setAddress(address);
					appWhiteKnightInfo.setPhone_model(phoneModel);
					appWhiteKnightInfo.setVer_code(verCode);
					appWhiteKnightInfo.setApp_list(appList.toJSONString());
					appWhiteKnightInfoMapper.updateByApprIdSelective(appWhiteKnightInfo);
				}
			}
			// 必须先填写基本资料
			if("".equals(StringUtil.nvl(mch.getOrgId()))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请先填写基本资料");
				return detail;
			}
			//学生借款功能暂时关闭
			if("0".equals(user.getOrgId())){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "暂不提供该功能");
				return detail;
			}
			if(!"".equals(StringUtil.nvl(appLoanAppl.getAccountStatus()))){
				if(2 == appLoanAppl.getAccountStatus() || 0 == appLoanAppl.getAccountStatus()){ //账户异常
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "很抱歉，你的账户存在异常，赶快联系微信公众号随心花吧！她会帮你解决哒！");
					return detail;
				}else if(3 == appLoanAppl.getAccountStatus()){ //账户关闭
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
					return detail;
				}else if(1 == appLoanAppl.getAccountStatus()){ //正常账户
					// 授信等级额度
					AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(appLoanAppl.getLevel()));
					if(null == appLevel){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您当前需要授信，请完善资料授信！");
						return detail;
					}
					// 有审核中的订单不能发起借款
					List<APPWithDrawAppl> withDrawAppls = aPPWithDrawApplMapper.selectAllByapprId(appLoanAppl.getId());
					if(null != withDrawAppls && withDrawAppls.size() > 0){
						int num1 = 0;
						int num2 = 0;
						double auditAmt = 0.00;
						for(APPWithDrawAppl withDrawAppl : withDrawAppls){
							if("0".equals(withDrawAppl.getStatus())){
								num1++;
							}
						}
						for(APPWithDrawAppl withDrawAppl : withDrawAppls){
							if("2".equals(withDrawAppl.getStatus()) && "3".equals(withDrawAppl.getLoanStatus())){
								num2++;
							}
							if(!("3").equals(withDrawAppl.getLoanStatus()) && !("4").equals(withDrawAppl.getLoanStatus())){
								auditAmt = auditAmt + Double.parseDouble(withDrawAppl.getBorrowAmt());
							}
						}
						if(num1 > 0){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "您有订单正在审核中，不能发起借款");
							return detail;
						}
						if(num2 > 0){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "您的借款申请失败，有逾期订单未还款，请还款后再次借款");
							return detail;
						}
						APPCredit appCredit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
						double useAmt = appCredit.getCreditAmt() - auditAmt - appCredit.getWait_pay_amt();
						if(useAmt < Double.parseDouble(loanAmt)){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "贷款金额超过上限");
							return detail;
						}
					}
					// 最小借款金额
					int minBorrowAmt = 100;
					List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("MCH_MIN_BORROW_AMT");
					if(null != maps && maps.size() > 0){
						minBorrowAmt = (int) Double.parseDouble(StringUtil.nvl(maps.get(0).get("ITEM_VALUE")));
					}
					if(Integer.parseInt(loanAmt) < minBorrowAmt){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "借款金额不能少于" + minBorrowAmt + "元");
						return detail;
					}
					
					// 满足借款条件才能借款
					AppLoanCtm ctm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
					String schduleStatus = ctm.getSchedule_status();
					String status = appLoanAppl.getStatus();
					String zhimaStatus = StringUtil.nvl(appLoanAppl.getZhimaStatus());
					String baseInfoStatus = StringUtil.nvl(appLoanAppl.getBaseInfoStatus());
					String contactInfoStatus = StringUtil.nvl(appLoanAppl.getContactInfoStatus());
					if("2".equals(status) && "8".equals(schduleStatus) && !"1".equals(zhimaStatus) && !"1".equals(baseInfoStatus) && !"1".equals(contactInfoStatus)){
						APPCredit aPPCredit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
						Double useAmt = aPPCredit.getUseAmt();
						int compareResult = Double.compare(useAmt, Double.parseDouble(loanAmt));
						if (compareResult >= 0) { //可用金额大于借款金额
							String orgId = StringUtil.nvl(mch.getOrgId());
							String stuProvince = "";
							String workProvince = "";
							if ("0".equals(orgId)) {
								stuProvince = null == ctm.getProvince() ? "" : ctm.getProvince().substring(0, 2);
								if("".equals(stuProvince)){
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "学习地点未填写，请重新填写");
									return detail;
								}
							} else {
								workProvince = null == ctm.getCommProvince() ? "" : ctm.getCommProvince().substring(0, 2);
								if("".equals(workProvince)){
									detail.put(Consts.RESULT, ErrorCode.FAILED);
									detail.put(Consts.RESULT_NOTE, "工作地点未填写，请重新填写");
									return detail;
								}
							}
							// 地区年龄校验
							String identityNo = null == ctm.getIdentityCard() ? "" : ctm.getIdentityCard();
							String bithYear = GetInfoFromIDUtil.getYear(identityNo);
							if("身份证格式有误".equals(bithYear)){
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "身份证格式有误，请联系客服");
								return detail;
							}
							Map<String, Object> map0 = new HashMap<>();
							map0.put("dataType", "MCH_CUSTOM_FILTER");
							map0.put("perion", "0");
							IfmBaseDict baseDict0 = ifmBaseDictMapper.selectfWFAndRate(map0);
							String stuFiltProvince = null == baseDict0.getItemValue() ? "" : baseDict0.getItemValue();
							int stuYear = ("".equals(StringUtil.nvl(baseDict0.getXh()))) ? 0 : Integer.parseInt(baseDict0.getXh());
							String[] stuFiltProvinces = stuFiltProvince.split("\\|");
							boolean flag0 = Arrays.asList(stuFiltProvinces).contains(stuProvince);
							Map<String, Object> map1 = new HashMap<>();
							map1.put("dataType", "MCH_CUSTOM_FILTER");
							map1.put("perion", "1");
							IfmBaseDict baseDict1 = ifmBaseDictMapper.selectfWFAndRate(map1);
							String workFiltProvince = null == baseDict1.getItemValue() ? "" : baseDict1.getItemValue();
							int workYear = ("".equals(StringUtil.nvl(baseDict1.getXh()))) ? 0 : Integer.parseInt(baseDict1.getXh());
							String[] workFiltProvinces = workFiltProvince.split("\\|");
							boolean flag1 = Arrays.asList(workFiltProvinces).contains(workProvince);
							// 计算服务费和月供
	//						AppLevel appLevel = appLevelMapper.selectByLevel(appLoanAppl.getLevel());
							String mchRate = appLevel.getMchRate();
							String cutRate = appLevel.getFwfRate();
							//服务费
							String fwf = StringUtil.formatNumberToDecimals(new BigDecimal(loanAmt).multiply(new BigDecimal(cutRate)).toString(), 2);
							//到手金额
							String actualAmt = StringUtil.formatNumberToDecimals(new BigDecimal(loanAmt).subtract(new BigDecimal(fwf)).toString(), 2);
							//月供
							String monthPay = StringUtil.formatNumberToDecimals(new BigDecimal(loanAmt).multiply(new BigDecimal(mchRate).multiply(new BigDecimal(loanPerion))).add(new BigDecimal(loanAmt)).toString(), 2);
							// 从redis中获取合同号
//							String curDate = DateUtil.format(new Date(), "yyyyMMdd");
//							String lastDate = DateUtil.format(DateUtil.addDays(new Date(), -1), "yyyyMMdd");
//							String no = getOrderNoFromRedis(curDate, lastDate);
							// 合同
							String contractTempId = "0";
							List<APPContractTemplete> temp = appTem.selectTem();
							if(null != temp && temp.size() > 0){
								for(APPContractTemplete tem : temp){
									if("1".equals(tem.getContractType())){
										contractTempId = tem.getTmpid();
										break;
									}
								}
							}
							//阈值关闭，校验地区，年龄
							// 成人:1982年前（包含1982年）出生、**等禁止准入
							// 学生：**等禁止准入
							if (!"0".equals(ctm.getIs_pass())
									&& ((("0".equals(orgId)) && (flag0 || Integer.parseInt(bithYear) <= stuYear))
											|| (("1".equals(orgId)) && (flag1 || Integer.parseInt(bithYear) <= workYear)))){
								APPWithDrawAppl aPPWithDrawAppl = new APPWithDrawAppl();
								aPPWithDrawAppl.setApprId(appLoanAppl.getId());
								aPPWithDrawAppl.setBorrowAmt(loanAmt);
								aPPWithDrawAppl.setCommissionAmt(fwf);
								aPPWithDrawAppl.setActualAmt(actualAmt);
								aPPWithDrawAppl.setBorrowDays(loanPerion);
								aPPWithDrawAppl.setStatus("4");
								aPPWithDrawAppl.setLoanStatus("4");
								aPPWithDrawAppl.setMonth_pay(monthPay);
								aPPWithDrawAppl.setCapital_and_interest(monthPay);
								aPPWithDrawAppl.setContractTempid(contractTempId);
								aPPWithDrawAppl.setContract_no(contraNo);
								aPPWithDrawAppl.setNhl(mchRate);
								aPPWithDrawAppl.setFwfRate(cutRate);
								aPPWithDrawAppl.setPurpose(purposeCode);
								aPPWithDrawAppl.setLoanSource("1"); //攸县
								aPPWithDrawAppl.setInsurance_type(insuranceType); //保险类型
								aPPWithDrawAppl.setProtocol_type(protocolType); //协议
								aPPWithDrawAppl.setDayRate(mchRate); //记录日利率
								aPPWithDrawAppl.setConsumeType(consumeType);
								aPPWithDrawApplMapper.insertSelective(aPPWithDrawAppl);
								// 复制从表
								Map<String, Object> mao = new HashMap<>();
								mao.put("withDrawId", aPPWithDrawAppl.getId());
								mao.put("apprId", appLoanAppl.getId());
								mao.put("userId", userId);
								copyDataByWithId(mao);
								AppWithDrawLog drawLog = new AppWithDrawLog();
								drawLog.setNodeContent("订单提交");
								drawLog.setDetail("等待受理审核");
								drawLog.setWithdrawId(aPPWithDrawAppl.getId());
								appWithDrawLogMapper.insertSelective(drawLog);
								AppWithDrawLog drawLog1 = new AppWithDrawLog();
								drawLog1.setNodeContent("订单处理中");
								drawLog1.setDetail("订单正在处理,请您耐心等待");
								drawLog1.setWithdrawId(aPPWithDrawAppl.getId());
								appWithDrawLogMapper.insertSelective(drawLog1);
								AppWithDrawLog drawLog2 = new AppWithDrawLog();
								drawLog2.setNodeContent("订单审核不通过");
								drawLog2.setDetail("您暂时不符合我们的授信要求，请30天后再尝试申请。");
								drawLog2.setWithdrawId(aPPWithDrawAppl.getId());
								appWithDrawLogMapper.insertSelective(drawLog2);
								AppMessage appMessage = new AppMessage();
								appMessage.setApprId(appLoanAppl.getId());
								appMessage.setTitle("提交订单消息");
								appMessage.setComtent("您的" + loanAmt + "元借款申请已提交成功，2个工作日内随心花将完成审核，请耐心等待。更多详情可关注随心花微信公众号咨询，有问必答喔。");
								appMessage.setMessageType("2");
								appMessage.setWithdrawId(aPPWithDrawAppl.getId());
								appMessageMapper.insertSelective(appMessage);
								appMessage = new AppMessage();
								appMessage.setApprId(appLoanAppl.getId());
								appMessage.setTitle("审核拒绝消息");
								appMessage.setComtent("亲爱的用户，您提交的" + loanAmt + "元借款申请失败，由于资质暂时不符合借款条件，请保持良好信用记录，再来加入随心花吧。等你喔。");
								appMessage.setMessageType("2");
								appMessage.setWithdrawId(aPPWithDrawAppl.getId());
								appMessageMapper.insertSelective(appMessage);
								appLoanAppl.setStatus("3");
								appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
								detail.put("withDrawId", aPPWithDrawAppl.getId() + "");
								
								//借款订单提交失败微信提醒
								final JSONObject json = new JSONObject();
								json.put("contractNo", aPPWithDrawAppl.getContract_no());
								json.put("appLoanApplId", appLoanAppl.getId());
								json.put("loanAmt", loanAmt);								
								//loanOrderFail(json);
								/*new Thread(new Runnable() {
			                        public void run() {
			                        	loanOrderFail(json);
			                        }
			                    }).start();*/
								ThreadPoolUtil.exec(new Runnable() {
						            @Override
						            public void run() {
						            	loanOrderFail(json);
						            }
						        });
												
								//保存闪蝶设备信息
 								saveAppMorphoDevice(appLoanAppl.getId(),aPPWithDrawAppl.getId(),idfa, imei, ip, longitude, latitude);
							
								
							}else{ //正常借款
								//小安验卡
								XiaoAnCheckCardUtil.checkCard(ctm, ifmBaseDictMapper, appXiaoanCheckcardRecordMapper);
								// 计算优惠券
								String couponAmt = "0.00";
								String couponId = "";
								if(null != couponIdLists && couponIdLists.size() > 0){
									// 查看用户可用的优惠券
									Map<String, Object> m = new HashMap<String, Object>();
									m.put("apprId", appLoanAppl.getId());
									m.put("status", "0");
									List<AppUserCoupon> userCoupons = appUserCouponMapper.selectEffectiveByApprIdAndStatus(m);
									int usefulUserCouponId = 0;
									int choosonCouponId = 0;
									int count = 0;
									JSONObject jo1 = null;
									AppUserCoupon coupon0 = null;
									if(null != userCoupons && userCoupons.size() > 0){
										for (AppUserCoupon userCoupon : userCoupons) {
											usefulUserCouponId = userCoupon.getId();
											for (Object couponIdList : couponIdLists) {
												jo1 = (JSONObject) couponIdList;
												choosonCouponId = Integer.parseInt(StringUtil.nvl(jo1.get("couponId")));
												coupon0 = appUserCouponMapper.selectByPrimaryKey(choosonCouponId);
												if(null != coupon0){
													if(coupon0.getApprId().intValue() != appLoanAppl.getId().intValue()){
														detail.put(Consts.RESULT, ErrorCode.FAILED);
														detail.put(Consts.RESULT_NOTE, "只能使用自己的优惠券");
														return detail;
													}
												}
												if (usefulUserCouponId == choosonCouponId) {
													count++;
												}
											}
										}
										if (count != couponIdLists.size()) {
											detail.put(Consts.RESULT, ErrorCode.FAILED);
											detail.put(Consts.RESULT_NOTE, "请选择有效的优惠券");
											return detail;
										}
									}
									couponId = StringUtil.nvl(((JSONObject) couponIdLists.get(0)).get("couponId"));
									AppUserCoupon userCoupon = appUserCouponMapper.selectByPrimaryKey(Integer.parseInt(couponId));
									AppCoupon coupon = appCouponMapper.selectByPrimaryKey(userCoupon.getCouponId());
									couponAmt = coupon.getUses();
								}
								APPWithDrawAppl aPPWithDrawAppl = new APPWithDrawAppl();
								aPPWithDrawAppl.setApprId(appLoanAppl.getId());
								aPPWithDrawAppl.setBorrowAmt(loanAmt);
								aPPWithDrawAppl.setCommissionAmt(fwf);
								aPPWithDrawAppl.setActualAmt(actualAmt);
								aPPWithDrawAppl.setBorrowDays(loanPerion);
								aPPWithDrawAppl.setStatus("0");
								aPPWithDrawAppl.setLoanStatus("0");
								aPPWithDrawAppl.setMonth_pay(new BigDecimal(monthPay).subtract(new BigDecimal(couponAmt)).toString());
								aPPWithDrawAppl.setCapital_and_interest(monthPay);
								aPPWithDrawAppl.setCoupon_amt(couponAmt);
								aPPWithDrawAppl.setContractTempid(contractTempId);
								aPPWithDrawAppl.setContract_no(contraNo);
								aPPWithDrawAppl.setNhl(mchRate);
								aPPWithDrawAppl.setCouponId(couponId);
								aPPWithDrawAppl.setFwfRate(cutRate);
								aPPWithDrawAppl.setPurpose(purposeCode);
								aPPWithDrawAppl.setLoanSource("1"); //攸县
								aPPWithDrawAppl.setInsurance_type(insuranceType);  //保险类型
								aPPWithDrawAppl.setProtocol_type(protocolType); //协议
								aPPWithDrawAppl.setDayRate(mchRate); //记录日利率
								aPPWithDrawAppl.setConsumeType(consumeType);
								aPPWithDrawApplMapper.insertSelective(aPPWithDrawAppl);
								// 优惠券标记为已使用
								if(null != couponIdLists && couponIdLists.size() > 0){
									int choosonUserCouponId = 0;
									JSONObject jo2 = null;
									AppUserCoupon userCoupon =null;
									for (Object couponIdList : couponIdLists) {
										jo2 = (JSONObject) couponIdList;
										choosonUserCouponId = Integer.parseInt(StringUtil.nvl(jo2.get("couponId")));
	//									Map<String, Object> map = new HashMap<String, Object>();
	//									map.put("apprId", appLoanAppl.getId());
	//									map.put("couponId", choosonCouponId);
	//									userCoupon = appUserCouponMapper.selectByApprIdAndCouponId(map);
										userCoupon = appUserCouponMapper.selectByPrimaryKey(choosonUserCouponId);
										userCoupon.setStatus("1");
										appUserCouponMapper.updateByPrimaryKeySelective(userCoupon);
									}
								}
								aPPCredit.setUseAmt(new BigDecimal(useAmt).subtract(new BigDecimal(loanAmt)).doubleValue());
								aPPCreditMapper.updateByPrimaryKeySelective(aPPCredit);
								
								// 静默签署借款合同和代扣协议
								/*JSONObject signResult = signBorrowAndWithholdQuietly(ctm, appLoanAppl, appLevel, aPPWithDrawAppl, loanAmt, loanPerion, monthPay, purposeCode, contraNo, projectUrl);
								if(null != signResult && "1".equals(StringUtil.nvl(signResult.get("result")))){
									return signResult;
								}*/
								
								// 生成风控订单
								JSONObject jo = new JSONObject();
								jo.put("userId", mch.getMch_version());
								jo.put("withDrawId", StringUtil.toString(aPPWithDrawAppl.getId()));
								jo.put("engineType", "1");
								JSONObject jo2 = appGoSumbitService.sumbitUserInfoToDecisionEngine(jo);
								if("-1".equals(StringUtil.nvl(jo2.get("result")))){
									return jo2;
								}
								// 复制从表
								Map<String, Object> mao = new HashMap<>();
								mao.put("withDrawId", aPPWithDrawAppl.getId());
								mao.put("apprId", appLoanAppl.getId());
								mao.put("userId", userId);
								copyDataByWithId(mao);
								AppWithDrawLog drawLog = new AppWithDrawLog();
								drawLog.setNodeContent("订单提交");
								drawLog.setDetail("等待受理审核");
								drawLog.setWithdrawId(aPPWithDrawAppl.getId());
								appWithDrawLogMapper.insertSelective(drawLog);
								AppWithDrawLog drawLog1 = new AppWithDrawLog();
								drawLog1.setNodeContent("订单处理中");
								drawLog1.setDetail("订单正在处理,请您耐心等待");
								drawLog1.setWithdrawId(aPPWithDrawAppl.getId());
								appWithDrawLogMapper.insertSelective(drawLog1);
								AppMessage appMessage = new AppMessage();
								appMessage.setApprId(appLoanAppl.getId());
								appMessage.setTitle("提交订单消息");
								appMessage.setComtent("您的" + loanAmt + "元借款申请已提交成功，2个工作日内随心花将完成审核，请耐心等待。更多详情可关注随心花微信公众号咨询，有问必答喔。");
								appMessage.setMessageType("2");
								appMessage.setWithdrawId(aPPWithDrawAppl.getId());
								appMessageMapper.insertSelective(appMessage);
								appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
								
								//借款订单提交成功微信提醒
								final JSONObject json = new JSONObject();
								json.put("contractNo", aPPWithDrawAppl.getContract_no());
								json.put("appLoanApplId", appLoanAppl.getId());
								json.put("loanAmt", loanAmt);								
								//loanOrderSuccess(json);								
								 /*new Thread(new Runnable() {
				                        public void run() {
				                        	loanOrderSuccess(json);
				                        }
				                    }).start();*/
//								ThreadPoolUtil.exec(new Runnable() {
//						            @Override
//						            public void run() {
////						            	loanOrderSuccess(json);
//						            }
//						        });								
 								detail.put("withDrawId", aPPWithDrawAppl.getId() + "");
 								
 								//保存闪蝶设备信息
 								saveAppMorphoDevice(appLoanAppl.getId(),aPPWithDrawAppl.getId(),idfa, imei, ip, longitude, latitude);
 								//氪信
// 								saveCreditxInfo(idfa, imei, ctm, appLoanAppl, aPPWithDrawAppl, user.getMch_version());
							}
							
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "贷款金额超过上限");
						}
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "资料未认证完成，请完善资料");
					}
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "当前账户未授信，请先授信");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}


	private void saveAppMorphoDevice(Integer apprId,Integer withdrawId,String idfa, String imei, String ip, String longitude, String latitude) {
		AppMorphoDeviceInfo apd =new AppMorphoDeviceInfo();
		apd.setApprId(apprId);
		apd.setWithdrawId(withdrawId);
		apd.setIdfa(idfa);
		apd.setImei(imei);
		apd.setIp(ip);
		apd.setLatitude(latitude);
		apd.setLongitude(longitude);
		appMorphoDeviceInfoMapper.insertSelective(apd);
	}
			
	
	/**
	 * 复制到order表
	 */
	private void copyDataByWithId(Map<String, Object> map){
		insertDataMapper.insertAppOrderAppl(map);
//		insertDataMapper.insertAppOrderAttch(map);
//		insertDataMapper.insertAppOrderBook(map);
//		insertDataMapper.insertAppOrderCtmCnt(map);
//		insertDataMapper.insertAppOrderCtmship(map);
		insertDataMapper.insertAppOrderCustom(map);
//		insertDataMapper.insertAppOrderZhimaScore(map);
	}
	
	/**
	 * 从redis中获取莫愁花订单号
	 * @param curDate 当前日期
	 * @param lastDate 昨天日期
	 * @return 莫愁花合同号
	 */
	private synchronized String getOrderNoFromRedis(String curDate, String lastDate){
		String redisResult = StringUtil.nvl(redisService.get("SXH" + curDate));
		String lastDateResult = StringUtil.nvl(redisService.get("SXH" + lastDate));
		//删除昨日数据
		if(!"".equals(lastDateResult)){
			redisService.del("SXH" + lastDate);
		}
		int num = 0;
		if(!"".equals(redisResult)){
			num = Integer.parseInt(redisResult) + 1;
			redisService.set("SXH" + curDate, num + "");
		}else{
			redisService.set("SXH" + curDate, "1000");
			num = 1000;
		}
		String no = StringUtil.getSeri2(curDate, num);
		return no;
	}

	@Override
	public JSONObject QureyUrlAndMonthpay(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String loanAmt = JsonUtil.getJStringAndCheck(params, "loanAmt", null, false, detail);
		String loanPerion = JsonUtil.getJStringAndCheck(params, "loanPerion", null, false, detail);
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
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLogin login = appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
		if (appLoanAppl.getStatus().equals("0")) {
			APPCredit aPPCredit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
			Double useAmt = aPPCredit.getUseAmt();
			if (Double.parseDouble(loanAmt) <= useAmt) {
				Map<String, Object> map2 = new HashMap<>();
				map2.put("dataType", "YLX" + "_" + appLoanAppl.getLevel());
				map2.put("perion", loanPerion);
				IfmBaseDict LX = ifmBaseDictMapper.selectfWFAndRate(map2);
				String month_pay = "";
				String lx = StringUtil.toString(LX.getItemValue());
				Double pay = StringUtil.parseDouble(
						(Double.parseDouble(loanAmt) * (1 + Double.parseDouble(lx) * Double.parseDouble(loanPerion)))
								/ Double.parseDouble(loanPerion));
				month_pay = StringUtil.toString(pay);
				List<APPContractTemplete> appContractTempletes = appTem.selectTem();
				for (APPContractTemplete appContractTemplete : appContractTempletes) {
					if (appContractTemplete.getContractType().equals("1")) {
						detail.put("jkurl", appContractTemplete.getUrl() + "");
					}
					if (appContractTemplete.getContractType().equals("0")) {
						detail.put("fwurl", appContractTemplete.getUrl() + "");
					}
					if (appContractTemplete.getContractType().equals("2")) {
						detail.put("jqzmurl", appContractTemplete.getUrl() + "");
					}

				}
				detail.put("monthPay", month_pay);

			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "贷款金额超过上限");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "审核还未通过");

		}
		return detail;
	}


	@Override
	public JSONObject myBill(JSONObject params) {
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
		AppLogin login = appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
		APPCredit appCredit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
		detail.put("useAmt", appCredit.getUseAmt());
		JSONArray detaList = new JSONArray();
		// 正常和逾期
		Map<String, Object> m0 = new HashMap<>();
		m0.put("status", "('1','2')");
		m0.put("apprId", appLoanAppl.getId());
		List<Map<String, Object>> dhk = aPPWithDrawApplMapper.selectByapprIdAndStatus(m0);
		if (dhk != null && dhk.size() > 0) {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", DateUtil
					.format(DateUtil.parseDate(StringUtil.toString(dhk.get(0).get("lastLoanTime"))), "yyyy-MM-dd"));
			jo.put("type", "0");
			jo.put("size", StringUtil.toString(dhk.size()));
			detaList.add(jo);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", "暂无记录");
			jo.put("type", "0");
			jo.put("size", "0");
			detaList.add(jo);
		}
		// 待放款
		Map<String, Object> m1 = new HashMap<>();
		m1.put("loanStatus", "('0','1','2','8')");
		m1.put("apprId", appLoanAppl.getId());
		List<Map<String, Object>> dfk = aPPWithDrawApplMapper.selectByapprIdAndLoanStatus(m1);
		if (dfk != null && dfk.size() > 0) {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", DateUtil
					.format(DateUtil.parseDate(StringUtil.toString(dfk.get(0).get("lastLoanTime"))), "yyyy-MM-dd"));
			jo.put("type", "1");
			jo.put("size", StringUtil.toString(dfk.size()));
			detaList.add(jo);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", "暂无记录");
			jo.put("type", "1");
			jo.put("size", "0");
			detaList.add(jo);
		}
		// 结清
		Map<String, Object> m2 = new HashMap<>();
		m2.put("status", "('3')");
		m2.put("apprId", appLoanAppl.getId());
		List<Map<String, Object>> jq = aPPWithDrawApplMapper.selectByapprIdAndStatus(m2);
		if (jq != null && jq.size() > 0) {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", DateUtil
					.format(DateUtil.parseDate(StringUtil.toString(jq.get(0).get("lastLoanTime"))), "yyyy-MM-dd"));
			jo.put("type", "2");
			jo.put("size", StringUtil.toString(jq.size()));
			detaList.add(jo);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", "暂无记录");
			jo.put("type", "2");
			jo.put("size", "0");
			detaList.add(jo);
		}

		// 驳回
		Map<String, Object> m3 = new HashMap<>();
		m3.put("status", "('4')");
		m3.put("apprId", appLoanAppl.getId());
		List<Map<String, Object>> bh = aPPWithDrawApplMapper.selectByapprIdAndStatus(m3);
		if (bh != null && bh.size() > 0) {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", DateUtil
					.format(DateUtil.parseDate(StringUtil.toString(bh.get(0).get("lastLoanTime"))), "yyyy-MM-dd"));
			jo.put("type", "3");
			jo.put("size", StringUtil.toString(bh.size()));
			detaList.add(jo);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", "暂无记录");
			jo.put("type", "3");
			jo.put("size", "0");
			detaList.add(jo);
		}
		// 待签约
		Map<String, Object> m4 = new HashMap<>();
		m4.put("loanStatus", "('5')");
		m4.put("apprId", appLoanAppl.getId());
		List<Map<String, Object>> dqy = aPPWithDrawApplMapper.selectByapprIdAndLoanStatus(m4);
		if (dqy != null && dqy.size() > 0) {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", DateUtil
					.format(DateUtil.parseDate(StringUtil.toString(dqy.get(0).get("lastLoanTime"))), "yyyy-MM-dd"));
			jo.put("type", "4");
			jo.put("size", StringUtil.toString(dqy.size()));
			detaList.add(jo);
		} else {
			JSONObject jo = new JSONObject();
			jo.put("lastLoanTime", "暂无记录");
			jo.put("type", "4");
			jo.put("size", "0");
			detaList.add(jo);
		}
		detail.put("detaList", detaList);
		return detail;
	}
	
	//一键借款
	@Override
	public JSONObject loanByOneStep(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			if ("0".equals(StringUtil.nvl(mch.getOrgId()))) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "暂不提供该功能");
				return detail;
			}
			if(!"".equals(StringUtil.nvl(loanAppl.getAccountStatus()))){
				if(2 == loanAppl.getAccountStatus() || 0 == loanAppl.getAccountStatus()){ //账户异常
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "很抱歉，你的账户存在异常，赶快联系微信公众号随心花吧！她会帮你解决哒！");
					return detail;
				}else if(3 == loanAppl.getAccountStatus()){ //账户关闭
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
					return detail;
				}else if(1 == loanAppl.getAccountStatus()){ //正常账户
					List<APPWithDrawAppl> withDrawAppls = aPPWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
					if (null != withDrawAppls && withDrawAppls.size() > 0) {
						int overduleOrder = 0;
						int reviewOrder = 0;
						for (APPWithDrawAppl withDrawAppl : withDrawAppls) {
							// 逾期
							if ("2".equals(withDrawAppl.getStatus()) && "3".equals(withDrawAppl.getLoanStatus())) {
								overduleOrder++;
							}
							// 审核中
							if ("0".equals(withDrawAppl.getStatus())) {
								reviewOrder++;
							}
							if (overduleOrder > 0) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "您的借款申请失败，有逾期订单未还款，请还款后再次借款");
								return detail;
							}
							if (reviewOrder > 0) {
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "您有一笔借款正在审批中");
								return detail;
							}
						}
					}
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "当前账户未授信，请先授信");
			}
			List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("MCH_USE_PROTOGENESIS");
			detail.put("mchUseProtogenesis", StringUtil.nvl(list.get(0).get("ITEM_KEY"))); // 首页判断我要借款页面走原生还是h5 0 h5 1 android ios
			detail.put("mchUseProtogenesisUrl", StringUtil.nvl(list.get(0).get("ITEM_VALUE")));  //h5链接
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	@Override
	public JSONObject loanOrderFail(JSONObject json){
		//String tpid="3";
		String tpid="16";
		String loanAmt = json.getString("loanAmt");
		String contractNo = json.getString("contractNo");
		String appLoanApplId = json.getString("appLoanApplId");
		AppWxTemplet appWxTemplet=appWxTempletMapper.qryWxtempletByid(Integer.parseInt(tpid));
		//String [] str=appWxTemplet.getTemplet_coment().split("\\|");
		List<IfmBaseDict> IfmBaseDict2=ifmBaseDictMapper.fetchDictsByType("MCH_WX_MES_TMP");
		IfmBaseDict base=IfmBaseDict2.get(0);
		Map<String,TemplateData> m=new HashMap<>();
		TemplateData first = new TemplateData();
		TemplateData k1 = new TemplateData();
		TemplateData k2 = new TemplateData();
		TemplateData remark = new TemplateData();
		AppWxBind appWxBind=appWxBindMapper.queryAppWxBindbyapprid(Integer.parseInt(appLoanApplId));
		if(null!=appWxBind){
			first.setColor("#000000");
			first.setValue("亲爱的用户，您有一笔借款申请被拒绝。");
			m.put("first", first);
			k1.setColor("#000000");			
			k1.setValue(contractNo);
			m.put("keyword1", k1);
			k2.setColor("#000000");
			k2.setValue("已拒绝");
			m.put("keyword2", k2);
			remark.setColor("#000000");
			remark.setValue("亲爱的用户，您提交的" + loanAmt + "元借款申请失败，由于资质暂时不符合借款条件，请保持良好信用记录，再来加入随心花吧。等你喔。");
			m.put("remark", remark);
			SendOrderPaySuccessMsg.send_template_message(base.getItemKey(), base.getItemValue(), appWxBind.getOpenId(),appWxTemplet.getTemplet_code(), "", m);
			appWxBind.setPushWxsmsTime(new Date());
			appWxBindMapper.updateAppWxBindbyappridSx(appWxBind.getOpenId());
			}
		return null;
	}
	
	@Override
	public JSONObject loanOrderSuccess(JSONObject json){
		//String tpid="3";
		String tpid="16";
		String loanAmt = json.getString("loanAmt");
		String contractNo = json.getString("contractNo");
		String appLoanApplId = json.getString("appLoanApplId");
		AppWxTemplet appWxTemplet=appWxTempletMapper.qryWxtempletByid(Integer.parseInt(tpid));
		String [] str=appWxTemplet.getTemplet_coment().split("\\|");
		List<IfmBaseDict> IfmBaseDict2=ifmBaseDictMapper.fetchDictsByType("MCH_WX_MES_TMP");
		IfmBaseDict base=IfmBaseDict2.get(0);
		Map<String,TemplateData> m=new HashMap<>();
		TemplateData first = new TemplateData();
		TemplateData k1 = new TemplateData();
		TemplateData k2 = new TemplateData();
		TemplateData remark = new TemplateData();
		AppWxBind appWxBind=appWxBindMapper.queryAppWxBindbyapprid(Integer.parseInt(appLoanApplId));
		if(null!=appWxBind){
			first.setColor("#000000");
			first.setValue("亲爱的用户，您提交成功一笔借款申请。");
			m.put("first", first);
			k1.setColor("#000000");			
			k1.setValue(contractNo);
			m.put("keyword1", k1);
			k2.setColor("#000000");
			k2.setValue("审核中");
			m.put("keyword2", k2);
			remark.setColor("#000000");
			remark.setValue("您的" + loanAmt + "元借款申请已提交成功，2个工作日内随心花将完成审核，请耐心等待。更多详情可关注随心花微信公众号咨询，有问必答喔。");
			m.put("remark", remark);
			SendOrderPaySuccessMsg.send_template_message(base.getItemKey(), base.getItemValue(), appWxBind.getOpenId(),appWxTemplet.getTemplet_code(), "", m);
			appWxBind.setPushWxsmsTime(new Date());
			appWxBindMapper.updateAppWxBindbyappridSx(appWxBind.getOpenId());
			}
		return null;
	}
	
	/**
	 * 静默签署借款合同和代扣协议
	 * @param ctm
	 * @param appLoanAppl
	 * @param appLevel
	 * @param appWithDrawAppl
	 * @param loanAmt 借款金额
	 * @param loanPerion 借款天数
	 * @param monthPay 月供
	 * @param purposeCode 借款用途
	 * @param contraNo 合同号
	 * @param projectUrl 项目路径
	 * @return
	 * @throws Exception
	 */
	private JSONObject signBorrowAndWithholdQuietly(AppLoanCtm ctm, AppLoanAppl appLoanAppl, AppLevel appLevel, APPWithDrawAppl appWithDrawAppl, String loanAmt, String loanPerion, String monthPay, String purposeCode, String contraNo, String projectUrl) throws Exception{
		JSONObject detail = new JSONObject();
		if(!"1".equals(StringUtil.nvl(appLoanAppl.getSsqApplyStatus()))){ //未申请成功
			// 上上签用户注册申请
			JSONObject ssqResult = ssqUserApply(appLoanAppl, ctm);
			if(null != ssqResult && "1".equals(StringUtil.nvl(ssqResult.get("result")))){
				return ssqResult;
			}
		}
		// 每天服务费
		String dayPayAmt = StringUtil.formatNumberToDecimals(new BigDecimal(loanAmt).multiply(new BigDecimal(appLevel.getMchRate())).toString(), 2);
		// 砍头息
		String protocalCutRate = StringUtil.nvl(Double.parseDouble(appLevel.getFwfRate()) * 100).substring(0, 2);
		// 获取逾期率
		JSONObject overDuleRate = ServiceProrocolServiceImpl.getOverDuleRate(ifmBaseDictMapper);
		String overdule3DayRate = StringUtil.nvl(overDuleRate.get("overdule3DayRate")); // 三天内逾期利率
		String overduleOver3DayRate = StringUtil.nvl(overDuleRate.get("overduleOver3DayRate")); // 超过三天逾期利率
		// PDF添加元素
		JSONArray borrowElementsArray = AppBestSignServiceImpl.getBorrowPdfElements("", ctm.getIdentityCard(),
				appLoanAppl.getItemCode(), ctm.getBankCard(), loanAmt, monthPay, loanPerion,
				purposeCode, protocalCutRate, dayPayAmt, contraNo, overdule3DayRate, overduleOver3DayRate, "");
		// 借款协议路径
		String borrowContractPath = AppBestSignServiceImpl.getprotocalByType(projectUrl, 0);
		// 莫愁花分期合同编号
		String borrowContractNo = appBestSignService.uploadAndCreatefenQiContract("莫愁花分期服务协议", "莫愁花分期服务协议", "3", "莫愁花分期服务协议合同", borrowContractPath, borrowElementsArray);
		
		// PDF添加元素
		JSONArray withholdElementsArray = AppBestSignServiceImpl.getWithholdPdfElements("", ctm.getIdentityCard(), appLoanAppl.getItemCode(), "");
		// 代扣协议路径
		String withholdContractPath = AppBestSignServiceImpl.getprotocalByType(projectUrl, 1);
		// 代扣授权合同编号
		String withholdContractNo = appBestSignService.uploadAndCreatefenQiContract("代扣授权书", "代扣授权书", "2", "悦才代扣授权书合同", withholdContractPath, withholdElementsArray);
		
		String borrowContractUrl = "";
		String withholdContractUrl = "";
		if(!"".equals(StringUtil.nvl(borrowContractNo)) && !"".equals(StringUtil.nvl(withholdContractNo))){
			borrowContractUrl = StringUtil.nvl(appBestSignService.getScanUrl(borrowContractNo));
			withholdContractUrl = StringUtil.nvl(appBestSignService.getScanUrl(withholdContractNo));
			if("".equals(borrowContractUrl) || "".equals(withholdContractUrl)){
				System.out.println("合同号生成失败，借款人：" + appLoanAppl.getItemCode());
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "借款失败，请重试！");
				return detail;
			}
		}else{
			System.out.println("合同地址生成失败，借款人：" + appLoanAppl.getItemCode());
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "借款失败，请重试！");
			return detail;
		}
		// 借款合同
		saveSsqContract(appLoanAppl, appWithDrawAppl, borrowContractNo, borrowContractUrl, "0");
		// 代扣协议
		saveSsqContract(appLoanAppl, appWithDrawAppl, withholdContractNo, withholdContractUrl, "1");
		return detail;
	}
	
	/**
	 * 上上签用户注册申请
	 * @param appLoanAppl
	 * @param ctm
	 * @return
	 * @throws Exception
	 */
	private JSONObject ssqUserApply(AppLoanAppl appLoanAppl, AppLoanCtm ctm) throws Exception {
		JSONObject detail = new JSONObject();
		// 先注册
		JSONObject registerJson = new JSONObject();
		registerJson.put("account", appLoanAppl.getItemCode());
		registerJson.put("name", ctm.getCustomName());
		registerJson.put("identity", ctm.getIdentityCard());
		String taskId = BestSignConfig.registerUser(registerJson);
		if (null != taskId) {
			// 是否申请成功
			int count = 0;
			int secondCount = 10; // 设置查询时间10秒
			for (int i = 1; i <= secondCount; i++) {
				JSONObject applyJson = new JSONObject();
				applyJson.put("account", appLoanAppl.getItemCode());
				applyJson.put("taskId", taskId);
				String asyncStatus = BestSignConfig.getAsyncStatus(applyJson);
				if (null != asyncStatus) {
					// 1：新申请
					// 2：申请中
					// 3：超时
					// 4：申请失败
					// 5：成功
					// -1：无效的申请（数据库无此值）
					if ("5".equals(asyncStatus)) {
						// 标记
						appLoanAppl.setSsqApplyStatus("1");
						appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
						break;
					} else if ("2".equals(asyncStatus)) {
						count++;
						Thread.sleep(1000);
					} else if ("1".equals(asyncStatus) || "3".equals(asyncStatus) || "4".equals(asyncStatus)
							|| "-1".equals(asyncStatus)) {
						System.out.println(appLoanAppl.getItemCode() + "，上上签申请失败");
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "借款失败，请重试！");
						return detail;
					}
				} else {
					System.out.println(appLoanAppl.getItemCode() + "，上上签申请失败");
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "借款失败，请重试！");
					return detail;
				}
			}
			if (count >= secondCount) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "请求超时，请重试！");
				return detail;
			}
		} else {
			System.out.println(appLoanAppl.getItemCode() + "，上上签注册失败");
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "借款失败，请重试！");
			return detail;
		}
		return detail;
	}
	
	/**
	 * 保存上上签合同信息
	 * @param appLoanAppl
	 * @param appWithDrawAppl
	 * @param contractNo
	 * @param contractUrl
	 * @param type
	 */
	private void saveSsqContract(AppLoanAppl appLoanAppl, APPWithDrawAppl appWithDrawAppl, String contractNo, String contractUrl, String type){
		AppSSQContract ssqContract = appSSQContractMapper.selectByWithdrawIdAndType(appWithDrawAppl.getId(), type);
		if(null != ssqContract){
			ssqContract.setApprId(appLoanAppl.getId());
			ssqContract.setContractId(contractNo);
			ssqContract.setContractUrl(contractUrl);
			ssqContract.setCreateTime(new Date());
			ssqContract.setViewTime(new Date());
			ssqContract.setType(type);
			appSSQContractMapper.updateByPrimaryKeySelective(ssqContract);
		}else{
			ssqContract = new AppSSQContract();
			ssqContract.setApprId(appLoanAppl.getId());
			ssqContract.setContractId(contractNo);
			ssqContract.setContractUrl(contractUrl);
			ssqContract.setCreateTime(new Date());
			ssqContract.setViewTime(new Date());
			ssqContract.setType(type);
			ssqContract.setWithdrawId(appWithDrawAppl.getId());
			appSSQContractMapper.insertSelective(ssqContract);
		}
	}
	
	/**
	 * 获取氪信信息
	 * @param idfa iOS设备号
	 * @param imei 安卓设备号
	 * @param loanCtm
	 * @param loanAppl
	 * @param withDrawAppl
	 * @throws Exception
	 */
	private void saveCreditxInfo(final String idfa, final String imei, final AppLoanCtm loanCtm, final AppLoanAppl loanAppl, final APPWithDrawAppl withDrawAppl, final String mchVersion){
		ThreadPoolUtil.exec(new Runnable() {
			public void run() {
				RiskRequest riskRequest = new RiskRequest();
				RiskInvoker riskInvoker = null;
				try {
					if (!"".equals(StringUtil.nvl(idfa)) && "".equals(StringUtil.nvl(imei))) {
						riskRequest.setAppKey(CreditxConstants.APPKEY_IOS);
						riskRequest.setDeviceId(idfa);
						riskInvoker = new RiskInvoker(idfa, CreditxConstants.ACCESSKEY);
					} else if ("".equals(StringUtil.nvl(idfa)) && !"".equals(StringUtil.nvl(imei))) {
						riskRequest.setAppKey(CreditxConstants.APPKEY_ANDROID);
						riskRequest.setDeviceId(imei);
						riskInvoker = new RiskInvoker(imei, CreditxConstants.ACCESSKEY);
					}
				} catch (UnsupportedEncodingException e) {
					System.out.println("apprId== " + loanAppl.getId() + " ==初始化氪信请求参数错误！");
					e.printStackTrace();
				}
				riskRequest.setUserId(mchVersion);
				// 借款信息
				AppAccountInfo accountInfo = appAccountInfoMapper.selectByApprId(loanAppl.getId());
				Application application = new Application();
				application.setIsNewApplicant("1"); //默认新贷用户
				if(null != accountInfo && null != accountInfo.getSettleCount() && accountInfo.getSettleCount().intValue() > 0){
					application.setIsNewApplicant("2"); //复贷用户
				}
				application.setApplyTime(DateUtil.format(withDrawAppl.getBorrowTime(), DateUtil.DATETIME_FORMAT));
				riskRequest.setApplication(application);
				// 基本信息
				BasicInfo basicInfo = new BasicInfo();
				basicInfo.setCompanyAddress(loanCtm.getCompanyAddress());
				basicInfo.setCompanyCity(loanCtm.getCommCity());
				basicInfo.setCompanyCounty(loanCtm.getCommTown());
				basicInfo.setCompanyProvince(loanCtm.getCommProvince());
				basicInfo.setEducationLevel(loanCtm.getEducational());
				basicInfo.setHomeAddress(loanCtm.getBdrAddr());
				basicInfo.setHomeCity(loanCtm.getCity());
				basicInfo.setHomeCounty(loanCtm.getTown());
				basicInfo.setHomeProvince(loanCtm.getProvince());
				basicInfo.setIdentity(loanCtm.getIdentityCard());
				basicInfo.setMarriageStatus(loanCtm.getIsMarry());
				basicInfo.setMobileMd5(StringUtil.MD5(loanAppl.getItemCode()));
				basicInfo.setName(loanCtm.getCustomName());
				// 紧急联系人
				Contact[] contacts = new Contact[2];
				Contact contact0 = new Contact("", "");
				Contact contact1 = new Contact("", "");
				List<AppLoanCtmShip> ctmShips = appLoanCtmShipMapper.selectByApprId(loanAppl.getId());
				if(null != ctmShips && ctmShips.size() > 0){
					contact0 = new Contact(ctmShips.get(0).getShipName(), ctmShips.get(0).getShipCnt());
					contact1 = new Contact(ctmShips.get(1).getShipName(), ctmShips.get(1).getShipCnt());
				}
				contacts[0] = contact0;
				contacts[1] = contact1;
				basicInfo.setEmergencyContacts(contacts);
				riskRequest.setBasicInfo(basicInfo);
				
				RiskResponse riskResponse;
				try {
					riskResponse = riskInvoker.query(riskRequest);
					int statusCode = riskResponse.getStatusCode();
					if(statusCode == 200){
						JSONObject objectResponse = (JSONObject) JSON.toJSON(riskResponse);
						JSONObject object = new JSONObject();
						object.put("id_card", loanCtm.getIdentityCard());
						object.put("report", objectResponse);
						List<Map<String, Object>> creditxMaps = ifmBaseDictMapper.selectBaseDict("MCH_SAVE_CREDITX_URL");
						String saveCreditxUrl = StringUtil.nvl(creditxMaps.get(0).get("ITEM_VALUE"));
						String requestResult = APIHttpClient.doPost(saveCreditxUrl, object, 60000);
						JSONObject requestObject = JSON.parseObject(requestResult);
						if("false".equals(requestObject.getString("success"))){
							System.out.println("订单 " + withDrawAppl.getId() + " 保存氪信信息失败");
						}
						// 保存氪信分
						JSONObject outputJson = (JSONObject) objectResponse.get("output");
						withDrawAppl.setCreditxScore(outputJson.getString("riskScore"));
						aPPWithDrawApplMapper.updateByPrimaryKeyForCreditxScore(withDrawAppl);
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("apprId== " + loanAppl.getId() + " ==请求氪信服务错误！");
				}
			}
		});
	}
	
	/**
	 * @param detail
	 * @param insuranceName
	 * @param contractNo
	 * @return
	 * 消费订单模板
	 */
	public JSONObject consume(JSONObject detail,String insuranceName,String contractNo){
		List<Map<String,Object>> consumeTypeList = ifmBaseDictMapper.selectBaseDict("MCH_CONSUME_TYPE");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dataType", "MCH_WITHDRAW_THEPAGETEXT");
		map.put("itemKey", "("+consumeTypeList.get(0).get("ITEM_KEY")+")");
		List<IfmBaseDict> thePageTestList = ifmBaseDictMapper.selectByItemKeyAndDataTypeList(map);
		if(thePageTestList.size() > 0){
			if("1".equals(consumeTypeList.get(0).get("ITEM_KEY"))){  
				detail.put("thePageText1", thePageTestList.get(0).getItemValue());
				detail.put("thePageText2", thePageTestList.get(1).getItemValue());
				detail.put("thePageText3", thePageTestList.get(2).getItemValue());
				detail.put("thePageText4", thePageTestList.get(3).getItemValue());
				detail.put("thePageText5", thePageTestList.get(4).getItemValue());
				detail.put("thePageText6", insuranceName);
				detail.put("consumeStatus", "1");
			}else{
				detail.put("thePageText1", thePageTestList.get(0).getItemValue());
				detail.put("thePageText2", thePageTestList.get(1).getItemValue());
				detail.put("thePageText3", thePageTestList.get(2).getItemValue());
				detail.put("thePageText4", thePageTestList.get(3).getItemValue());
				detail.put("thePageText5", thePageTestList.get(4).getItemValue());
				detail.put("thePageText6", thePageTestList.get(5).getItemValue());
				detail.put("consumeStatus", "0");
			}
		}else{
			detail.put("thePageText1", "");
			detail.put("thePageText2", "");
			detail.put("thePageText3", "");
			detail.put("thePageText4", "");
			detail.put("thePageText5", "");
			detail.put("thePageText6", "");
			detail.put("consumeStatus", "0");
		}
		detail.put("consumeNumber", "BH"+contractNo);
		detail.put("consumeType", consumeTypeList.get(0).get("ITEM_KEY"));
		return detail;
	}
	
}
