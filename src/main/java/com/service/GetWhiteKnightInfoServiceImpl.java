package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppUserMapper;
import com.dao.AppWhiteKnightInfoMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppUser;
import com.model.AppWhiteKnightInfo;
import com.service.intf.GetWhiteKnightInfoService;
import com.service.intf.MorphoLoanInfoService;
import com.util.ErrorCode;
import com.util.JsonUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class GetWhiteKnightInfoServiceImpl implements GetWhiteKnightInfoService {
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	AppWhiteKnightInfoMapper appWhiteKnightInfoMapper;
	@Autowired
	AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private MorphoLoanInfoService loanInfoService;
	/* @author yang.wu
	 * 类名称： GetWhiteKnightInfoServiceImpl
	 * 创建时间：2017年9月5日 下午2:13:43
	 * @see com.service.intf.GetWhiteKnightInfoService#getWhiteKnightInfo(com.alibaba.fastjson.JSONObject)
	 * 类描述：白骑士信息
	 */
	@Override
	public JSONObject getWhiteKnightInfo(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String tokenKey = JsonUtil.getJStringAndCheck(params, "tokenKey", null, true, detail);
		String mac = JsonUtil.getJStringAndCheck(params, "mac", null, false, detail);
		String idfa = JsonUtil.getJStringAndCheck(params, "idfa", null, false, detail);
		String imei = JsonUtil.getJStringAndCheck(params, "imei", null, false, detail);
		String ip = JsonUtil.getJStringAndCheck(params, "ip", null, false, detail);
		String longitude = JsonUtil.getJStringAndCheck(params, "longitude", null, false, detail);
		String latitude = JsonUtil.getJStringAndCheck(params, "latitude", null, false, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
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
		if(null != appLoanAppl){
			AppWhiteKnightInfo appWhiteKnightInfo = new AppWhiteKnightInfo();
			appWhiteKnightInfo.setApprId(appLoanAppl.getId());
			appWhiteKnightInfo.setImei(imei);
			appWhiteKnightInfo.setIdfa(idfa);
			appWhiteKnightInfo.setIp(ip);
			appWhiteKnightInfo.setLatitude(latitude);
			appWhiteKnightInfo.setMac(mac);
			appWhiteKnightInfo.setTokenKey(tokenKey);
			appWhiteKnightInfo.setLongitude(longitude);
			if(!"".equals(type)){
				String address = JsonUtil.getJStringAndCheck(params, "address", null, false, detail);
				String phoneModel = JsonUtil.getJStringAndCheck(params, "phoneModel", null, false, detail);
				String verCode = JsonUtil.getJStringAndCheck(params, "verCode", null, false, detail);
				JSONArray appList = params.getJSONArray("appList");
				appWhiteKnightInfo.setPhone_model(phoneModel);
				appWhiteKnightInfo.setAddress(address);
				appWhiteKnightInfo.setVer_code(verCode);
				appWhiteKnightInfo.setApp_list(appList.toJSONString());
			}
			AppWhiteKnightInfo appWhiteKnightInfo2 = appWhiteKnightInfoMapper.selectByApprId(appLoanAppl.getId());
			if(null == appWhiteKnightInfo2){
				appWhiteKnightInfoMapper.insertSelective(appWhiteKnightInfo);
			}else{
				appWhiteKnightInfoMapper.updateByApprIdSelective(appWhiteKnightInfo);
			}
			
			//调用闪蝶共享数据
			JSONObject loanInfo = loanInfoService.loanInfo(idfa,appLoanAppl,appWhiteKnightInfo);
			if(loanInfo.get("success")==null || !"1".equals(loanInfo.get("success"))) {
				System.out.println("用户："+userId+"闪蝶共享数据获取错误,错误信息："+loanInfo.get("resultNote"));
			}
			
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
		}
		return detail;
	}

}
