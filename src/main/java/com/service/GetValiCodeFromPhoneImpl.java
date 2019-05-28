package com.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoginMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLogin;
import com.model.AppPhoneValicode;
import com.model.AppUser;
import com.model.SmsBean;
import com.service.intf.GetValiCodeFromPhoneService;
import com.util.Constants;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
import com.util.URLInvoke;

@Service
public class GetValiCodeFromPhoneImpl implements GetValiCodeFromPhoneService {

	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;

	/**
	 * 添加验证码类型 0：注册 1：快捷登录 2：忘记密码 3：绑定银行卡  4:语音验证码注册 5:语音验证码快捷登录
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject getValiCodeFromPhone(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);

		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		if(-1 != phone.indexOf(" ")){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号不能有空格");
			return detail;
		}
		if(!"0".equals(type) && !"1".equals(type) && !"2".equals(type) && !"3".equals(type)&&!"8".equals(type)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "短信类型错误");
			return detail;
		}
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		if("0".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null != appLogin || null != appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码已注册，请登录");
				return detail;
			}
		}
		if("1".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null == appLogin || null == appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码未注册，请先注册");
				return detail;
			}
		}
		if("2".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null == appLogin || null == appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码未注册，请先注册");
				return detail;
			}
		}
		
		Map<String, Object> map0 = new HashMap<>();
		map0.put("phone", phone);
		map0.put("status", type);
		AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map0);
		List<Map<String, Object>> ifmBaseDicts = ifmBaseDictMapper.selectBaseDict("OUT_WEB_SMS");
		Map<String, Object> map = ifmBaseDicts.get(0);
		String url = StringUtil.toString(map.get("ITEM_VALUE"));
		SmsBean bean = new SmsBean();
		// 区分短信发送模板
		String sendId = "";
		if("0".equals(type)){
			sendId = "20";
		}else if("1".equals(type)){
			sendId = "21";
		}else if("2".equals(type)){
			sendId = "22";
		}else if("3".equals(type)){
			sendId = "23";
		}else if("8".equals(type)){
			List<Map<String, Object>> ifmBase = ifmBaseDictMapper.selectBaseDict("AGREEMENT_PAY_SMS_ID");
			String smsId = StringUtil.toString(ifmBase.get(0).get("ITEM_VALUE"));
			sendId = smsId;
		}
		bean.setId(sendId);
		if (appPhoneValicode != null) {
			if((null == appPhoneValicode.getCount() ? 0 : appPhoneValicode.getCount()) >= 5){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您当天的短信验证码请求次数已达上限，请次日再进行操作！");
				return detail;
			}
			Date creatTime = appPhoneValicode.getCreatTime();
			Date d = new Date();
			long nowTime = d.getTime();
			long longDate = creatTime.getTime();
			int random = (new Random()).nextInt(8999) + 1000;
			if (nowTime - longDate < 180000&&nowTime - longDate>0) {
				random = Integer.parseInt(StringUtil.nvl(appPhoneValicode.getValicode()));
			}
			List<SmsBean.SmsParams> mutils = new ArrayList<SmsBean.SmsParams>();
			appPhoneValicode.setValicode(StringUtil.toString(random));
			Map<String, String> maps = new HashMap<String, String>();
			SmsBean.SmsParams sp = new SmsBean.SmsParams();
			sp.setTo(phone);
			maps.put("code", StringUtil.toString(random));
			sp.setVars(maps);
			mutils.add(sp);
			bean.setMulti(mutils);
			String reString = URLInvoke.postForJson(url + "/" + Constants.SMS_METHOD,
					JsonUtil.covertSms(bean).toJSONString());
			JSONObject restring = (JSONObject) JSON.parse(reString);
			if ("Success".equals(restring.get("resultNote"))) {
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("message", restring);
			} else {
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("resultNote", "短信发送失败，请联系SUBMAIL.CN查询短信发送情况！");
			}
			appPhoneValicode.setCount((null == appPhoneValicode.getCount() ? 0 : appPhoneValicode.getCount()) + 1);
			if (nowTime - longDate >= 180000||nowTime - longDate<=0) {
				appPhoneValicode.setCreatTime(new Date());
			}
			appPhoneValicodeMapper.updateByPrimaryKeySelective(appPhoneValicode);
		} else {
			List<SmsBean.SmsParams> mutils = new ArrayList<SmsBean.SmsParams>();
			AppPhoneValicode PhoneValicode = new AppPhoneValicode();
			int random = (new Random()).nextInt(8999) + 1000;
			PhoneValicode.setValicode(StringUtil.toString(random));
			Map<String, String> maps = new HashMap<String, String>();
			SmsBean.SmsParams sp = new SmsBean.SmsParams();
			sp.setTo(phone);
			maps.put("code", StringUtil.toString(random));
			sp.setVars(maps);
			mutils.add(sp);
			bean.setMulti(mutils);
			String reString = URLInvoke.postForJson(url + "/" + Constants.SMS_METHOD,
					JsonUtil.covertSms(bean).toJSONString());
			JSONObject restring = (JSONObject) JSON.parse(reString);
			if ("Success".equals(restring.get("resultNote"))) {
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("message", restring);
			} else {
				detail.put(Consts.RESULT, ErrorCode.SUCCESS);
				detail.put("resultNote", "短信发送失败，请联系SUBMAIL.CN查询短信发送情况！");
			}
			PhoneValicode.setPhone(phone);
			PhoneValicode.setStatus(type);
			PhoneValicode.setCount(1);
			PhoneValicode.setCreatTime(new Date());
			appPhoneValicodeMapper.insertSelective(PhoneValicode);
		}
		return detail;
	}

	/**
	 * 4:语音验证码注册 5:语音验证码快捷登录
	 */
	@Override
	public JSONObject getVoiceMsgFromPhone(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		//4:语音验证码注册 5:语音验证码快捷登录
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);

		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		if(-1 != phone.indexOf(" ")){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号不能有空格");
			return detail;
		}
		if(!"4".equals(type) && !"5".equals(type)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "类型错误");
			return detail;
		}
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		if("4".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null != appLogin || null != appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码已注册，请前往登录");
				return detail;
			}
		}
		if("5".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null == appLogin || null == appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码未注册，请先注册");
				return detail;
			}
		}
		
		Map<String, Object> map0 = new HashMap<>();
		map0.put("phone", phone);
		map0.put("status", type);
		AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map0);
		List<Map<String, Object>> ifmBaseDicts = ifmBaseDictMapper.selectBaseDict("OUT_WEB_SMS");
		Map<String, Object> map = ifmBaseDicts.get(0);
		String url = StringUtil.toString(map.get("ITEM_VALUE"));
		SmsBean bean = new SmsBean();
		// 区分短信发送模板
		List<Map<String, Object>> ifmBases = ifmBaseDictMapper.selectBaseDict("SMS_TEMPLATE_ID");
		String sendId = "";
		if("4".equals(type)){
			sendId = StringUtil.toString(ifmBases.get(0).get("ITEM_VALUE"));
		}else if("5".equals(type)){
			sendId = StringUtil.toString(ifmBases.get(0).get("ITEM_VALUE"));;
		}
		bean.setId(sendId);
		if (appPhoneValicode != null) {
			List<Map<String, Object>> bases = ifmBaseDictMapper.selectBaseDict("SMS_VOICE_NUM");
			if((null == appPhoneValicode.getCount() ? 0 : appPhoneValicode.getCount()) >= Integer.parseInt("".equals(StringUtil.nvl(bases.get(0).get("ITEM_VALUE")))?"3":StringUtil.nvl(bases.get(0).get("ITEM_VALUE")))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				if("4".equals(type)){
					detail.put(Consts.RESULT_NOTE, "您当天的语音验证次数已达上限，请更换手机号码");
				}else if("5".equals(type)){
					detail.put(Consts.RESULT_NOTE, "您当天的语音验证次数已达上限，请次日再验证");
				}
				return detail;
			}
			Date creatTime = appPhoneValicode.getCreatTime();
			Date d = new Date();
			long nowTime = d.getTime();
			long longDate = creatTime.getTime();
			int random = (new Random()).nextInt(8999) + 1000;
			if (nowTime - longDate < 180000&&nowTime - longDate>0) {
				random = Integer.parseInt(StringUtil.nvl(appPhoneValicode.getValicode()));
			}
			appPhoneValicode.setValicode(StringUtil.toString(random));
			bean.setTo(phone);
			bean.setCode(StringUtil.toString(random));
			String reString = URLInvoke.postForJson(url + "/" + Constants.SMS_VOICE_METHOD,
					JsonUtil.covertSms(bean).toJSONString());
			if("".equals(reString)||null==reString){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("resultNote", "由于网络原因导致语音验证码发送失败，请稍后重试");
				return detail;
			}
			JSONObject restring = (JSONObject) JSON.parse(reString);
			if ("Success".equalsIgnoreCase(restring.getString("resultNote"))) {
				appPhoneValicode.setCount((null == appPhoneValicode.getCount() ? 0 : appPhoneValicode.getCount()) + 1);
				if (nowTime - longDate >= 180000||nowTime - longDate<=0) {
					appPhoneValicode.setCreatTime(new Date());
				}
				appPhoneValicodeMapper.updateByPrimaryKeySelective(appPhoneValicode);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("resultNote", "由于网络原因导致语音验证码发送失败，请稍后重试");
			}
		} else {
			AppPhoneValicode PhoneValicode = new AppPhoneValicode();
			int random = (new Random()).nextInt(8999) + 1000;
			PhoneValicode.setValicode(StringUtil.toString(random));
			bean.setTo(phone);
			bean.setCode(StringUtil.toString(random));
			String reString = URLInvoke.postForJson(url + "/" + Constants.SMS_VOICE_METHOD,
					JsonUtil.covertSms(bean).toJSONString());
			if("".equals(reString)||null==reString){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("resultNote", "由于网络原因导致语音验证码发送失败，请稍后重试");
				return detail;
			}
			JSONObject restring = (JSONObject) JSON.parse(reString);
			if ("Success".equalsIgnoreCase(restring.getString("resultNote"))) {
				PhoneValicode.setPhone(phone);
				PhoneValicode.setStatus(type);
				PhoneValicode.setCount(1);
				PhoneValicode.setCreatTime(new Date());
				appPhoneValicodeMapper.insertSelective(PhoneValicode);
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("resultNote", "由于网络原因导致语音验证码发送失败，请稍后重试");
			}
		}
		return detail;
	}

	/**
	 * 语音验证码提示语
	 */
	@Override
	public JSONObject getVoiceMsgClues(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		//4:语音验证码注册 5:语音验证码快捷登录
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
		
		if("0".equals(type)){
			type = "4";
		}
		if("1".equals(type)){
			type = "5";
		}
		
		if(-1 != phone.indexOf(" ")){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号不能有空格");
			return detail;
		}
		if(!"4".equals(type) && !"5".equals(type)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "类型错误");
			return detail;
		}
		String regex = "[1]{1}\\d{10}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone);
		boolean flag = matcher.matches();
		if(!flag){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "手机号格式不正确");
			return detail;
		}
		if("4".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null != appLogin || null != appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码已注册，请前往登录");
				return detail;
			}
		}
		if("5".equals(type)){
			AppLogin appLogin = appLoginMapper.selectByPhone(phone);
			AppUser appUser = appUserMapper.selectByPhone(phone);
			if(null == appLogin || null == appUser){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该手机号码未注册，请先注册");
				return detail;
			}
		}
		
		Map<String, Object> map0 = new HashMap<>();
		map0.put("phone", phone);
		map0.put("status", type);
		AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map0);
		List<Map<String, Object>> bases = ifmBaseDictMapper.selectBaseDict("SMS_VOICE_NUM");
		if(appPhoneValicode!=null&&(null == appPhoneValicode.getCount() ? 0 : appPhoneValicode.getCount()) >= Integer.parseInt("".equals(StringUtil.nvl(bases.get(0).get("ITEM_VALUE")))?"3":StringUtil.nvl(bases.get(0).get("ITEM_VALUE")))){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			if("4".equals(type)){
				detail.put(Consts.RESULT_NOTE, "您当天的语音验证次数已达上限，请更换手机号码");
			}else if("5".equals(type)){
				detail.put(Consts.RESULT_NOTE, "您当天的语音验证次数已达上限，请次日再验证");
			}
		}else{
			List<Map<String, Object>> ifmBaseDicts = ifmBaseDictMapper.selectBaseDict("SMS_VOICE_CLUES");
			Map<String, Object> map = ifmBaseDicts.get(0);
			String clues = StringUtil.toString(map.get("ITEM_VALUE"));
			detail.put("clues", clues);
		}
		return detail;
	}

}