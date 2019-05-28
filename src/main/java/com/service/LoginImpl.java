package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoginMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppSysLoginLogMapper;
import com.dao.AppSysSessionMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLogin;
import com.model.AppPhoneValicode;
import com.model.AppSysLoginLog;
import com.model.AppSysSession;
import com.model.AppUser;
import com.mq.yingyingbao.YYBMsgProducer;
import com.service.intf.LoginService;
import com.service.intf.YingYongBaoService;
import com.util.Constants;
import com.util.ErrorCode;
import com.util.InvitationCodeUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class LoginImpl implements LoginService {
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppSysLoginLogMapper appSysLoginLogMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private AppSysSessionMapper appSysSessionMapper;
	@Autowired
	private  YYBMsgProducer  yybMsgProducer;

	/**
	 * 账号密码登录
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject login(JSONObject params) throws Exception{
			
		JSONObject detail = new JSONObject();
		String userCode = JsonUtil.getJStringAndCheck(params, "userCode", null, false, detail);
		String passWord = JsonUtil.getJStringAndCheck(params, "passWord", null, false, detail);
		
		String deviceType =JsonUtil.getJStringAndCheck(params, "deviceType", null, true, detail);
		String deviceId=JsonUtil.getJStringAndCheck(params, "deviceId", null, false, detail);
		String client_ip=JsonUtil.getJStringAndCheck(params, "client_ip", null, false, detail);
				
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
		String inj_stra[] = inj_str.split("\\|");
		for (int i = 0; i < inj_stra.length; i++) {
			if (userCode.indexOf(inj_stra[i]) != -1) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "账号含有非法字符");
				return detail;
			}
		}
		AppLogin isExist = appLoginMapper.selectByPhone(userCode);
		if (null != isExist) {
			AppLogin appLogin = new AppLogin();
			appLogin.setPassword(passWord);
			appLogin.setUserCode(userCode);
			AppLogin login = appLoginMapper.login(appLogin);
			if (login != null) {
				//ios的设备信息不用发给应用宝，只发安卓的
				if("1".equals(deviceType)) {
//					yybMsgProducer.sendMessage(Constants.yyb_mq_topic, Constants.yyb_mq_tag, params.toJSONString());
				}
				AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
				if("".equals(StringUtil.nvl(appLoanAppl.getInviteCode()))){
					appLoanAppl.setInviteCode( InvitationCodeUtil.toSerialCode((long)appLoanAppl.getUserId()));
					appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
				}
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
				if (null != appLoanCtm.getCustomName() && !"".equals(appLoanCtm.getCustomName())) {
					detail.put("customName", appLoanCtm.getCustomName());
				} else {
					detail.put("customName", "随心花");
				}
				AppUser user = appUserMapper.selectByPhone(login.getUserCode());
				detail.put("phone", login.getUserCode());
				detail.put("userId", StringUtil.toString(user.getMch_version()));
				detail.put("orgId", StringUtil.toString(user.getOrgId()));
				AppSysLoginLog log = new AppSysLoginLog();
				log.setStatus("0");
				log.setUserCode(login.getUserCode());
				log.setDeviceType(deviceType);
				log.setDeviceId(deviceId);
				log.setAddress(client_ip);
				appSysLoginLogMapper.insertSelective(log);
				String token = UUID.randomUUID().toString().replace("-", "");
				AppSysSession sysSession = appSysSessionMapper.selectByUserId(user.getUserid());
				if(null == sysSession){
					sysSession = new AppSysSession();
					sysSession.setUserId(user.getUserid());
					sysSession.setStartDate(new Date());
					sysSession.setToken(token);
					appSysSessionMapper.insertSelective(sysSession);
				}else{
					sysSession.setStartDate(new Date());
					sysSession.setToken(token);
					appSysSessionMapper.updateByPrimaryKeySelective(sysSession);
				}
				detail.put("token", token);
				return detail;
			} else {
				AppSysLoginLog log = new AppSysLoginLog();
				log.setStatus("1");
				log.setUserCode(userCode);

				log.setDeviceType(deviceType);
				log.setDeviceId(deviceId);
				log.setAddress(client_ip);
				appSysLoginLogMapper.insertSelective(log);
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "手机号和密码配对失败");
				return detail;
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "账户不存在，请先注册！");
			return detail;
		}

	}

	/**
	 * 快捷登录
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject fastLogin(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userCode = JsonUtil.getJStringAndCheck(params, "userCode", null, false, detail);
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, false, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
		
		String deviceType =JsonUtil.getJStringAndCheck(params, "deviceType", null, true, detail);
		String deviceId=JsonUtil.getJStringAndCheck(params, "deviceId", null, false, detail);
		String client_ip=JsonUtil.getJStringAndCheck(params, "client_ip", null, false, detail);
	
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
		String inj_stra[] = inj_str.split("\\|");
		for (int i = 0; i < inj_stra.length; i++) {
			if (userCode.indexOf(inj_stra[i]) != -1) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "账号含有非法字符");
				return detail;
			}
		}
		AppLogin appLogin = appLoginMapper.selectByPhone(userCode);
		if (null != appLogin) {
			Map<String, Object> map0 = new HashMap<>();
			if("1".equals(type)||"5".equals(type)){
				type = "1,5";
			}
			map0.put("phone",userCode);
			map0.put("status", type.split(","));
			map0.put("valicode", valiCode);
			AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatusCode(map0);
			if (appPhoneValicode != null) {
				Date creatTime = appPhoneValicode.getCreatTime();
				Date d = new Date();
				long nowTime = d.getTime();
				long longDate = creatTime.getTime();
				if (nowTime - longDate < 180000) {
					String code = appPhoneValicode.getValicode();
					if (code.equals(valiCode)) {
						//ios的设备信息不用发给应用宝，只发安卓的
						if("1".equals(deviceType)) {
//							yybMsgProducer.sendMessage(Constants.yyb_mq_topic, Constants.yyb_mq_tag, params.toJSONString());
						}
						AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(userCode);
						if("".equals(StringUtil.nvl(appLoanAppl.getInviteCode()))){
							appLoanAppl.setInviteCode( InvitationCodeUtil.toSerialCode((long)appLoanAppl.getUserId()));
							appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
						}
						AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
						if (null != appLoanCtm.getCustomName() && !"".equals(appLoanCtm.getCustomName())) {
							detail.put("customName", appLoanCtm.getCustomName());
						} else {
							detail.put("customName", "随心花");
						}
						AppUser user = appUserMapper.selectByPhone(userCode);
						detail.put("phone", userCode);
						detail.put("userId", StringUtil.toString(user.getMch_version()));
						detail.put("orgId", StringUtil.toString(user.getOrgId()));
						AppSysLoginLog log = new AppSysLoginLog();
						log.setStatus("0");
						log.setUserCode(userCode);

						log.setDeviceType(deviceType);
						log.setDeviceId(deviceId);
						log.setAddress(client_ip);
						appSysLoginLogMapper.insertSelective(log);
						String token = UUID.randomUUID().toString().replace("-", "");
						AppSysSession sysSession = appSysSessionMapper.selectByUserId(user.getUserid());
						if(null == sysSession){
							sysSession = new AppSysSession();
							sysSession.setUserId(user.getUserid());
							sysSession.setStartDate(new Date());
							sysSession.setToken(token);
							appSysSessionMapper.insertSelective(sysSession);
						}else{
							sysSession.setStartDate(new Date());
							sysSession.setToken(token);
							appSysSessionMapper.updateByPrimaryKeySelective(sysSession);
						}
						detail.put("token", token);
						if(("").equals(appLogin.getPassword()) || null == appLogin.getPassword()){
							detail.put("isPassword", "0");
						}else{
							detail.put("isPassword", "1");
						}
						return detail;
					} else {
						AppSysLoginLog log = new AppSysLoginLog();
						log.setStatus("1");
						log.setUserCode(userCode);
						log.setDeviceType(deviceType);
						log.setDeviceId(deviceId);
						log.setAddress(client_ip);
						appSysLoginLogMapper.insertSelective(log);
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "手机号与验证码不配对");
						return detail;
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "验证码超时");
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "手机号与验证码不配对");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "账号未注册");
		}
		return detail;
	}

	@Override
	public JSONObject queryUserIdToken(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userCode = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String deviceType = JsonUtil.getJStringAndCheck(params, "deviceType", null, true, detail);
		String deviceId=JsonUtil.getJStringAndCheck(params, "deviceId", null, false, detail);
		String client_ip=JsonUtil.getJStringAndCheck(params, "client_ip", null, false, detail);
				
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		String inj_str = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+|,";
		String inj_stra[] = inj_str.split("\\|");
		for (int i = 0; i < inj_stra.length; i++) {
			if (userCode.indexOf(inj_stra[i]) != -1) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "账号含有非法字符");
				return detail;
			}
		}
		AppLogin isExist = appLoginMapper.selectByPhone(userCode);
		if (null != isExist) {
			AppLogin login = appLoginMapper.selectByPhone(userCode);
			if (login != null) {
				//ios的设备信息不用发给应用宝，只发安卓的
				if("1".equals(deviceType)) {
//					yybMsgProducer.sendMessage(Constants.yyb_mq_topic, Constants.yyb_mq_tag, params.toJSONString());
				}
				AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(login.getUserCode());
				if("".equals(StringUtil.nvl(appLoanAppl.getInviteCode()))){
					appLoanAppl.setInviteCode( InvitationCodeUtil.toSerialCode((long)appLoanAppl.getUserId()));
					appLoanApplMapper.updateByPrimaryKeySelective(appLoanAppl);
				}
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
				if (null != appLoanCtm.getCustomName() && !"".equals(appLoanCtm.getCustomName())) {
					detail.put("customName", appLoanCtm.getCustomName());
				} else {
					detail.put("customName", "随心花");
				}
				AppUser user = appUserMapper.selectByPhone(login.getUserCode());
				detail.put("phone", login.getUserCode());
				detail.put("userId", StringUtil.toString(user.getMch_version()));
				detail.put("orgId", StringUtil.toString(user.getOrgId()));
				AppSysLoginLog log = new AppSysLoginLog();
				log.setStatus("0");
				log.setUserCode(login.getUserCode());
				log.setDeviceType(deviceType);
				log.setDeviceId(deviceId);
				log.setAddress(client_ip);
				appSysLoginLogMapper.insertSelective(log);
				String token = UUID.randomUUID().toString().replace("-", "");
				AppSysSession sysSession = appSysSessionMapper.selectByUserId(user.getUserid());
				if(null == sysSession){
					sysSession = new AppSysSession();
					sysSession.setUserId(user.getUserid());
					sysSession.setStartDate(new Date());
					sysSession.setToken(token);
					appSysSessionMapper.insertSelective(sysSession);
				}else{
					sysSession.setStartDate(new Date());
					sysSession.setToken(token);
					appSysSessionMapper.updateByPrimaryKeySelective(sysSession);
				}
				detail.put("token", token);
				return detail;
			} else {
				AppSysLoginLog log = new AppSysLoginLog();
				log.setStatus("1");
				log.setUserCode(userCode);
				log.setDeviceType(deviceType);
				log.setDeviceId(deviceId);
				log.setAddress(client_ip);
				appSysLoginLogMapper.insertSelective(log);
				detail.put(Consts.RESULT, ErrorCode.TOKEN_FAILED);
				detail.put(Consts.RESULT_NOTE, "请前往登陆");
				return detail;
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "账户不存在，请先注册！");
			return detail;
		}
	}

}
