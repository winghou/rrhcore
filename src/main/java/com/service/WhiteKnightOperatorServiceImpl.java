package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppLoanCtmShip;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.DecisionEngineService;
import com.service.intf.WhiteKnightOperatorService;
import com.util.ErrorCode;
import com.util.HttpClientUtil;
import com.util.JsonUtil;
@Service
public class WhiteKnightOperatorServiceImpl implements WhiteKnightOperatorService {

	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private DecisionEngineService decisionEngineService;
	
	
	/**
	  *  Description:白骑士运营商登录授权
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:44:31
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorLogin(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String mobile = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String pwd = JsonUtil.getJStringAndCheck(params, "password", null, true, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, false, detail);
		String smsCode = JsonUtil.getJStringAndCheck(params, "smsCode", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		JSONObject json = new JSONObject();
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		if(mobile.equals("")){
			mobile = appLoanAppl.getItemCode();
		}
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_LOGIN_URL");
		List<IfmBaseDict> list2 = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_PARTNERID");
		String url = list.get(0).getItemValue();
		String partnerId = list2.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/login";
		json.put("reqId", reqId);
		json.put("partnerId", partnerId);
		json.put("name", appLoanCtm.getCustomName());
		json.put("certNo", appLoanCtm.getIdentityCard());
		json.put("mobile", mobile);
		json.put("pwd", pwd);
		json.put("smsCode", smsCode);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			JSONObject data = res.getJSONObject("data");
			if(res.getString("resultCode").equals("CCOM3069") || res.getString("resultCode").equals("CCOM3014")){
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc"));
			    detail.put("reqId", data.getString("reqId"));
			}else if(res.getString("resultCode").equals("CCOM1000")){
				whiteKnightOperatorReportext(appLoanAppl,mobile);
				JSONObject jsonObject = whiteKnightOperatorSaveAccount(appLoanAppl, appLoanCtmCntMapper, appLoanCtmMapper, decisionEngineService, mch, mobile, pwd);
				if(jsonObject.getString("process_code").equals("30000")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "30000");
					detail.put(Consts.RESULT_NOTE, "请重新认证");
					return detail;
				}else{
					detail.put("process_code", res.getString("resultCode"));
				    detail.put("content", res.getString("resultDesc")); 
				}  
			}else if(res.getString("resultCode").equals("CCOM4205") || res.getString("resultCode").equals("CCOM4207") || res.getString("resultCode").equals("CCOM4208")){
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc"));  
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
			    detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}

	
	
	/**
	  *  Description:白骑士运营商重发登录短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:44:34
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorSendLoginSms(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		JSONObject json = new JSONObject();
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_SENDLOGINSMS_URL");
		String url = list.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/sendloginsms";
		json.put("reqId", reqId);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("resultCode").equals("CCOM1000") || res.getString("resultCode").equals("CCOM4207")){
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc"));
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
			    detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}

	
	
	/**
	  *  Description:白骑士运营商重发二次鉴权短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:44:36
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorSendAuthSms(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		JSONObject json = new JSONObject();
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_SENDAUTHSMS_URL");
		String url = list.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/sendauthsms";
		json.put("reqId", reqId);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
		    if(res.getString("resultCode").equals("CCOM1000") || res.getString("resultCode").equals("CCOM4208")){
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc")); 
			}else{
			    detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode")); //自定义流程码
				detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}

	
	
	public JSONObject whiteKnightOperatorReportext(AppLoanAppl appLoanAppl,String mobile) {
		JSONObject detail = new JSONObject();
		JSONObject json = new JSONObject();
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("cntType", "2");
//		map.put("apprId", appLoanAppl.getId());
//		AppLoanCtmCnt appLoanCtmCnt = appLoanCtmCntMapper.queryByType(map);
		List<AppLoanCtmShip> list = appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId()); 
		JSONArray contacts=new JSONArray();
		for(AppLoanCtmShip ship : list){
			JSONObject contacts02 = new JSONObject();
			contacts02.put("contact_tel", ship.getShipCnt());         //联系人电话
			contacts02.put("contact_name", ship.getShipName());        //联系人姓名
			/*1 父母 2 子女 3 夫妻 4 恋人 5 同事 6 同学 7 朋友 8 其他*/
			if(ship.getShipType().equals("0")){    //shipType 0:为第一联系人 1：为第二联系人
				if(ship.getRelationship().equals("配偶")){
					contacts02.put("relation", "3");
				}else{
					contacts02.put("relation", "1");
				}
			}else{
				if(ship.getRelationship().equals("配偶")){
					contacts02.put("relation", "3");
				}else if(ship.getRelationship().equals("父/母")){
					contacts02.put("relation", "1");
				}else if(ship.getRelationship().equals("同事")){
					contacts02.put("relation", "5");
				}else if(ship.getRelationship().equals("朋友")){
					contacts02.put("relation", "7");
				}else{
					contacts02.put("relation", "6");  //将其他类型定为同学
				}
			}      
			contacts.add(contacts02);
		}
		List<IfmBaseDict> list2 = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_REPORTEXT_URL");
		List<IfmBaseDict> list3 = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_PARTNERID");
		String url = list2.get(0).getItemValue();
		String partnerId = list3.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/common/reportext";
		//json.put("partnerId", "yuecaijf");
		json.put("partnerId", partnerId);
		json.put("name", appLoanCtm.getCustomName());
		json.put("certNo", appLoanCtm.getIdentityCard());
		json.put("mobile", mobile);
		json.put("contacts", contacts);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("resultCode").equals("CCOM1000")){ 
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc"));
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
			    detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}

	
	
	/**
	  *  Description:白骑士运营商校验二次鉴权短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:44:42
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorVerifyAuthSms(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, true, detail);
		String smsCode = JsonUtil.getJStringAndCheck(params, "smsCode", null, true, detail);
		String mobile = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);
		String pwd = JsonUtil.getJStringAndCheck(params, "password", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_VERIFYAUTHSMS_URL");
		String url = list.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/verifyauthsms";
		detail.put("reqId", reqId);
		detail.put("smsCode", smsCode);
		String result = HttpClientUtil.doPost2(url,detail,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("resultCode").equals("CCOM1000")){
				whiteKnightOperatorReportext(appLoanAppl,mobile);
				JSONObject jsonObject = whiteKnightOperatorSaveAccount(appLoanAppl, appLoanCtmCntMapper, appLoanCtmMapper, decisionEngineService, mch, mobile, pwd);
				if(jsonObject.getString("process_code").equals("30000")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "30000");
					detail.put(Consts.RESULT_NOTE, "请重新认证");
					return detail;
				}else{
					detail.put("process_code", res.getString("resultCode"));
				    detail.put("content", res.getString("resultDesc")); 
				}  
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
			    detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}



	/**
	  *  Description:白骑士运营商重置服务密码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 下午4:16:16
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorResetpwd(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String mobile = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, false, detail);
		String pwd = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);
		String smsCode = JsonUtil.getJStringAndCheck(params, "smsCode", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		JSONObject json = new JSONObject();
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		if(mobile.equals("")){
			mobile = appLoanAppl.getItemCode();
		}
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_RESETPWD_URL");
		List<IfmBaseDict> list3 = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_PARTNERID");
		String url = list.get(0).getItemValue();
		String partnerId = list3.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/resetpwd";
		json.put("reqId", reqId);
//		json.put("partnerId", "yuecaijf");
		json.put("partnerId", partnerId);
		json.put("name", appLoanCtm.getCustomName());
		json.put("certNo", appLoanCtm.getIdentityCard());
		json.put("mobile", mobile);
		json.put("pwd", pwd);
		json.put("smsCode", smsCode);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
//		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			JSONObject data = res.getJSONObject("data");
			if(res.getString("resultCode").equals("CCOM4211")){
				detail.put("process_code", data.getString("reqId"));
				detail.put("reqId", data.getString("reqId"));
				detail.put("smsCodeFlag", data.getString("smsCodeFlag"));
				detail.put("pwdFlag", data.getString("pwdFlag"));
				detail.put("content", data.getString("pwdTips"));
			}else if(res.getString("resultCode").equals("CCOM1000") || res.getString("resultCode").equals("CCOM4214") || res.getString("resultCode").equals("CCOM4216")){
				detail.put("process_code", res.getString("resultCode"));
				detail.put("content", res.getString("resultDesc"));
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
				detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		//System.out.println(res);
		return detail;
	}

	
	
	/**
	  *  Description:白骑士运营商重发重置密码短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 下午4:20:29
	  *  @param params
	  *  @return
	  *  @throws Exception
	  */
	@Override
	public JSONObject whiteKnightOperatorSendResetpwdSms(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String reqId = JsonUtil.getJStringAndCheck(params, "reqId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		List<IfmBaseDict> list = ifmBaseDictMapper.qryIfmBaseDict("WHITE_KNIGHT_OPERATOR_SENDRESETPWDSMS_URL");
		String url = list.get(0).getItemValue();
		//String url = "https://credit.baiqishi.com/clweb/api/mno/sendresetpwdsms";
		JSONObject json = new JSONObject();
		json.put("reqId", reqId);
		String result = HttpClientUtil.doPost2(url,json,"utf-8",60000);
		JSONObject res =(JSONObject) JSON.parse(result);
//		System.out.println(res);
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("resultCode").equals("CCOM1000")){ 
				detail.put("process_code", res.getString("resultCode"));
			    detail.put("content", res.getString("resultDesc"));
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", res.getString("resultCode"));
			    detail.put(Consts.RESULT_NOTE, res.getString("resultDesc"));
			}
		}
		return detail;
	}
	
	public JSONObject whiteKnightOperatorSaveAccount(AppLoanAppl appLoanAppl,AppLoanCtmCntMapper appLoanCtmCntMapper,AppLoanCtmMapper appLoanCtmMapper,DecisionEngineService decisionEngineService,AppUser mch,String mobile,String pwd) throws Exception{
		JSONObject detail = new JSONObject();
		Map<String , Object> mp=new HashMap<>();
		mp.put("cntType", "2");
		mp.put("apprId", appLoanAppl.getId());
		AppLoanCtmCnt cnt=appLoanCtmCntMapper.queryByType(mp);
		AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
		Date operatorTime = new Date();
		JSONObject params2 = new JSONObject();
		params2.put("userId", mch.getUserid());
		if(null == cnt){
			appLoanCtmCnt.setCntCommt(mobile);
			appLoanCtmCnt.setCntPass(pwd);
			appLoanCtmCnt.setApprId(appLoanAppl.getId());
			appLoanCtmCnt.setCntType("2");
			appLoanCtmCnt.setCntDesc("2");
			appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
			AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			ctm.setSchedule_status("8");
			ctm.setOperatorTime(operatorTime);
			appLoanCtmMapper.updateByPrimaryKeySelective(ctm);
			JSONObject jsonObject = decisionEngineService.checkReportExsit(params2);
			if(!jsonObject.getString("result").equals("0")){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put("process_code", "30000");
				detail.put(Consts.RESULT_NOTE, "请重新认证");
				return detail;
			}
			detail.put("process_code", "10086");
		}else{
			appLoanCtmCnt.setId(cnt.getId());
			appLoanCtmCnt.setCntCommt(mobile);
			appLoanCtmCnt.setCntPass(pwd);
			appLoanCtmCnt.setApprId(appLoanAppl.getId());
			appLoanCtmCnt.setCntType("2");
			appLoanCtmCnt.setCntDesc("2");
			appLoanCtmCntMapper.updateByPrimaryKeySelective(appLoanCtmCnt);
			AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			ctm.setSchedule_status("8");
			ctm.setOperatorTime(operatorTime);
			appLoanCtmMapper.updateByPrimaryKeySelective(ctm);
			if(!(("1").equals(appLoanAppl.getZhimaStatus()) || ("1").equals(appLoanAppl.getBaseInfoStatus()) || ("1").equals(appLoanAppl.getContactInfoStatus()))){
				JSONObject jsonObject = decisionEngineService.checkReportExsit(params2);
				if(!jsonObject.getString("result").equals("0")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "30000");
					detail.put(Consts.RESULT_NOTE, "请重新认证");
					return detail;
				}
			}
			detail.put("process_code", "10086");
		}
		return detail;
	}

}
