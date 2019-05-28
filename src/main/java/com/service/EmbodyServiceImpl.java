package com.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppIncreaseAmtInfoMapper;
import com.dao.AppLevelMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppMessageMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPCredit;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppLevel;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppMessage;
import com.model.AppUser;
import com.service.intf.EmbodyService;
import com.util.DateUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class EmbodyServiceImpl implements EmbodyService {
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private APPCreditMapper APPCreditMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper; 
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;
	@Autowired
	private AppLevelMapper appLevelMapper;
	@Autowired
	private AppIncreaseAmtInfoMapper appIncreaseAmtInfoMapper;
	

	
	/**
	* 创建人：Administrator   
	* 创建时间：2017年5月15日 下午5:51:49
	* 修改人：lizhongwei
	* 修改时间：2017年12月20日 下午5:34:49   
	* 修改备注： 首页接口修改
	* @version  3.0
	 */
	@Override
	public JSONObject homePage(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		// 等级
		List<AppLevel> appLevels = appLevelMapper.selectAll();
		// 品牌
		JSONArray jsonArrayBrand = new JSONArray();
		JSONObject object1 = new JSONObject();
		object1.put("isLock", "0"); //是否加锁 0：否，1：是
		object1.put("grade", "0"); //梅花
		object1.put("rangCreditAmt", "猜猜您的额度？");
		object1.put("creditAmtInfo", "");
		JSONObject object2 = initLevelJsonObject("D", appLevels);
		JSONObject object3 = initLevelJsonObject("C", appLevels);
		JSONObject object4 = initLevelJsonObject("B", appLevels);
		JSONObject object5 = initLevelJsonObject("A", appLevels);
		
		// 拉新活动
//		AppMessage message = appMessageMapper.selectByPrimaryKey(1021334); //19服务器环境
		AppMessage message = appMessageMapper.selectByPrimaryKey(1040166); //正式环境
		String h5Url = message.getH5Url();
		detail.put("activeUrl", h5Url);
		detail.put("mesId", 1040166 + "");
		
		//是否展示首页现金分期按钮 0：不展示，1：展示
		String showInstallmentBtn = "0";
		List<Map<String, Object>> btn = ifmBaseDictMapper.selectBaseDict("MCH_HOMEPAGE_INSTALLMENT_BTN_SHOW");
		String showBtn = StringUtil.nvl(btn.get(0).get("ITEM_VALUE"));
		if("1".equals(showBtn)){
			showInstallmentBtn = "1";
		}
		detail.put("showInstallmentBtn", showInstallmentBtn);
		List<Map<String,Object>> thePageTestList = ifmBaseDictMapper.selectBaseDict("MCH_HOMEPAGE_THEPAGETEXT");
		if("1".equals(thePageTestList.get(0).get("ITEM_KEY"))){  //是否显示 1 显示 0 不显示
			detail.put("thePageTest", thePageTestList.get(0).get("ITEM_VALUE"));  //借用上架公司名称
		}else{
			detail.put("thePageTest", "");
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		// 用户是否登录
		if (null != mch) {
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			// banner
			Map<String, Object> map = new HashMap<>();
			map.put("apprId", appLoanAppl.getId());
			map.put("type", "1");
			List<Map<String, Object>> maps = appMessageMapper.selectByapprIdAndTypeIsRead2(map);
			JSONArray dateListBanner = getHomePageBanner(maps);
			detail.put("picAnnounce", dateListBanner);
			//品牌
			JSONObject brandObject = getBrand(appLoanCtm, appLoanAppl);
			detail.putAll(brandObject);
			String level = null == appLoanAppl.getLevel() ? "" : appLoanAppl.getLevel();
			// 根据用户等级判断等级图片是否加锁
			jsonArrayBrand = getLockLevelPicByLevel(level, object1, object2, object3, object4, object5, jsonArrayBrand);
			detail.put("brand", jsonArrayBrand);
			// 根据用户等级返回APP需要的等级
			level = getNumLevel(level);
			detail.put("level", level);
			// 我要借款按钮
			JSONObject btnObject = getLoanBtn(appLoanAppl, appLoanCtm);
			detail.putAll(btnObject);
		} else {
			// banner
			Map<String, Object> map = new HashMap<>();
			map.put("phoneId", phoneId);
			map.put("type", "1");
			List<Map<String, Object>> maps = appMessageMapper.selectByapprIdAndTypeIsRead2(map);
			JSONArray dateListBanner = getHomePageBanner(maps);
			detail.put("picAnnounce", dateListBanner);
			//还款提醒消息
			JSONArray array = new JSONArray();
			detail.put("repayList", array); //还款提醒列表
			//品牌
			detail.put("brandStatus", "6"); // 品牌状态
			detail.put("brandMainInfo", ""); // 主信息
			detail.put("brandTipInfo", ""); // 返回提示信息
			detail.put("brandClickInfo", "");
			detail.put("betweenDays", ""); // 审核失败剩余等待天数
			detail.put("schduleStatus", "");
			jsonArrayBrand.add(object1);
			jsonArrayBrand.add(object2);
			jsonArrayBrand.add(object3);
			jsonArrayBrand.add(object4);
			jsonArrayBrand.add(object5);
			detail.put("brand", jsonArrayBrand);
			detail.put("level", "0");
			//按钮
			detail.put("buttonStatus", "8"); // 按钮状态
			detail.put("buttonTipInfo", "我要借款"); // 返回提示信息
			detail.put("buttonClickInfo", ""); // 返回点击信息
			detail.put("withDrawId", ""); // 订单号
		}
		return detail;
	}
	
	/**
	 * 首页未读消息数量分开查询
	 */
	@Override
	public JSONObject queryNotReadMessage(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String phoneId = JsonUtil.getJStringAndCheck(params, "phoneId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		int messCount = 0;
		if (null != mch) {
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			// 订单消息
			Map<String, Object> map1 = new HashMap<>();
			map1.put("apprId", appLoanAppl.getId());
			map1.put("type", "('2','3','4','5')");
			List<Map<String, Object>> list1 = appMessageMapper.selectByapprIdAndTypeIsRead4(map1);
			if (null != list1 && list1.size() > 0) {
				for (Map<String, Object> list : list1) {
					if ("0".equals(StringUtil.nvl(list.get("isRead")))) {
						messCount++;
					}
				}
			}
			// 系统消息
			Map<String, Object> map2 = new HashMap<>();
			map2.put("apprId", appLoanAppl.getId());
			map2.put("type", "0");
			List<Map<String, Object>> list2 = appMessageMapper.selectByapprIdAndTypeIsRead2(map2);
			if (null != list2 && list2.size() > 0) {
				for (Map<String, Object> list : list2) {
					if ("0".equals(StringUtil.nvl(list.get("isRead")))) {
						messCount++;
					}
				}
			}
		} else {
			// 系统消息
			Map<String, Object> map1 = new HashMap<>();
			map1.put("phoneId", phoneId);
			map1.put("type", "0");
			List<Map<String, Object>> list2 = appMessageMapper.selectByapprIdAndTypeIsRead3(map1);
			if (null != list2 && list2.size() > 0) {
				for (Map<String, Object> list : list2) {
					if ("0".equals(StringUtil.nvl(list.get("isRead")))) {
						messCount++;
					}
				}
			}
		}
		detail.put("messCount", messCount + ""); // 未读消息条目
		return detail;
	}
	
	/**
	 * 首页用户未还款还款计划轮播图
	 */
	@Override
	public JSONObject payPlanCarousel(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		//还款提醒消息
		JSONArray array = new JSONArray();
		if(null != mch){
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
			// 用户未还款还款计划
			List<APPPayPlan> payPlans = appPayPlanMapper.selectByApprIdAndStatus(appLoanAppl.getId(), "0");
			if (null != payPlans && payPlans.size() > 0) {
				String shouldPayDate = "";
				BigDecimal PayAmt = new BigDecimal("0.00");
				Integer restDays = 0;
				double monthPay = 0.0;
				double fxje = 0.0;
				double reducAmt = 0.0;
				JSONObject object = null;
				for (APPPayPlan payPlan : payPlans) {
					object = new JSONObject();
					shouldPayDate = DateUtil.format(payPlan.getRepayDate(), "yyyy.MM.dd");
					monthPay = null == payPlan.getMonthPay() ? 0.0 : payPlan.getMonthPay();
					fxje = null == payPlan.getFxje() ? 0.0 : payPlan.getFxje();
					reducAmt = null == payPlan.getReducAmt() ? 0.0 : payPlan.getReducAmt();
					PayAmt = (new BigDecimal(monthPay)).add(new BigDecimal(fxje).subtract(new BigDecimal(reducAmt)));
					restDays = Math.abs(DateUtil.getDateDaysBetween(new Date(), payPlan.getRepayDate()));
					if (payPlan.getDays() > 0) { // 逾期
						object.put("repayInfo", "还款日：" + shouldPayDate + "  " + StringUtil.formatNumberToDecimals(PayAmt + "", 2) + "元" + "  " + "逾期" + payPlan.getDays() + "天");
					} else {
						if(restDays == 0){
							object.put("repayInfo", "还款日：" + shouldPayDate + "  " + StringUtil.formatNumberToDecimals(PayAmt + "", 2) + "元" + "  " + "今日还款");
						}else{
							object.put("repayInfo", "还款日：" + shouldPayDate + "  " + StringUtil.formatNumberToDecimals(PayAmt + "", 2) + "元" + "  " + "距离还款日" + restDays + "天");
						}
					}
					array.add(object);
				}
			}
		}
		detail.put("repayList", array); //还款提醒列表
		return detail;
	}
	
	/**
	 * 初始化首页等级图片
	 * @param lev 等级
	 * @param appLevels 所有等级集合
	 * @return
	 */
	public JSONObject initLevelJsonObject(String lev, List<AppLevel> appLevels){
		JSONObject object = new JSONObject();
		if("A".equals(lev) || "B".equals(lev) || "C".equals(lev) || "D".equals(lev)){
			AppLevel level = getLevelInfoByLevel(lev, appLevels);
			String levelAmt = level.getMinCredit() + "~" + level.getMaxCredit();
			String levelDesc = StringUtil.nvl(level.getDescription());
			switch (lev) {
			case "A":
				object.put("isLock", "1"); //是否加锁 0：否，1：是
				object.put("grade", "4"); //随心花
				break;
			case "B":
				object.put("isLock", "1"); //是否加锁 0：否，1：是
				object.put("grade", "3"); //想花就花
				break;
			case "C":
				object.put("isLock", "1"); //是否加锁 0：否，1：是
				object.put("grade", "2"); //随便花
				break;
			case "D":
				object.put("isLock", "1"); //是否加锁 0：否，1：是
				object.put("grade", "1"); //有钱花
				break;
			}
			object.put("rangCreditAmt", levelAmt);
			object.put("creditAmtInfo", levelDesc);
		}
		return object;
	}
	
	/**
	 * 查询指定等级信息
	 * @param lev 等级
	 * @param appLevels 所有等级集合
	 * @return
	 */
	public AppLevel getLevelInfoByLevel(String lev, List<AppLevel> appLevels){
		AppLevel aLevel = null;
		if(null != appLevels && appLevels.size() > 0){
			for(AppLevel appLevel : appLevels){
				String level = StringUtil.nvl(appLevel.getLevel());
				if(lev.equals(level)){
					aLevel = appLevel;
					break;
				}
			}
		}
		return aLevel;
	}
	
	/**
	 * 首页查询banner图
	 * @param maps
	 * @return
	 */
	public JSONArray getHomePageBanner(List<Map<String, Object>> maps){
		JSONArray dateList = new JSONArray();
		if (null != maps && maps.size() > 0) {
			JSONObject jo = null;
			String shareStatus = "";
			String trackStatus = "0";
			for (Map<String, Object> m : maps) {
				jo = new JSONObject();
				String shareUrl = StringUtil.toString(m.get("share_url"));
				String is_track = StringUtil.nvl(m.get("is_track"));
				if (null == shareUrl || "".equals(shareUrl)) {
					shareStatus = "0"; // 不可分享
				} else {
					shareStatus = "1"; // 可分享
				}
				if("1".equals(is_track)){
					trackStatus = "1";
				}
				jo.put("messageId", StringUtil.toString(m.get("id")));
				jo.put("title", StringUtil.toString(m.get("title")));
				jo.put("picUrl", StringUtil.toString(m.get("pic_url")));
				jo.put("h5Url", StringUtil.toString(m.get("h5_url")));
				jo.put("shareStatus", shareStatus);
				jo.put("trackStatus", trackStatus);
				dateList.add(jo);
			}
		}
		return dateList;
	}
	
	/**
	 * 根据用户资料填写状态确定用户资料百分比
	 * @param status
	 * @return
	 */
	public int getPerFromScheduleStatus(int status){
		int per = 0;
		if(status >= 4){ //活体人像比对证
			per += 25;
		}
		if(status >= 5){ //基本信息
			per += 25;
		}
		if(status >= 6){ //联系人信息
			per += 25;
		}
		if(status >= 7){ //芝麻信用
			per += 10;
		}
		if(status >= 8){ //运营商
			per += 15;
		}
		return per;
	}
	
	/**
	 * 根据用户等级判断等级图片是否加锁
	 * @param level 等级
	 * @param object1
	 * @param object2
	 * @param object3
	 * @param object4
	 * @param object5
	 * @param array
	 * @return
	 */
	public JSONArray getLockLevelPicByLevel(String level, JSONObject object1, JSONObject object2, JSONObject object3, JSONObject object4, JSONObject object5, JSONArray array){
		switch (level) {
		case "A":
			object2.put("isLock", "0");//是否加锁 0：否，1：是
			object3.put("isLock", "0");
			object4.put("isLock", "0");
			object5.put("isLock", "0");
			level = "4";
			break;
		case "B":
			object2.put("isLock", "0");
			object3.put("isLock", "0");
			object4.put("isLock", "0");
			level = "3";
			break;
		case "C":
			object2.put("isLock", "0");
			object3.put("isLock", "0");
			level = "2";
			break;
		case "D":
			object2.put("isLock", "0");
			level = "1";
			break;
		default:
			object2.put("isLock", "1");
			object3.put("isLock", "1");
			object4.put("isLock", "1");
			object5.put("isLock", "1");
			level = "0";
			break;
		}
		array.add(object1);
		array.add(object2);
		array.add(object3);
		array.add(object4);
		array.add(object5);
		return array;
	}
	
	/**
	 * 获取品牌信息
	 * @param appLoanCtm
	 * @param appLoanAppl
	 * @return
	 */
	public JSONObject getBrand(AppLoanCtm appLoanCtm, AppLoanAppl appLoanAppl){
		JSONObject detail = new JSONObject();
		List<Map<String, Object>> lists = ifmBaseDictMapper.selectBaseDict("MCH_AUTHEN_FAIL_DAYS");
		Map<String, Object> m = lists.get(0);
		String authenFailDays = (null == m.get("ITEM_VALUE")) ? "0" : StringUtil.nvl(m.get("ITEM_VALUE"));
		String schduleStatus = appLoanCtm.getSchedule_status();
		// 资料认证完成度
		int per = getPerFromScheduleStatus(Integer.parseInt(schduleStatus));
		Integer betweenDays = 0;
		if("3".equals(appLoanAppl.getStatus())){ //认证失败
			Date authenTime = appLoanAppl.getAuthenTime();
			Date afterDay = DateUtil.addDays(authenTime, Integer.parseInt(authenFailDays));
			betweenDays = DateUtil.getDateDaysBetween(new Date(), afterDay);
			betweenDays = betweenDays < 1 ? 1 : betweenDays;
			detail.put("brandStatus", "0"); // 品牌状态
			detail.put("brandMainInfo", "猜猜您的额度？"); // 主信息
			detail.put("brandTipInfo", ""); // 返回提示信息
			detail.put("brandClickInfo", "");
			detail.put("schduleStatus", schduleStatus);
			detail.put("reFillInfo", "");
		}else if("0".equals(appLoanAppl.getStatus())){  //资料认证中
			if("0".equals(schduleStatus)){ //刚注册
				detail.put("brandStatus", "1");
				detail.put("brandMainInfo", "猜猜您的额度？");
				detail.put("brandTipInfo", "");
				detail.put("brandClickInfo", "请完成身份信息认证，获取评级金额");
				detail.put("schduleStatus", schduleStatus);
				detail.put("reFillInfo", "");
			}else if((Integer.parseInt(schduleStatus) >= 4)){ //未认证完成
				detail.put("brandStatus", "2");
				detail.put("brandMainInfo", per + "%");
				detail.put("brandTipInfo", "完成认证度");
				detail.put("brandClickInfo", "");
				detail.put("schduleStatus", schduleStatus);
				detail.put("reFillInfo", "");
			}
		}else if("1".equals(appLoanAppl.getStatus()) && "8".equals(schduleStatus)){ //资料认证通过，授信中
			detail.put("brandStatus", "3");
			detail.put("brandMainInfo", "100%");
			detail.put("brandTipInfo", "完成认证");
			detail.put("brandClickInfo", "");
			detail.put("schduleStatus", schduleStatus);
			detail.put("reFillInfo", "");
		}else if("2".equals(appLoanAppl.getStatus())){ //授信通过
			APPCredit appCredit = APPCreditMapper.selectByApprId(appLoanAppl.getId());
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
			double useAmt = appCredit.getUseAmt();
			if(freezeAmt > 0){
				double actualUseAmt = 0.00;
				if(useAmt - freezeAmt > 0){
					actualUseAmt = useAmt - freezeAmt;
				}
				detail.put("brandMainInfo", StringUtil.formatNumberToDecimals(actualUseAmt + "", 2));
			}else{
				detail.put("brandMainInfo", StringUtil.formatNumberToDecimals(useAmt + "", 2));
			}
			detail.put("brandClickInfo", "");
			detail.put("reFillInfo", "");
			/*Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("appr_id", appLoanAppl.getId());
			map3.put("type", "0");
			AppIncreaseAmtInfo increaseAmtInfo0 = appIncreaseAmtInfoMapper.selectByApprIdAndType(map3);
			map3 = new HashMap<String, Object>();
			map3.put("appr_id", appLoanAppl.getId());
			map3.put("type", "1");
			AppIncreaseAmtInfo increaseAmtInfo1 = appIncreaseAmtInfoMapper.selectByApprIdAndType(map3);
			if(null != increaseAmtInfo0 && null != increaseAmtInfo1){ //所有资料认证完成
				detail.put("brandStatus", "4");
				detail.put("brandTipInfo", "按时还款，避免逾期可晋级");
			}else{
				detail.put("brandStatus", "5");
				detail.put("brandTipInfo", "完善资料可尽快提额>");
			}*/
			detail.put("brandStatus", "5");
			detail.put("brandTipInfo", "完善资料可尽快提额>");
			detail.put("schduleStatus", schduleStatus);
		}else if("8".equals(appLoanAppl.getStatus())){ //认证资料被驳回
			if("8".equals(schduleStatus)){ //运营商已认证
				//重新填写认证资料
				String baseInfoStatus = appLoanAppl.getBaseInfoStatus();
				String contactInfoStatus = appLoanAppl.getContactInfoStatus();
				String zhimaStatus = appLoanAppl.getZhimaStatus();
				//跳转相应页面和显示相应百分
				if("1".equals(baseInfoStatus) && "1".equals(contactInfoStatus) && "1".equals(zhimaStatus)){
					detail.put("reFillInfo", "0"); //重新填写基本信息和联系人及芝麻信用
					detail.put("brandMainInfo", "40%");
				}else if("1".equals(baseInfoStatus) && "1".equals(contactInfoStatus) && !"1".equals(zhimaStatus)){
					detail.put("reFillInfo", "1"); //重新填写基本信息和联系人
					detail.put("brandMainInfo", "50%");
				}else if(!"1".equals(baseInfoStatus) && "1".equals(contactInfoStatus) && "1".equals(zhimaStatus)){
					detail.put("reFillInfo", "2"); //重新填写联系人和芝麻信用
					detail.put("brandMainInfo", "65%");
				}else if(!"1".equals(baseInfoStatus) && "1".equals(contactInfoStatus) && !"1".equals(zhimaStatus)){
					detail.put("reFillInfo", "3"); //重新填写联系人
					detail.put("brandMainInfo", "75%");
				}else if(!"1".equals(baseInfoStatus) && !"1".equals(contactInfoStatus) && "1".equals(zhimaStatus)){
					detail.put("reFillInfo", "4"); //重新填写芝麻信用
					detail.put("brandMainInfo", "90%");
				}else if(!"1".equals(zhimaStatus) && !"1".equals(baseInfoStatus) && !"1".equals(contactInfoStatus)){
					detail.put("reFillInfo", "5"); //无需重填资料，进入认证页面后自动授信
					detail.put("brandMainInfo", "100%");
				}
				detail.put("brandStatus", "7");
				detail.put("brandTipInfo", "完成认证度");
				detail.put("brandClickInfo", "");
				detail.put("schduleStatus", schduleStatus);
			}else{ //运营商未认证
				detail.put("brandStatus", "2");
				detail.put("brandMainInfo", per + "%");
				detail.put("brandTipInfo", "完成认证度");
				detail.put("brandClickInfo", "");
				detail.put("schduleStatus", schduleStatus);
				detail.put("reFillInfo", "");
			}
		}
		List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("MCH_USE_PROTOGENESIS");
		detail.put("betweenDays", betweenDays + ""); // 审核失败剩余等待天数
		detail.put("mchUseProtogenesis", StringUtil.nvl(list.get(0).get("ITEM_KEY"))); // 首页判断我要借款页面走原生还是h5 0 h5 1 android ios
		detail.put("mchUseProtogenesisUrl", StringUtil.nvl(list.get(0).get("ITEM_VALUE")));  //h5链接
		List<Map<String,Object>> base = ifmBaseDictMapper.selectBaseDict("MCH_USE_REIMBURSEMENT");
		detail.put("mchUseReimbursement", StringUtil.nvl(base.get(0).get("ITEM_KEY"))); // 首页判断还款页面走原生还是h5 0 h5 1 原生
		detail.put("mchUseReimbursementUrl", StringUtil.nvl(base.get(0).get("ITEM_VALUE")));  //h5链接
		return detail;
	}

	/**
	 * 获取我要借款按钮信息
	 * @param appLoanAppl
	 * @param appLoanCtm
	 * @return
	 */
	public JSONObject getLoanBtn(AppLoanAppl appLoanAppl, AppLoanCtm appLoanCtm){
		JSONObject detail = new JSONObject();
		String schduleStatus = appLoanCtm.getSchedule_status();
		if("0".equals(appLoanAppl.getStatus())){ //资料认证中
			detail.put("buttonStatus", "0"); // 按钮状态
			detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
			detail.put("buttonClickInfo", "请完成身份信息认证，获取评级金额"); // 返回按钮点击信息
			detail.put("withDrawId", ""); // 订单号
		}else if("1".equals(appLoanAppl.getStatus())){ //授信中
			detail.put("buttonStatus", "1"); // 按钮状态
			detail.put("buttonTipInfo", "资料审核中"); // 按钮显示信息
			detail.put("buttonClickInfo", ""); // 返回按钮点击信息
			detail.put("withDrawId", ""); // 订单号
		}else if("2".equals(appLoanAppl.getStatus())){ //授信通过
			if("".equals(StringUtil.nvl(appLoanCtm.getBind_card_time()))){
				detail.put("buttonStatus", "2"); // 按钮状态
				detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
				detail.put("buttonClickInfo", "您已认证审核成功，绑卡后可获得放款"); // 返回按钮点击信息
				detail.put("withDrawId", ""); // 订单号
			}else{
				List<APPWithDrawAppl> drawAppls = aPPWithDrawApplMapper.selectAllByapprId(appLoanAppl.getId());
				if(null != drawAppls && drawAppls.size() > 0){
					int overDuleCount = 0; //逾期订单数
					int reviewId = 0; //审核中订单号
					for(APPWithDrawAppl drawAppl : drawAppls){
						if("2".equals(drawAppl.getStatus()) && "3".equals(drawAppl.getLoanStatus())){
							overDuleCount++;
						}
						if("0".equals(drawAppl.getStatus())){
							reviewId = drawAppl.getId();
						}
					}
					if(overDuleCount > 0){
						detail.put("buttonStatus", "3"); // 按钮状态
						detail.put("buttonTipInfo", "逾期中"); // 按钮显示信息
						detail.put("buttonClickInfo", ""); // 返回按钮点击信息
						detail.put("withDrawId", ""); // 订单号
					}else{
						if (reviewId > 0) {
							detail.put("buttonStatus", "4"); // 按钮状态
							detail.put("buttonTipInfo", "借款审核中"); // 按钮显示信息
							detail.put("buttonClickInfo", ""); // 返回按钮点击信息
							detail.put("withDrawId", reviewId + ""); // 订单号
						}else{
							detail.put("buttonStatus", "5"); // 按钮状态
							detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
							detail.put("buttonClickInfo", ""); // 返回按钮点击信息
							detail.put("withDrawId", ""); // 订单号
						}
					}
				}else{
					detail.put("buttonStatus", "6"); // 按钮状态
					detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
					detail.put("buttonClickInfo", ""); // 返回按钮点击信息
					detail.put("withDrawId", ""); // 订单号
				}
			}
		}else if("3".equals(appLoanAppl.getStatus())){ //授信失败
			detail.put("buttonStatus", "7"); // 按钮状态
			detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
			detail.put("buttonClickInfo", ""); // 返回按钮点击信息
			detail.put("withDrawId", ""); // 订单号
		}else if("8".equals(appLoanAppl.getStatus())){ //认证资料被驳回
			if("8".equals(schduleStatus)){ //运营商已认证
				//重新填写认证资料
				detail.put("buttonStatus", "9"); // 按钮状态
				detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
				detail.put("buttonClickInfo", ""); // 返回按钮点击信息
				detail.put("withDrawId", ""); // 订单号
			}else{ //运营商未认证
				detail.put("buttonStatus", "2"); // 按钮状态
				detail.put("buttonTipInfo", "我要借款"); // 按钮显示信息
				detail.put("buttonClickInfo", "您已认证审核成功，绑卡后可获得放款"); // 返回按钮点击信息
				detail.put("withDrawId", ""); // 订单号
			}
		}
		return detail;
	}
	
	/**
	 * 根据用户等级返回APP需要的等级
	 * @param level
	 * @return
	 */
	public String getNumLevel(String level){
		switch (level) {
		case "A":
			level = "4";
			break;
		case "B":
			level = "3";
			break;
		case "C":
			level = "2";
			break;
		case "D":
			level = "1";
			break;
		default:
			level = "0";
			break;
		}
		return level;
	}
	
}
