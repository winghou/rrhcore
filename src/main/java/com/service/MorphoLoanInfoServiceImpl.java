package com.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.config.MorphoCash;
import com.dao.AppLoanCtmMapper;
import com.dao.AppMorphoBlackListMapper;
import com.dao.AppMorphoLoanInfoMapper;
import com.dao.AppMorphoVelocityCheckMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppMorphoBlackList;
import com.model.AppMorphoLoanInfo;
import com.model.AppMorphoVelocity;
import com.model.AppWhiteKnightInfo;
import com.service.intf.MorphoLoanInfoService;
import com.util.ErrorCode;
import com.util.HttpClientUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class MorphoLoanInfoServiceImpl implements MorphoLoanInfoService{

	@Autowired
	AppUserMapper appUserMapper;	
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppMorphoBlackListMapper appMorphoBlacklistMapper;
	@Autowired
	private AppMorphoLoanInfoMapper appMorphoLoanInfoMapper;
	@Autowired
	private AppMorphoVelocityCheckMapper appMorphoVelocityCheckMapper;
	/* 
	 * 获取闪蝶共享数据
	 * 
	 */
	
	/* (non-Javadoc)
	 * @see com.service.intf.MorphoLoanInfoService#loanInfo(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public JSONObject loanInfo(String idfa,AppLoanAppl loanAppl,AppWhiteKnightInfo appWhiteKnightInfo) {
		JSONObject detail = new JSONObject();
		//String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail); 
		//int loan_type = JsonUtil.getJIntAndCheck(params, "loan_type", null, true, detail); 
/*		//验证用户登录
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null == mch) {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		
		userId = mch.getUserid() + "";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if(null == appUser){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服");
			return detail;
		}
*/		
		//AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
		//if(null != loanAppl){
			AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
			String name = loanCtm.getCustomName();
			String identityCard = loanCtm.getIdentityCard();
			String mobile = loanAppl.getItemCode();
			String home_address=loanCtm.getBdrAddr();
			//用户信息
			if(StringUtils.isBlank(name)){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该用户没有名字信息");
				return detail;
			}
			
			if(StringUtils.isBlank(name)){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "该用户没有身份证信息");
				return detail;
			}
			detail.put("name", name);
			detail.put("pid", identityCard);
			detail.put("mobile", mobile);
			detail.put("home_address", home_address);//非必填
		//}
		
		detail.put("loan_type", 1);//现金贷 写死
		detail.put("sub_tenant", "");//这个参数是针对一家机构有多个子品牌的,可以不填
		
		//设备信息
		JSONObject device=new JSONObject();
		device.put("device_type", "");//非必填
		device.put("imei", appWhiteKnightInfo.getImei());//安卓系统必填
		device.put("keychain_uuid", "");//iOS系统上,必填一个
		device.put("idfa", idfa);//iOS系统上,必填一个
		device.put("idfv", "");//iOS系统上,必填一个
		device.put("mac", "");//N
		device.put("ip_address", appWhiteKnightInfo.getIp());//必填
		device.put("gps_longitude", appWhiteKnightInfo.getLongitude());
		device.put("gps_latitude", appWhiteKnightInfo.getLatitude());
		device.put("os_name", "");//非必填
		device.put("os_version", "");//非必填
		device.put("serial_number", "");//非必填
		device.put("hd_serial_number", "");//非必填
		device.put("browser_name", "");//非必填
		device.put("browser_version", "");//非必填
		detail.put("device", device);
		
		
		String doPostJson = HttpClientUtil.doPostJson(MorphoCash.morphoUrl, detail.toJSONString(),MorphoCash.auth);
		
		JSONObject parseObject = JSON.parseObject(doPostJson);
		
		//保存数据到数据库
		if(parseObject.getJSONObject("loanInfo")!=null) {
			Integer apprId=appWhiteKnightInfo.getApprId();
			String loanInfo = parseObject.getJSONObject("loanInfo").toString();
			String blacklist = parseObject.getJSONObject("blacklist").toString();
			String velocityCheck = parseObject.getJSONArray("velocityCheck").toString();
			Date date = new Date();
			AppMorphoLoanInfo MorphoLoanInfo = appMorphoLoanInfoMapper.selectByApprId(apprId);
			if(MorphoLoanInfo!=null) {
				MorphoLoanInfo.setLoanInfo(loanInfo);
				MorphoLoanInfo.setUpdateDate(date);
				appMorphoLoanInfoMapper.updateByPrimaryKeySelective(MorphoLoanInfo);
			}else {
				AppMorphoLoanInfo mli=new AppMorphoLoanInfo();
				mli.setApprId(apprId);
				mli.setLoanInfo(loanInfo);
				mli.setCreateDate(date);
				appMorphoLoanInfoMapper.insertSelective(mli);
			}
			AppMorphoBlackList MorphoBlackList = appMorphoBlacklistMapper.selectByApprId(apprId);
			if(MorphoBlackList!=null){
				MorphoBlackList.setBlackList(blacklist);
				MorphoBlackList.setUpdateDate(date);
				appMorphoBlacklistMapper.updateByPrimaryKeySelective(MorphoBlackList);
			}else {
				AppMorphoBlackList abl=new AppMorphoBlackList();
				abl.setApprId(apprId);
				abl.setBlackList(blacklist);
				abl.setCreateDate(date);
				appMorphoBlacklistMapper.insertSelective(abl);
			}
			AppMorphoVelocity MorphoVelocity = appMorphoVelocityCheckMapper.selectByApprId(apprId);
			if(MorphoVelocity!=null){
				MorphoVelocity.setVelocityCheck(velocityCheck);
				MorphoVelocity.setUpdateDate(date);
				appMorphoVelocityCheckMapper.updateByPrimaryKeySelective(MorphoVelocity);
			}else {
				AppMorphoVelocity amv=new AppMorphoVelocity();
				amv.setApprId(apprId);
				amv.setVelocityCheck(velocityCheck);
				amv.setCreateDate(date);
				appMorphoVelocityCheckMapper.insertSelective(amv);
			}
			
			JSONObject success=new JSONObject();
			success.put("success", "1");
			return success;
		}else {
			return parseObject;
		}
		
	}



}
