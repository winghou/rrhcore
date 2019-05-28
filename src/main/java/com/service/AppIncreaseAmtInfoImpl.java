package com.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppIncreaseAmtInfoMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppIncreaseAmtInfo;
import com.model.AppLoanAppl;
import com.model.AppUser;
import com.service.intf.AppIncreaseAmtInfoService;
import com.util.ErrorCode;
import com.util.JsonUtil;
@Service
public class AppIncreaseAmtInfoImpl implements AppIncreaseAmtInfoService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppIncreaseAmtInfoMapper appIncreaseAmtInfoMapper;
	/**
	 * @author yang.wu
	 * @Description: 获取提额资料状态
	 * @date 2017年7月20日下午11:29:37
	 * @see com.service.intf.AppIncreaseAmtInfoService#getIncreaseAmtInfoStatus(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public JSONObject getIncreaseAmtInfoStatus(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //用户id
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appl = appLoanApplMapper.selectByUserId(mch.getUserid());
		if(appl.getCreditAmt() == null || appl.getCreditAmt().equals("")){
			detail.put("creditAmt", "0.00");
		}else{
			double creditAmt = Double.parseDouble(appl.getCreditAmt());
			String creditAmt2 = String.format("%.2f", creditAmt);
			detail.put("creditAmt", creditAmt2);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("appr_id", appl.getId());
		map.put("type", "0");
		AppIncreaseAmtInfo appIncreaseAmtInfo = appIncreaseAmtInfoMapper.selectByApprIdAndType(map);
		if(appIncreaseAmtInfo == null){
			detail.put("qqStatus", "0");
		}else{
			detail.put("qqStatus", "1");
		}
		Map<String,Object> map2 = new HashMap<String,Object>();
		map2.put("appr_id", appl.getId());
		map2.put("type", "1");
		AppIncreaseAmtInfo appIncreaseAmtInfo2 = appIncreaseAmtInfoMapper.selectByApprIdAndType(map2);
		if(appIncreaseAmtInfo2 == null){
			detail.put("weChatStatus", "0");
		}else{
			detail.put("weChatStatus", "1");
		}
		return detail;
	}
	/**
	 * @author yang.wu
	 * @Description: 提额资料授权
	 * @date 2017年7月21日上午12:06:49
	 * @see com.service.intf.AppIncreaseAmtInfoService#qqAccredit(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public JSONObject increaseAmtInfoAccredit(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //用户id
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail); //授权类型  0 qq 1 微信
		String city = JsonUtil.getJStringAndCheck(params, "city", null, false, detail);
		String province = JsonUtil.getJStringAndCheck(params, "province", null, false, detail);
		String country = JsonUtil.getJStringAndCheck(params, "country", null, false, detail);
		String gender = JsonUtil.getJStringAndCheck(params, "gender", null, false, detail);
		String name = JsonUtil.getJStringAndCheck(params, "name", null, false, detail);
		String image_url = JsonUtil.getJStringAndCheck(params, "image_url", null, false, detail);
		String uid = JsonUtil.getJStringAndCheck(params, "uid", null, false, detail);
		String expires_in = JsonUtil.getJStringAndCheck(params, "expires_in", null, false, detail);
		String access_toke = JsonUtil.getJStringAndCheck(params, "access_toke", null, false, detail);
		String openid = JsonUtil.getJStringAndCheck(params, "openid", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appl = appLoanApplMapper.selectByUserId(mch.getUserid());
		AppIncreaseAmtInfo appIncreaseAmtInfo2 = new AppIncreaseAmtInfo();
		appIncreaseAmtInfo2.setAppr_id(appl.getId());
		appIncreaseAmtInfo2.setType(type);
		appIncreaseAmtInfo2.setCity(city);
		appIncreaseAmtInfo2.setProvince(province);
		appIncreaseAmtInfo2.setCountry(country);
		if(gender.equals("男")||gender.equals("m")||gender.equals("1")){
			appIncreaseAmtInfo2.setGender("男");
		}else{
			appIncreaseAmtInfo2.setGender("女");
		}
		appIncreaseAmtInfo2.setName(name);
		appIncreaseAmtInfo2.setUid(uid);
		appIncreaseAmtInfo2.setImage_url(image_url);
		appIncreaseAmtInfo2.setExpires_in(expires_in);
		appIncreaseAmtInfo2.setAccess_token(access_toke);
		appIncreaseAmtInfo2.setOpenid(openid);
		if(type.equals("0")){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("appr_id", appl.getId());
			map.put("type", "0");
			AppIncreaseAmtInfo appIncreaseAmtInfo = appIncreaseAmtInfoMapper.selectByApprIdAndType(map);
			if(appIncreaseAmtInfo ==null){
				appIncreaseAmtInfoMapper.insertSelective(appIncreaseAmtInfo2);
			}else{
				appIncreaseAmtInfoMapper.updateByApprIdAndType(appIncreaseAmtInfo2);
			}
			detail.put(Consts.RESULT_NOTE, "授权成功");
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("appr_id", appl.getId());
			map.put("type", "1");
			AppIncreaseAmtInfo appIncreaseAmtInfo = appIncreaseAmtInfoMapper.selectByApprIdAndType(map);
			if(appIncreaseAmtInfo ==null){
				appIncreaseAmtInfoMapper.insertSelective(appIncreaseAmtInfo2);
			}else{
				appIncreaseAmtInfoMapper.updateByApprIdAndType(appIncreaseAmtInfo2);
			}
			detail.put(Consts.RESULT_NOTE, "授权成功");
		}
		
		return detail;
	}
}
