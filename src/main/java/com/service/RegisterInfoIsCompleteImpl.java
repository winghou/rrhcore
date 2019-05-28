package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.APPWithDrawApplMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppLoginMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.APPCredit;
import com.model.APPWithDrawAppl;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmShip;
import com.model.AppLogin;
import com.model.AppUser;
import com.service.intf.RegisterInfoIsCompleteService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class RegisterInfoIsCompleteImpl implements RegisterInfoIsCompleteService {
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private APPCreditMapper aPPCreditMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private APPWithDrawApplMapper aPPWithDrawApplMapper; 
	@Override
	public JSONObject registerInfoIsComplete(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
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
		if (appLoanAppl != null) {
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			if (appLoanCtm != null && appLoanCtm.getCustomName() != null && !appLoanCtm.getCustomName().equals("")
					&& appLoanCtm.getIdentityCard() != null && !appLoanCtm.getIdentityCard().equals("")
					&& appLoanCtm.getSchoolName() != null && !appLoanCtm.getSchoolName().equals("")
					&& appLoanCtm.getBdrAddr() != null && !appLoanCtm.getBdrAddr().equals("")
					//&& appLoanCtm.getEntraDate() != null && !appLoanCtm.getEntraDate().equals("")
					&& appLoanCtm.getGrade() != null && !appLoanCtm.getGrade().equals("")
					&& !"".equals(StringUtil.toString(appLoanCtm.getPlace()))
					&& appLoanCtm.getEducational() != null && !appLoanCtm.getEducational().equals("")
					//&& appLoanCtm.getAddress() != null && !appLoanCtm.getAddress().equals("")
					&& appLoanCtm.getBank() != null && !appLoanCtm.getBank().equals("")
					&& appLoanCtm.getBankCard() != null && !appLoanCtm.getBankCard().equals("")
					/*&& appLoanCtm.getBankPhone() != null && !appLoanCtm.getBankPhone().equals("")*/) {
				//AppLoanAttch appLoanAttch = appLoanAttchMapper.selectByapprId(appLoanAppl.getId());
				/*if (appLoanAttch != null && appLoanAttch.getFileName() != null && !appLoanAttch.getFileName().equals("")
						&& appLoanAttch.getFileUri() != null && !appLoanAttch.getFileUri().equals("")) {*/
				if(true){
					List<Map<String, Object>> cntPass = appLoanCtmCntMapper.selectCnPassByApprId(appLoanAppl.getId());
					//List<Map<String, Object>> cntCommt = appLoanCtmCntMapper.selectCntCommtByApprId(appLoanAppl.getId());
					List<AppLoanCtmShip> ship = appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId());
					if (cntPass.size() == 2 &&/* cntCommt.size() == 2 &&*/ ship.size() == 3) {
						detail.put("isCompleter", "true");
					} else {
						detail.put("isCompleter", "false");
					}
				}/* else {
					detail.put("isCompleter", "false");
				}*/
			} else {
				detail.put("isCompleter", "false");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "无此userId！");
		}
		return detail;
	}
	
	
	/**
	* 创建人：lizhongwei   
	* 创建时间：2017年5月18日 上午9:56:11   
	* 修改人：lizhongwei  
	* 修改时间：2017年5月18日 上午9:56:11   
	* 修改备注：   
	  @param userId  
	  @return customName 用户名
	  		  phone 加密手机号
	  		  creditAmt 授信额度
	  		  userAmt  可用额度
	  		  authenStatus  身份认证状态
	  		  authenInfo  认证返回信息
	* @version  3.0
	 */
	@Override
	public JSONObject mine(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		String level = "0";
		if (null != mch) {
			userId = mch.getUserid() + "";
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			switch (StringUtil.nvl(appLoanAppl.getLevel())) {
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
			}
			detail.put("level", level);
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			if (null != appLoanCtm.getCustomName() && !"".equals(appLoanCtm.getCustomName())) {
				detail.put("customName", appLoanCtm.getCustomName());
			} else {
				detail.put("customName", "随心花");
			}
			if(Integer.parseInt(appLoanCtm.getSchedule_status()) < 4){
				detail.put("bindCardStatus", "2"); //身份证还没认证
			}else{
				if(null != appLoanCtm.getBind_card_time() && !"".equals(appLoanCtm.getBind_card_time())){
					detail.put("bindCardStatus", "1"); //已绑卡
				}else{
					detail.put("bindCardStatus", "0"); //未绑卡
				}
			}
			String phone = appLoanAppl.getItemCode();
			if (!"".equals(phone) && null != phone) {
				phone = phone.substring(0, 3) + "****" + phone.substring(7);
			}
			detail.put("phone", phone);
			APPCredit credit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
			String creditAmt1 = "";
			String userAmt1 = "";
			if(null != credit){
				double creditAmt = credit.getCreditAmt();
				double userAmt = credit.getUseAmt();
				//重新计算可用额度
				List<APPWithDrawAppl> applWithDrawApplList = aPPWithDrawApplMapper.selectAllByapprId(appLoanAppl.getId());
				double auditAmt = 0.00;
				if(applWithDrawApplList.size() > 0){
					for(APPWithDrawAppl appWithDrawAppl : applWithDrawApplList){
						if(!("3").equals(appWithDrawAppl.getLoanStatus()) && !("4").equals(appWithDrawAppl.getLoanStatus())){
							auditAmt = auditAmt + Double.parseDouble(appWithDrawAppl.getBorrowAmt());
						}
					}
				}
				APPCredit appCredit = aPPCreditMapper.selectByApprId(appLoanAppl.getId());
				double freezeAmt = auditAmt + appCredit.getUseAmt() + appCredit.getWait_pay_amt() - appCredit.getCreditAmt();
				creditAmt1 = StringUtil.formatNumberToDecimals(creditAmt + "", 2);
				if(userAmt - freezeAmt > 0){
					userAmt1 = StringUtil.formatNumberToDecimals((userAmt - freezeAmt)+ "", 2);
				}else{
					userAmt1 = 0.00 + "";
				}
				
			}else{
				creditAmt1 = "0.00";
				userAmt1 = "0.00";
			}
			detail.put("creditAmt", creditAmt1);
			detail.put("userAmt", userAmt1);
			// 邀请码
			String inviteCode = appLoanAppl.getInviteCode();
			detail.put("inviteCode", inviteCode);
		} else {
			detail.put("level", level);
			detail.put("customName", "");
			detail.put("phone", "");
			detail.put("creditAmt", "");
			detail.put("userAmt", "");
			detail.put("inviteCode", "");
			detail.put("bindCardStatus", "0");
		}
		return detail;
	}
	
}
