package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPCredit;
import com.model.AppLoanAppl;
import com.model.AppLoanCtmCnt;
import com.model.AppUser;
import com.service.intf.AuthenticationCustomInfo;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class AuthenticationCustomImpl implements AuthenticationCustomInfo {
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private APPCreditMapper aPPCreditMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	
	/**
	 * 提额资料认证
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject authenticationCustom(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String accont = JsonUtil.getJStringAndCheck(params, "accont", null, false, detail);
		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);
		String authenType = JsonUtil.getJStringAndCheck(params, "authenType", null, false, detail);
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		if (null == appUser) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = appUser.getUserid() + "";
		AppLoanAppl appl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		if (null != appl) {
			String str = "";
			switch (authenType) {
			case "1":
				str = "社交账号";
				break;
			case "2":
				str = "运营商账号";
				break;
			case "3":
				str = "京东账号";
				break;
			case "4":
				str = "淘宝账号";
				break;
			case "5":
				str = "信用卡邮箱";
				break;
			}
			if ("1".equals(authenType)) {
				if (!"".equals(accont)) {
					str = "QQ";
				} else {
					str = "微信";
				}
			}
			Map<String, Object> mp = new HashMap<>();
			mp.put("cntType", authenType);
			mp.put("apprId", appl.getId());
			AppLoanCtmCnt cnt = appLoanCtmCntMapper.queryByType(mp);
			if (null == cnt) { // 新增
				AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
				if (!"".equals(accont) && null != accont) {
					appLoanCtmCnt.setCntCommt(accont);
				}
				if (!"".equals(password) && null != password) {
					appLoanCtmCnt.setCntPass(password);
				}
				appLoanCtmCnt.setApprId(appl.getId());
				appLoanCtmCnt.setCntType(authenType);
				appLoanCtmCnt.setCntDesc("1");
				appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
				if (!"1".equals(authenType)) {
					detail.put(Consts.RESULT_NOTE, str + "保存成功");
				} else {
					detail.put(Consts.RESULT_NOTE, str + "绑定成功");
				}
			} else { // 修改
				if (!"".equals(accont) && null != accont) {
					cnt.setCntCommt(accont);
				}
				if (!"".equals(password) && null != password) {
					cnt.setCntPass(password);
				}
				cnt.setApprId(appl.getId());
				cnt.setCntType(authenType);
				cnt.setCntDesc("1");
				appLoanCtmCntMapper.updateByPrimaryKeySelective(cnt);
				if (!"1".equals(authenType)) {
					detail.put(Consts.RESULT_NOTE, str + "修改成功");
				} else {
					detail.put(Consts.RESULT_NOTE, str + "绑定成功");
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户资料异常，请联系客服");
		}
		return detail;
	}

	/**
	 * 保存信用卡和公积金信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject saveXykAndGjj(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String accont = JsonUtil.getJStringAndCheck(params, "accont", null, false, detail);
		String authenType = JsonUtil.getJStringAndCheck(params, "authenType", null, false, detail);
		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);
		String cityOrbank = JsonUtil.getJStringAndCheck(params, "cityOrbank", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (null != appUser) {
			AppLoanAppl appl = appLoanApplMapper.selectByitemCode(appUser.getUserName());
			Map<String, Object> mp = new HashMap<>();
			mp.put("cntType", authenType);
			mp.put("apprId", appl.getId());
			AppLoanCtmCnt cnt = appLoanCtmCntMapper.queryByType(mp);
			String code = "";
			if ("6".equals(authenType)) {
				int n = 0;
				List<Map<String, Object>> dicts = ifmBaseDictMapper.selectBaseDict("BANK_NAME");
				for (Map<String, Object> dict : dicts) {
					String bankName = StringUtil.nvl(dict.get("ITEM_VALUE"));
					if (cityOrbank.equals(bankName)) {
						String bankCode = StringUtil.nvl(dict.get("ITEM_KEY"));
						code = bankCode;
						n++;
						break;
					}
				}
				if (0 == n) {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "请选择正确的开户银行");
					return detail;
				}
			}
			List<Map<String, Object>> baseDictes = ifmBaseDictMapper.selectBaseDict("ADD_AMT");
			Map<String, Object> map = baseDictes.get(0);
			String amt = StringUtil.toString(map.get("ITEM_VALUE"));
			APPCredit appCredit = aPPCreditMapper.selectByApprId(appl.getId());
			if (null == cnt || !authenType.equals(cnt.getCntType())) {
				AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
				appLoanCtmCnt.setCntCommt(accont);
				appLoanCtmCnt.setCntPass(password);
				appLoanCtmCnt.setApprId(appl.getId());
				appLoanCtmCnt.setCntType(authenType);
				appLoanCtmCnt.setCntDesc("1");
				if ("6".equals(authenType)) {
					appLoanCtmCnt.setCntLx(code);
				} else {
					appLoanCtmCnt.setCntLx(cityOrbank);
				}
				appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
				Double useAmt = appCredit.getUseAmt();
				Double creditAmt = appCredit.getCreditAmt();
				Double nowAmt = useAmt + Double.parseDouble(amt);
				Double nowAmt2 = creditAmt + Double.parseDouble(amt);
				appCredit.setUseAmt(nowAmt);
				appCredit.setCreditAmt(nowAmt2);
				aPPCreditMapper.updateByPrimaryKeySelective(appCredit);
				if ("6".equals(authenType)) {
					detail.put(Consts.RESULT_NOTE, "信用卡账号保存成功");
				} else {
					detail.put(Consts.RESULT_NOTE, "公积金账号保存成功");
				}
			} else {
				if ("6".equals(authenType)) {
					cnt.setCntCommt(accont);
					cnt.setCntPass(password);
					cnt.setCntDesc("1");
					cnt.setCntLx(code);
					detail.put(Consts.RESULT_NOTE, "信用卡账号修改成功");
					appLoanCtmCntMapper.updateByPrimaryKeySelective(cnt);
				} else {
					cnt.setCntCommt(accont);
					cnt.setCntPass(password);
					cnt.setCntDesc("1");
					cnt.setCntLx(cityOrbank);
					detail.put(Consts.RESULT_NOTE, "公积金账号修改成功");
					appLoanCtmCntMapper.updateByPrimaryKeySelective(cnt);
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户不存在");
		}
		return detail;
	}

}
