package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoginMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppSchoolNameMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppLogin;
import com.model.AppOprLog;
import com.model.AppSchoolName;
import com.model.AppUser;
import com.service.intf.SaveOrUpdatePersonInfo;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
public class SaveOrUpdatePersonImpl implements SaveOrUpdatePersonInfo {
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoginMapper appLoginMapper;
	@Autowired
	private AppSchoolNameMapper appSchoolNameMapper;
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	
	@Override
	public JSONObject QueryPersonInfo(JSONObject params) {
		JSONObject detail=new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
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
		AppUser user=appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLogin login=appLoginMapper.selectByPrimaryKey(Integer.parseInt(user.getLgnId()));
		AppLoanAppl appLoanAppl=appLoanApplMapper.selectByitemCode(login.getUserCode());
		AppLoanCtm appLoanCtm=appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		if(null!=appLoanCtm){
			detail.put("customName", appLoanCtm.getCustomName());
			detail.put("identityCard", appLoanCtm.getIdentityCard());
			detail.put("shcoolName", appLoanCtm.getSchoolName());
			detail.put("place", appLoanCtm.getPlace());
			detail.put("bdrAddr", appLoanCtm.getBdrAddr());
			detail.put("entraDate", StringUtil.toString(DateUtil.format(appLoanCtm.getEntraDate(), "yyyy-MM-dd")));
			detail.put("educational", appLoanCtm.getEducational());
			detail.put("grade", appLoanCtm.getGrade());
			//detail.put("address", appLoanCtm.getAddress());
		}else{
			detail.put("customName", "");
			detail.put("identityCard", "");
			detail.put("shcoolName", "");
			detail.put("place", "");
			detail.put("bdrAddr", "");
			detail.put("entraDate", "");
			detail.put("educational", "");
			detail.put("grade","");
			//detail.put("address", "");
			detail.put(Consts.RESULT_NOTE, "未填写个人信息！");
		}
		
		return detail;
	}
	
	/**
	 * 查询学校信息
	 */
	@Override
	public JSONObject QuerySchool(JSONObject params) {
		JSONObject detail=new JSONObject();
		String dataType=JsonUtil.getJStringAndCheck(params, "dataType", null, false, detail);
		String keyValue=JsonUtil.getJStringAndCheck(params, "keyValue", null, false, detail);
		if(null!=dataType&&!dataType.equals("")){
			List<AppSchoolName> schoolNames=appSchoolNameMapper.selectSchool(keyValue);
			if(null!=schoolNames&&schoolNames.size()>0){
			JSONArray details=new JSONArray();
			for (AppSchoolName s : schoolNames) {
				JSONObject jo=new JSONObject();
				jo.put("schoolName", s.getSchoolname());
				jo.put("parentId", StringUtil.toString(s.getId()));
				//jo.put("parentId", s.getId());
				details.add(jo);
				}
			detail.put("dataList", details);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "未查到此大学！");
			}
		}else{
			AppSchoolName places=appSchoolNameMapper.selectByPrimaryKey(Integer.parseInt(keyValue));
			JSONArray details=new JSONArray();
				JSONObject jo=new JSONObject();
				jo.put("place", places.getSchoolname());
				details.add(jo);
			detail.put("dataList", details);
			}
			
		return detail;
	}
	
	
	/**
	 * 保存个人信息（学生）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject authenticationSchool(JSONObject params) throws Exception {
		JSONObject detail=new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String schoolName=JsonUtil.getJStringAndCheck(params, "schoolName", null, false, detail);
		//String educational=JsonUtil.getJStringAndCheck(params, "educational", null, false, detail);
		String province=JsonUtil.getJStringAndCheck(params, "province", null, false, detail);
		String city=JsonUtil.getJStringAndCheck(params, "city", null, false, detail);
		String town=JsonUtil.getJStringAndCheck(params, "town", null, false, detail);
		String bedRoomAddress=JsonUtil.getJStringAndCheck(params, "bedRoomAddress", null, false, detail);
		String xxwzh=JsonUtil.getJStringAndCheck(params, "xxwzh", null, false, detail);
		String xxwmm=JsonUtil.getJStringAndCheck(params, "xxwmm", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		if("".equals(province) || null == province ){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请选择正确的地址");
			return detail;
		}
		AppUser appUser=appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if(null!=appUser){
			AppLoanAppl appl=appLoanApplMapper.selectByitemCode(appUser.getUserName());
			AppLoanCtm appLoanCtm=appLoanCtmMapper.selectByapprId(appl.getId());
			if("".equals(appLoanCtm.getSchoolName())||null==appLoanCtm.getSchoolName()){
			appLoanCtm.setSchoolName(schoolName);
			//appLoanCtm.setEducational(educational);
			appLoanCtm.setProvince(province);
			appLoanCtm.setCity(city);
			appLoanCtm.setTown(town);
			appLoanCtm.setBdrAddr(bedRoomAddress);
			appLoanCtm.setSchool_status("1");
			if(!"8".equals(appLoanCtm.getSchedule_status())&&Integer.parseInt(appLoanCtm.getSchedule_status())<=7){
			appLoanCtm.setSchedule_status("7");
			}
			appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
			AppLoanCtmCnt cnt=new AppLoanCtmCnt();
			cnt.setApprId(appl.getId());
			cnt.setCntType("0");
			cnt.setCntPass(xxwmm);
			cnt.setCntCommt(xxwzh);
			cnt.setCntDesc("1");
			appLoanCtmCntMapper.insertSelective(cnt);
			detail.put(Consts.RESULT_NOTE, "保存成功");
			}else{
				appLoanCtm.setSchoolName(schoolName);
				//appLoanCtm.setEducational(educational);
				appLoanCtm.setProvince(province);
				appLoanCtm.setCity(city);
				appLoanCtm.setTown(town);
				appLoanCtm.setBdrAddr(bedRoomAddress);
				appLoanCtm.setSchool_status("1");
				appLoanCtm.setIs_modify("1");
				if(!"8".equals(appLoanCtm.getSchedule_status())&&Integer.parseInt(appLoanCtm.getSchedule_status())<=7){
					appLoanCtm.setSchedule_status("7");
					}
				appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
				Map<String,Object> map=new HashMap<>();
				map.put("cntType", "0");
				map.put("apprId", appl.getId());
				AppLoanCtmCnt cnt=appLoanCtmCntMapper.queryByType(map);
				cnt.setCntCommt(xxwzh);
				cnt.setCntPass(xxwmm);
				appLoanCtmCntMapper.updateByPrimaryKeySelective(cnt);
				detail.put(Consts.RESULT_NOTE, "修改成功");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户不存在");
		}
		return detail;
	}
	

	/**
	 * 保存个人信息（成人）
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject authenticationFamily(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		// 判断入参是否校验
		boolean userIdB = doCheckOrNot(params, "userId");
		boolean isMarryB = doCheckOrNot(params, "isMarry");
		boolean provinceB = doCheckOrNot(params, "province");
		boolean cityB = doCheckOrNot(params, "city");
		boolean townB = doCheckOrNot(params, "town");
		boolean bedRoomAddressB = doCheckOrNot(params, "bedRoomAddress");
		boolean companyNameB = doCheckOrNot(params, "companyName");
		boolean companyProvinceB = doCheckOrNot(params, "companyProvince");
		boolean companyCityB = doCheckOrNot(params, "companyCity");
		boolean companyTownB = doCheckOrNot(params, "companyTown");
		boolean comPayDayB = doCheckOrNot(params, "comPayDay");
		boolean companyAddressB = doCheckOrNot(params, "companyAddress");
		boolean companyPhoneB = doCheckOrNot(params, "companyPhone");
		boolean carB = doCheckOrNot(params, "car");
		boolean professionB = doCheckOrNot(params, "profession");
		boolean socialSecurityB = doCheckOrNot(params, "socialSecurity");
		boolean workYearsB = doCheckOrNot(params, "workYears");
		boolean incomeB = doCheckOrNot(params, "income");
		boolean liveWayB = doCheckOrNot(params, "liveWay");
		boolean monthlyAmtB = doCheckOrNot(params, "monthlyAmt");
		boolean educationB = doCheckOrNot(params, "education");
		// 校验参数是否为空
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, userIdB, detail);
		String isMarry = JsonUtil.getJStringAndCheck(params, "isMarry", null, isMarryB, detail);
		String province = JsonUtil.getJStringAndCheck(params, "province", null, provinceB, detail);
		String city = JsonUtil.getJStringAndCheck(params, "city", null, cityB, detail);
		String town = JsonUtil.getJStringAndCheck(params, "town", null, townB, detail);
		String bedRoomAddress = JsonUtil.getJStringAndCheck(params, "bedRoomAddress", null, bedRoomAddressB, detail);
		String CompanyName = JsonUtil.getJStringAndCheck(params, "companyName", null, companyNameB, detail);
		String CompanyProvince = JsonUtil.getJStringAndCheck(params, "companyProvince", null, companyProvinceB, detail);
		String CompanyCity = JsonUtil.getJStringAndCheck(params, "companyCity", null, companyCityB, detail);
		String CompanyTown = JsonUtil.getJStringAndCheck(params, "companyTown", null, companyTownB, detail);
		String comPayDay = JsonUtil.getJStringAndCheck(params, "comPayDay", null, comPayDayB, detail);
		String CompanyAddress = JsonUtil.getJStringAndCheck(params, "companyAddress", null, companyAddressB, detail);
		String companyPhone = JsonUtil.getJStringAndCheck(params, "companyPhone", null, companyPhoneB, detail);
		String orgId = JsonUtil.getJStringAndCheck(params, "orgId", null, true, detail);
		String car = JsonUtil.getJStringAndCheck(params, "car", null, carB, detail);//车辆情况
		String profession = JsonUtil.getJStringAndCheck(params, "profession", null, professionB, detail);//职业
		String social_security = JsonUtil.getJStringAndCheck(params, "socialSecurity", null, socialSecurityB, detail);//社保缴纳
		String work_years = JsonUtil.getJStringAndCheck(params, "workYears", null, workYearsB, detail);//车辆情况
		String income = JsonUtil.getJStringAndCheck(params, "income", null, incomeB, detail);//收入
		String live_way = JsonUtil.getJStringAndCheck(params, "liveWay", null, liveWayB, detail);//居住情况
		String monthlyAmt = JsonUtil.getJStringAndCheck(params, "monthlyAmt", null, monthlyAmtB, detail);//居住情况
		String education = JsonUtil.getJStringAndCheck(params, "education", null, educationB, detail);//居住情况
		if("".equals(province) || "".equals(city) || "".equals(town)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请重新选择您家庭所在地的省市区");
			return detail;
		}
		if("".equals(CompanyProvince) || "".equals(CompanyTown) || "".equals(CompanyCity)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "请重新选择您公司的省市区");
			return detail;
		}
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			AppLoanAppl appl = appLoanApplMapper.selectByUserId(mch.getUserid());
			if(null != appl.getAccountStatus() && 3 == appl.getAccountStatus()){ //账户关闭
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
				return detail;
			}else{
				if("1".equals(StringUtil.nvl(appl.getStatus()))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "正在授信中，不可修改资料");
					return detail;
				}
				if("2".equals(StringUtil.nvl(appl.getStatus()))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "授信已通过，不可修改资料");
					return detail;
				}
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
				appLoanCtm.setIsMarry(isMarry);
				appLoanCtm.setProvince(province);
				appLoanCtm.setCity(city);
				appLoanCtm.setTown(town);
				appLoanCtm.setBdrAddr(bedRoomAddress);
				appLoanCtm.setFamily_status("1");
				appLoanCtm.setCompanyName(CompanyName);
				appLoanCtm.setCommProvince(CompanyProvince);
				appLoanCtm.setCommCity(CompanyCity);
				appLoanCtm.setCommTown(CompanyTown);
				appLoanCtm.setCompayPayDay(comPayDay);
				appLoanCtm.setCompanyAddress(CompanyAddress);
				appLoanCtm.setCompanyPhone(companyPhone);
				appLoanCtm.setCompany_status("1");
				appLoanCtm.setCar(car);
				appLoanCtm.setProfession(profession);
				appLoanCtm.setSocial_security(social_security);
				appLoanCtm.setWork_years(work_years);
				appLoanCtm.setIncome(income);
				appLoanCtm.setLive_way(live_way);
				appLoanCtm.setMonthlyAmt(monthlyAmt);
				appLoanCtm.setEducational(education);
				appl.setPrvince(orgId);
				mch.setOrgId(orgId);
				//风控退回的资料不需要改认证状态
				if(!"1".equals(appl.getBaseInfoStatus())){
					if (Integer.parseInt(appLoanCtm.getSchedule_status()) <= 5) {
						appLoanCtm.setSchedule_status("5");
					}
				}else{
					appl.setBaseInfoStatus("0");
				}
				appLoanApplMapper.updateByPrimaryKeySelective(appl);
				appUserMapper.updateByPrimaryKeySelective(mch);
				appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm);
				if ("".equals(appLoanCtm.getCompanyName()) || null == appLoanCtm.getCompanyName()) { // 新增工作信息
					AppOprLog appOprLog = new AppOprLog();
					appOprLog.setModuleId("0");
					appOprLog.setOprContent("个人信息保存成功");
					appOprLog.setUserid(userId);
					appOprLogMapper.insertSelective(appOprLog);
					detail.put(Consts.RESULT_NOTE, "保存成功");
				} else { // 更新工作信息
					detail.put(Consts.RESULT_NOTE, "修改成功");
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}
	
	/**
	 * 校验传参是否需要校验空值
	 * @param params
	 * @param flag
	 * @return
	 */
	private static boolean doCheckOrNot(JSONObject params, String param) {
		boolean flag = false;
		if (null != params) {
			Set<String> keys = params.keySet();
			if (null != keys && keys.size() > 0) {
				for (String key : keys) {
					if (key.equals(param)) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}

}
