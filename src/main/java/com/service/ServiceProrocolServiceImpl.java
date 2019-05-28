package com.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPPayPlanMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLevelMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppOrderCtmMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPPayPlan;
import com.model.APPWithDrawAppl;
import com.model.AppLevel;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppOrderCtm;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.ServiceProrocolService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class ServiceProrocolServiceImpl implements ServiceProrocolService {
	
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private APPWithDrawApplMapper appWithDrawApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private APPPayPlanMapper appPayPlanMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLevelMapper appLevelMapper;
	@Autowired
	private AppOrderCtmMapper appOrderCtmMapper;
	/**
	 * 随心花3.0借款合同
	 */
	@Override
	public JSONObject queryServiceProrocol(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String withdrawId = JsonUtil.getJStringAndCheck(params, "withdrawId", null, false, detail);
		String borrowPerion2 = JsonUtil.getJStringAndCheck(params, "borrowPerion", null, false, detail);
		String borrowAmt2 = JsonUtil.getJStringAndCheck(params, "borrowAmt", null, false, detail);
		String purposeCode2 = JsonUtil.getJStringAndCheck(params, "purposeCode", null, false, detail);
		String gangSign = JsonUtil.getJStringAndCheck(params, "gangSign", null, false, detail);
		// 获取逾期率
		JSONObject overDuleRate = getOverDuleRate(ifmBaseDictMapper);
		String overdule3DayRate = StringUtil.nvl(overDuleRate.get("overdule3DayRate")); //三天内逾期利率
		String overduleOver3DayRate = StringUtil.nvl(overDuleRate.get("overduleOver3DayRate")); //超过三天逾期利率
		// 爱鸿森查看合同
		if("".equals(userId) && !"".equals(gangSign) && !"".equals(withdrawId)){
			APPWithDrawAppl withDrawAppl = appWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withdrawId));
			// 校验合同号
			if(gangSign.equals(StringUtil.MD5(withdrawId + "ahs")) && null != withDrawAppl){
				AppLoanAppl loanAppl = appLoanApplMapper.selectByPrimaryKey(withDrawAppl.getApprId());
				// 查询用户资料
				JSONObject prorocolResult = getProrocolResult(loanAppl, withDrawAppl);
				detail.putAll(prorocolResult);
				detail.put("overdule3DayRate", overdule3DayRate);
				detail.put("overduleOver3DayRate", overduleOver3DayRate);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "不存在的合同");
			}
			return detail;
		}
		// 随心花查看合同
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if(null != appUser){
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
			if(null != loanAppl){
				//借款申请发起地
				List<Map<String, Object>> map =  ifmBaseDictMapper.selectBaseDict("MCH_RZ_SIGN_ADDRESS");
				String initiatedAddress = StringUtil.nvl(map.get(0).get("ITEM_VALUE"));
				//一次性逾期管理费
				List<Map<String, Object>> map1 =  ifmBaseDictMapper.selectBaseDict("MCH_OVERDUE_MANAGE_FEE");
				String overdueManageFee = StringUtil.nvl(map1.get(0).get("ITEM_VALUE"));
				if(!"".equals(withdrawId)){
					List<APPWithDrawAppl> withDrawAppls = appWithDrawApplMapper.selectAllByapprId(loanAppl.getId());
					if(null != withDrawAppls && withDrawAppls.size() > 0){
						int count = 0;
						for(APPWithDrawAppl withDrawAppl : withDrawAppls){
							if(withDrawAppl.getId().intValue() == Integer.parseInt(withdrawId)){
								count++;
								break;
							}
						}
						if(count > 0){
							APPWithDrawAppl withDrawAppl = appWithDrawApplMapper.selectByPrimaryKey(Integer.parseInt(withdrawId));
							// 查询用户资料
							JSONObject prorocolResult = getProrocolResult(loanAppl, withDrawAppl);
							detail.putAll(prorocolResult);
							//每天服务费
							AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(loanAppl.getLevel()));
							String fwf = "0.00";
							if("".equals(StringUtil.nvl(withDrawAppl.getDayRate()))){
								fwf = StringUtil.formatNumberToDecimals(new BigDecimal(withDrawAppl.getBorrowAmt()).multiply(new BigDecimal(appLevel.getMchRate())).toString(), 2);
							}else{
								fwf = StringUtil.formatNumberToDecimals(new BigDecimal(withDrawAppl.getBorrowAmt()).multiply(new BigDecimal(withDrawAppl.getDayRate())).toString(), 2);
							}							String actualAmt = withDrawAppl.getActualAmt();
							String commissionAmt = withDrawAppl.getCommissionAmt();
							java.text.SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
							detail.put("overdule3DayRate", overdule3DayRate);
							detail.put("overduleOver3DayRate", overduleOver3DayRate);
							detail.put("fwf", fwf);
							detail.put("actualAmt", actualAmt);
							detail.put("commissionAmt", commissionAmt);
							detail.put("serveRate", StringUtil.nvl(Double.parseDouble(withDrawAppl.getFwfRate())*100).substring(0, 2));  
							detail.put("borrowDate", formatter.format(withDrawAppl.getBorrowTime()));  //合同签订日期
							detail.put("investorName", "浙江纵横新创科技有限公司");  //资方公司名称
							detail.put("investorAccount", "571910284610605");  //资方账号
							detail.put("investorOpenBank", "招商银行股份有限公司杭州海创园小微企业专营支行");  //资方开户行
							//借款申请发起地
							detail.put("initiatedAddress", initiatedAddress);
							//逾期管理费
							detail.put("overdueManageFee", overdueManageFee);
							//日利率
							String dayRate = StringUtil.nvl(withDrawAppl.getDayRate());
							if(!"".equals(dayRate)){
								detail.put("dayRate", new BigDecimal(dayRate).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP)+"%");
							}else{
								detail.put("dayRate", new BigDecimal(appLevel.getMchRate()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP)+"%");
							}
							
						}else{
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "该订单不属于您");
						}
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您还没有订单，请先借款");
					}
				}else{
					AppLevel appLevel = appLevelMapper.selectByLevel(StringUtil.nvl(loanAppl.getLevel()));
					if(null == appLevel){
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您当前需要授信，请完善资料授信！");
						return detail;
					}
					String mchRate = appLevel.getMchRate();
					////每天服务费
					String fwf = StringUtil.formatNumberToDecimals(new BigDecimal(borrowAmt2).multiply(new BigDecimal(mchRate)).toString(), 2);
					String cutRate = StringUtil.nvl(Double.parseDouble(appLevel.getFwfRate())*100).substring(0, 2);
					String commissionAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(Double.parseDouble(appLevel.getFwfRate()) * Double.parseDouble(borrowAmt2)),2);
					String actualAmt = StringUtil.formatNumberToDecimals(StringUtil.nvl(Double.parseDouble(borrowAmt2) - Double.parseDouble(commissionAmt)),2);
					String purposeName = getPurposeName(purposeCode2, ifmBaseDictMapper);
					detail.put("purposeName", purposeName);
					//查询用户信息
					AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
					if(appLoanCtm!=null){
						//客户姓名
						detail.put("customName", StringUtil.nvl(appLoanCtm.getCustomName()));
						//身份证号
						detail.put("idetityCard", StringUtil.nvl(appLoanCtm.getIdentityCard()));
						//手机号
						detail.put("phone", StringUtil.nvl(loanAppl.getItemCode()));
					}else{
						detail.put("customName", "");
						detail.put("idetityCard", "");
						detail.put("phone", "");
					}
					detail.put("borrowPerion", borrowPerion2);
					detail.put("borrowAmt", borrowAmt2);
					detail.put("contractNo", "");
					detail.put("mchRate", mchRate);//每日0.1%利息之和
					detail.put("loanDate", "");
					detail.put("repayDate", "");
					detail.put("shouldPayAmt", "");
					detail.put("bankCardNo", "");
					detail.put("serveRate", cutRate);
					detail.put("overdule3DayRate", overdule3DayRate);
					detail.put("overduleOver3DayRate", overduleOver3DayRate);
					detail.put("fwf", fwf);
					detail.put("actualAmt", actualAmt);
					detail.put("commissionAmt", commissionAmt);
					detail.put("borrowDate", "");
					detail.put("investorName", "");
					detail.put("investorAccount", "");
					detail.put("investorOpenBank", "");
					//借款申请发起地
					detail.put("initiatedAddress", initiatedAddress);
					//逾期管理费
					detail.put("overdueManageFee", overdueManageFee);
					//日利率
					detail.put("dayRate", new BigDecimal(appLevel.getMchRate()).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_UP)+"%");
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户资料异常，请联系客服");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "登录后才能查看订单");
		}
		return detail;
	}
	
	/**
	 * 获取逾期率
	 * @return
	 */
	public static JSONObject getOverDuleRate(IfmBaseDictMapper ifmBaseDictMapper){
		JSONObject result = new JSONObject();
		String overdule3DayRate = ""; //三天内逾期利率
		String overduleOver3DayRate = ""; //超过三天逾期利率
		List<Map<String, Object>> overduleLists = ifmBaseDictMapper.selectBaseDict("YQ_PARAMS");
		if(null != overduleLists && overduleLists.size() > 0){
			overdule3DayRate = StringUtil.nvl(overduleLists.get(0).get("ITEM_KEY"));
			overduleOver3DayRate = StringUtil.nvl(overduleLists.get(0).get("ITEM_VALUE"));
			overdule3DayRate = StringUtil.subZeroAndDot(StringUtil.formatNumberToDecimals(Double.parseDouble(overdule3DayRate) * 100 + "" , 2));
			overduleOver3DayRate = StringUtil.subZeroAndDot(StringUtil.formatNumberToDecimals(Double.parseDouble(overduleOver3DayRate) * 100 + "" , 2));
		}
		result.put("overdule3DayRate", overdule3DayRate);
		result.put("overduleOver3DayRate", overduleOver3DayRate);
		return result;
	}
	
	/**
	 * 根据借款用户编码获取借款用途
	 * @param purposeCode
	 * @return 借款用途
	 */
	public static String getPurposeName(String purposeCode, IfmBaseDictMapper ifmBaseDictMapper){
		String purposeName = "";
		if(!"".equals(StringUtil.nvl(purposeCode))){
			IfmBaseDict ifmBaseDict = new IfmBaseDict();
			ifmBaseDict.setDataType("PURPOSE");
			ifmBaseDict.setItemKey(purposeCode);
			ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
			if(ifmBaseDict != null){
				purposeName = StringUtil.nvl(ifmBaseDict.getItemValue());
			}
		}
		return purposeName;
	}
	
	/**
	 * 根据还款计划获取应还款金额和应还款时间
	 * @param payPlans
	 * @return
	 */
	private JSONObject getShouldPayAmtAndPayDate(List<APPPayPlan> payPlans){
		JSONObject result = new JSONObject();
		result.put("repayDate", "");
		result.put("shouldPayAmt", "");
		if(null != payPlans && payPlans.size() > 0){
			APPPayPlan appPayPlan = payPlans.get(0);
			String repayDate = DateUtil.format(appPayPlan.getRepayDate(), "yyyy-MM-dd");
			String fxje = null == appPayPlan.getFxje() ? "0.00" : (appPayPlan.getFxje() + "");
			String monthPayAmt = null == appPayPlan.getMonthPay() ? "0.00" : (appPayPlan.getMonthPay() + "");
			String reducAmt = null == appPayPlan.getReducAmt() ? "0.00" : (appPayPlan.getReducAmt() + "");
			String shouldPayAmt = StringUtil.formatNumberToDecimals(new BigDecimal(monthPayAmt).add(new BigDecimal(fxje).subtract(new BigDecimal(reducAmt))).toString(), 2);
			result.put("repayDate", repayDate);
			result.put("shouldPayAmt", shouldPayAmt);
		}
		return result;
	}
	
	/**
	 * 获取用户合同信息
	 * @param loanAppl
	 * @param withDrawAppl
	 * @return
	 */
	private JSONObject getProrocolResult(AppLoanAppl loanAppl, APPWithDrawAppl withDrawAppl){
		JSONObject detail = new JSONObject();
		// 查询用户资料
		AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
		String customName = loanCtm.getCustomName();
		String idetityCard = loanCtm.getIdentityCard();
		String phone = loanAppl.getItemCode();
		List<AppOrderCtm> list = appOrderCtmMapper.selectOrderCtmByDrawId(withDrawAppl.getId());
		String bankCardNo = StringUtil.nvl(list.get(0).getBankCard());
		// 查询订单信息
		String borrowPerion = withDrawAppl.getBorrowDays();
		String borrowAmt = withDrawAppl.getBorrowAmt();
		String contractNo = withDrawAppl.getContract_no();
		String mchRate = withDrawAppl.getNhl();
		String loanDate = null == withDrawAppl.getLoanDate() ? "" : DateUtil.format(withDrawAppl.getLoanDate(), "yyyy-MM-dd");
		String purposeName = getPurposeName(withDrawAppl.getPurpose(), ifmBaseDictMapper);
		// 获取应还款金额和应还款时间
		List<APPPayPlan> payPlans = appPayPlanMapper.selectAllByWithDrawId(withDrawAppl.getId());
		JSONObject payResult = getShouldPayAmtAndPayDate(payPlans);
		String repayDate = StringUtil.nvl(payResult.get("repayDate"));
		String shouldPayAmt = StringUtil.nvl(payResult.get("shouldPayAmt"));
		// 服务费率
		String serveRate = null == withDrawAppl.getFwfRate() ? "15" : withDrawAppl.getFwfRate().substring(2);
		
		detail.put("purposeName", purposeName);
		detail.put("customName", customName);
		detail.put("idetityCard", idetityCard);
		detail.put("phone", phone);
		detail.put("borrowPerion", borrowPerion);
		detail.put("borrowAmt", borrowAmt);
		detail.put("contractNo", "SXHHT" + contractNo);
		detail.put("mchRate", mchRate);
		detail.put("loanDate", loanDate);
		detail.put("repayDate", repayDate);
		detail.put("shouldPayAmt", shouldPayAmt);
		detail.put("bankCardNo", bankCardNo);
		detail.put("serveRate", serveRate);
		return detail;
	}

}
