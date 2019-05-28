package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoginMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLogin;
import com.model.AppPhoneValicode;
import com.model.AppUser;
import com.service.intf.UpdatePasswordService;
import com.util.ErrorCode;
import com.util.JsonUtil;

@Service
public class UpdatePasswordImpl implements UpdatePasswordService {
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;

	/**
	 * 修改密码
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updatePassword(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String oldPassword = JsonUtil.getJStringAndCheck(params, "oldPassword", null, true, detail);
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String newPassword = JsonUtil.getJStringAndCheck(params, "newPassword", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser user = appUserMapper.selectByMchVersion(userId);
		if (null != user) {
			AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(user.getUserid());
			if(null != loanAppl.getAccountStatus() && 3 == loanAppl.getAccountStatus().intValue()){ //账户关闭
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
				return detail;
			} else { 
				String phone = loanAppl.getItemCode();
				AppLogin login = appLoginMapper.selectByPhone(phone);
				if (null != login) {
					String password = login.getPassword();
					if (!oldPassword.equals(password)) {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "修改密码失败");
					} else {
						login.setPassword(newPassword);
						login.setHisPassword(oldPassword);
						appLoginMapper.updateByPrimaryKeySelective(login);
						detail.put(Consts.RESULT_NOTE, "修改密码成功");
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "手机号不存在，请先注册");
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

	/**
	 * 忘记密码
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject forgetPassword(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, false, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		if(-1 != phone.indexOf(" ")){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号不能有空格");
			return detail;
		}
		if(!"2".equals(type)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "短信类型错误");
			return detail;
		}
		AppLogin login = appLoginMapper.selectByPhone(phone);
		if (null != login) {
			Map<String, Object> map0 = new HashMap<>();
			map0.put("phone", phone);
			map0.put("status", type);
			AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map0);
			if (appPhoneValicode != null) {
				Date creatTime = appPhoneValicode.getCreatTime();
				Date d = new Date();
				long nowTime = d.getTime();
				long longDate = creatTime.getTime();
				if (nowTime - longDate < 180000) {
					if (appPhoneValicode.getValicode().equals(valiCode)) {
						login.setPassword(password);
						appLoginMapper.updateByPrimaryKeySelective(login);
						detail.put(Consts.RESULT_NOTE, "修改成功");
					} else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码不对");
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "验证码超时");
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "验证码不正确");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号不存在");
		}
		return detail;
	}

	
	
	/**
	  *  Description:快捷登录设置密码
	  *  @author  yang.wu  
	  *	 DateTime 2017年11月14日 下午1:45:21
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject setPassword(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String password = JsonUtil.getJStringAndCheck(params, "password", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser user = appUserMapper.selectByMchVersion(userId);
		if (null != user) {
			AppLogin appLogin = appLoginMapper.selectByPhone(user.getUserName());
			appLogin.setPassword(password);
			appLogin.setRegisterDate(new Date());
			appLoginMapper.updateByPrimaryKeySelective(appLogin);
			detail.put(Consts.RESULT_NOTE, "设置成功");
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户不存在");
		}
		return detail;
	}

}
