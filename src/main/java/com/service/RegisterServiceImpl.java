package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoginMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLogin;
import com.model.AppOprLog;
import com.model.AppPhoneValicode;
import com.model.AppUser;
import com.service.intf.LoginService;
import com.service.intf.RegisterService;
import com.util.ErrorCode;
import com.util.InvitationCodeUtil;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service("registerServiceImpl")
public class RegisterServiceImpl implements RegisterService {
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private LoginService loginService;

	/* @author yang.wu
	 * 类名称： RegisterServiceImpl
	 * 创建时间：2017年5月17日 下午4:47:14
	 * @see com.service.intf.RegisterService#register(com.alibaba.fastjson.JSONObject)
	 * 类描述：注册
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject register(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone1 = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);   //用户账号
//		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);   //用户密码
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, true, detail);   //验证码
		String version = JsonUtil.getJStringAndCheck(params, "version", null, true, detail);  //版本号
		String inviterInviteCode = JsonUtil.getJStringAndCheck(params, "inviteCode", null, false, detail);   //邀请人邀请码
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail);   //验证码类型
		if (detail.containsKey(Consts.RESULT)) {
			detail.put("smsFailed", "0");
			return detail;
		}
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone1);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put("smsFailed", "0");
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		String phone = phone1.replace(" ", "");
		AppLogin appLogin = appLoginMapper.selectByPhone(phone);
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
						if(inviterInviteCode.equals("")){
							AppLogin appLogin02 = new AppLogin();
//							appLogin02.setPassword(password);
							appLogin02.setUserCode(phone);
							appLoginMapper.insertSelective(appLogin02);
							AppUser user = new AppUser();
							user.setPhone(phone);
							user.setUserName(phone);
							user.setStatus("2");
							user.setRoleFilter("2");
							user.setJobNum(version);
							user.setLgnId(appLogin02.getLgnid() + "");
							user.setMch_version(StringUtil.MD5(phone + UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()));
							appUserMapper.insertSelective(user);
							AppLoanAppl appLoanAppl = new AppLoanAppl();
							String inviteCode = InvitationCodeUtil.toSerialCode((long)user.getUserid());  //邀请码
							appLoanAppl.setItemCode(phone);
							appLoanAppl.setStatus("0");
							appLoanAppl.setCity(version); //渠道来源
							appLoanAppl.setUserId(user.getUserid()); //userId
							appLoanAppl.setInviteCode(inviteCode);
							appLoanAppl.setInviterInviteCode(inviterInviteCode.replace(" ", ""));
							appLoanApplMapper.insertSelective(appLoanAppl);
							AppLoanCtm appLoanCtm = new AppLoanCtm();
							appLoanCtm.setApprId(appLoanAppl.getId());
							appLoanCtm.setSchedule_status("0");
							appLoanCtmMapper.insertSelective(appLoanCtm);
							AppOprLog appOprLog = new AppOprLog();
							appOprLog.setModuleId("0");
							appOprLog.setOprContent("注册成功！");
							appOprLog.setUserid(StringUtil.toString(user.getUserid()));
							appOprLogMapper.insertSelective(appOprLog);
							detail.put(Consts.RESULT_NOTE, "注册成功！");
						}else{
							AppLoanAppl appLoanAppl02 = appLoanApplMapper.selectByInviterInviteCode(inviterInviteCode.replace(" ", ""));
							if(appLoanAppl02 != null){
								AppLogin appLogin02 = new AppLogin();
//								appLogin02.setPassword(password);
								appLogin02.setUserCode(phone);
								appLoginMapper.insertSelective(appLogin02);
								AppUser user = new AppUser();
								user.setPhone(phone);
								user.setUserName(phone);
								user.setStatus("2");
								user.setRoleFilter("2");
								user.setJobNum(version);
								user.setLgnId(appLogin02.getLgnid() + "");
								user.setMch_version(StringUtil.MD5(phone + UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()));
								appUserMapper.insertSelective(user);
								AppLoanAppl appLoanAppl = new AppLoanAppl();
								String inviteCode = InvitationCodeUtil.toSerialCode((long)user.getUserid());  //邀请码
								appLoanAppl.setItemCode(phone);
								appLoanAppl.setStatus("0");
								appLoanAppl.setCity(version); //渠道来源
								appLoanAppl.setUserId(user.getUserid()); //userId
								appLoanAppl.setInviteCode(inviteCode);
								appLoanAppl.setInviterInviteCode(inviterInviteCode.replace(" ", ""));
								appLoanApplMapper.insertSelective(appLoanAppl);
								AppLoanCtm appLoanCtm = new AppLoanCtm();
								appLoanCtm.setApprId(appLoanAppl.getId());
								appLoanCtm.setSchedule_status("0");
								appLoanCtmMapper.insertSelective(appLoanCtm);
								AppOprLog appOprLog = new AppOprLog();
								appOprLog.setModuleId("0");
								appOprLog.setOprContent("注册成功！");
								appOprLog.setUserid(StringUtil.toString(user.getUserid()));
								appOprLogMapper.insertSelective(appOprLog);
								detail.put(Consts.RESULT_NOTE, "注册成功！");
							}else{
								detail.put("smsFailed", "0");
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "邀请码不存在，请重新确认");
							}
						}
					}else{
						if(!"4".equals(type)&&!"5".equals(type)){
							detail.put("smsFailed", "1");
						}else{
							detail.put("smsFailed", "0");
						}
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码不正确");
					}
				}else{
					if(!"4".equals(type)&&!"5".equals(type)){
						detail.put("smsFailed", "1");
					}else{
						detail.put("smsFailed", "0");
					}
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "当前短信验证码已失效，请重新获取");
				}
			}else{
				if(!"4".equals(type)&&!"5".equals(type)){
					detail.put("smsFailed", "1");
				}else{
					detail.put("smsFailed", "0");
				}
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "手机号与验证码不配对");
			}
		}else{
			detail.put("smsFailed", "0");
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该手机号已注册，可直接登录");
		}
		return detail;
	}
	
	/* @author zw
	 * 类名称： RegisterServiceImpl
	 * @see com.service.intf.RegisterService#register(com.alibaba.fastjson.JSONObject)
	 * 类描述：app注册
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject appRegister(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone1 = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);   //用户账号
//		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);   //用户密码
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, true, detail);   //验证码
		String version = JsonUtil.getJStringAndCheck(params, "version", null, true, detail);  //版本号
		String inviterInviteCode = JsonUtil.getJStringAndCheck(params, "inviteCode", null, false, detail);   //邀请人邀请码
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail);   //验证码类型
		JsonUtil.getJStringAndCheck(params, "deviceType", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			detail.put("smsFailed", "0");
			return detail;
		}
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone1);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put("smsFailed", "0");
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		String phone = phone1.replace(" ", "");
		AppLogin appLogin = appLoginMapper.selectByPhone(phone);
		if(appLogin == null){
			Map<String, Object> map = new HashMap<String, Object>();
			if("0".equals(type)||"4".equals(type)){
				type = "0,4";
			}
			map.put("phone",phone);
			map.put("status", type.split(","));
			map.put("valicode", valiCode);
			AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatusCode(map);
			if(appPhoneValicode != null){
				Date date = new Date();
				long nowTime = date.getTime();  //获取系统时间
				long creatTime = appPhoneValicode.getCreatTime().getTime();  //验证码时间
				if(nowTime - creatTime <= 180000){
					String code = appPhoneValicode.getValicode();
					if(valiCode.equals(code)){
						if(inviterInviteCode.equals("")){
							AppLogin appLogin02 = new AppLogin();
//							appLogin02.setPassword(password);
							appLogin02.setUserCode(phone);
							appLoginMapper.insertSelective(appLogin02);
							AppUser user = new AppUser();
							user.setPhone(phone);
							user.setUserName(phone);
							user.setStatus("2");
							user.setRoleFilter("2");
							user.setJobNum(version);
							user.setLgnId(appLogin02.getLgnid() + "");
							user.setMch_version(StringUtil.MD5(phone + UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()));
							appUserMapper.insertSelective(user);
							AppLoanAppl appLoanAppl = new AppLoanAppl();
							String inviteCode = InvitationCodeUtil.toSerialCode((long)user.getUserid());  //邀请码
							appLoanAppl.setItemCode(phone);
							appLoanAppl.setStatus("0");
							appLoanAppl.setCity(version); //渠道来源
							appLoanAppl.setUserId(user.getUserid()); //userId
							appLoanAppl.setInviteCode(inviteCode);
							appLoanAppl.setInviterInviteCode(inviterInviteCode.replace(" ", ""));
							appLoanApplMapper.insertSelective(appLoanAppl);
							AppLoanCtm appLoanCtm = new AppLoanCtm();
							appLoanCtm.setApprId(appLoanAppl.getId());
							appLoanCtm.setSchedule_status("0");
							appLoanCtmMapper.insertSelective(appLoanCtm);
							AppOprLog appOprLog = new AppOprLog();
							appOprLog.setModuleId("0");
							appOprLog.setOprContent("注册成功！");
							appOprLog.setUserid(StringUtil.toString(user.getUserid()));
							appOprLogMapper.insertSelective(appOprLog);
							detail.put(Consts.RESULT_NOTE, "注册成功！");
							detail = loginService.queryUserIdToken(params);
						}else{
							AppLoanAppl appLoanAppl02 = appLoanApplMapper.selectByInviterInviteCode(inviterInviteCode.replace(" ", ""));
							if(appLoanAppl02 != null){
								AppLogin appLogin02 = new AppLogin();
//								appLogin02.setPassword(password);
								appLogin02.setUserCode(phone);
								appLoginMapper.insertSelective(appLogin02);
								AppUser user = new AppUser();
								user.setPhone(phone);
								user.setUserName(phone);
								user.setStatus("2");
								user.setRoleFilter("2");
								user.setJobNum(version);
								user.setLgnId(appLogin02.getLgnid() + "");
								user.setMch_version(StringUtil.MD5(phone + UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis()));
								appUserMapper.insertSelective(user);
								AppLoanAppl appLoanAppl = new AppLoanAppl();
								String inviteCode = InvitationCodeUtil.toSerialCode((long)user.getUserid());  //邀请码
								appLoanAppl.setItemCode(phone);
								appLoanAppl.setStatus("0");
								appLoanAppl.setCity(version); //渠道来源
								appLoanAppl.setUserId(user.getUserid()); //userId
								appLoanAppl.setInviteCode(inviteCode);
								appLoanAppl.setInviterInviteCode(inviterInviteCode.replace(" ", ""));
								appLoanApplMapper.insertSelective(appLoanAppl);
								AppLoanCtm appLoanCtm = new AppLoanCtm();
								appLoanCtm.setApprId(appLoanAppl.getId());
								appLoanCtm.setSchedule_status("0");
								appLoanCtmMapper.insertSelective(appLoanCtm);
								AppOprLog appOprLog = new AppOprLog();
								appOprLog.setModuleId("0");
								appOprLog.setOprContent("注册成功！");
								appOprLog.setUserid(StringUtil.toString(user.getUserid()));
								appOprLogMapper.insertSelective(appOprLog);
								detail.put(Consts.RESULT_NOTE, "注册成功！");
								detail = loginService.queryUserIdToken(params);
							}else{
								detail.put("smsFailed", "0");
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put(Consts.RESULT_NOTE, "邀请码不存在，请重新确认");
							}
						}
					}else{
						if(!"4".equals(type)&&!"5".equals(type)){
							detail.put("smsFailed", "1");
						}else{
							detail.put("smsFailed", "0");
						}
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "验证码不正确");
					}
				}else{
					if(!"4".equals(type)&&!"5".equals(type)){
						detail.put("smsFailed", "1");
					}else{
						detail.put("smsFailed", "0");
					}
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "当前短信验证码已失效，请重新获取");
				}
			}else{
				if(!"4".equals(type)&&!"5".equals(type)){
					detail.put("smsFailed", "1");
				}else{
					detail.put("smsFailed", "0");
				}
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "手机号与验证码不配对");
			}
		}else{
			detail.put("smsFailed", "0");
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该手机号已注册，可直接登录");
		}
		return detail;
	}

}
