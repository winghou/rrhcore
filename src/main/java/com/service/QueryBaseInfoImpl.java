package com.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppOprLog;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.QueryBaseInfoService;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class QueryBaseInfoImpl implements QueryBaseInfoService {
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Override
	public JSONObject info(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
		List<IfmBaseDict> ifmBaseDictsList = ifmBaseDictMapper.qryMchPersonInfoConfiguration("MCH_PERSON_INFO_CONFIGURATION");
		if(null != ifmBaseDictsList && ifmBaseDictsList.size() > 0){
			JSONArray jsonArray = new JSONArray();
			JSONObject object = null;
			String configId = "";
			String configName = "";
			for(IfmBaseDict ifmBaseDict : ifmBaseDictsList){
				String currentValue = baseInfo(appLoanCtm,ifmBaseDict.getItemKey());
				configId = StringUtil.nvl(ifmBaseDict.getItemKey());
				configName = StringUtil.nvl(ifmBaseDict.getItemValue());
				object = new JSONObject();
				IfmBaseDict dict = new IfmBaseDict();
				dict.setDataType("MCH_BASE_INFO");
				dict.setOutDataFrom(ifmBaseDict.getItemKey());
				JSONArray jsonArray2 = new JSONArray();
				List<IfmBaseDict> list = ifmBaseDictMapper.selectTypeBankCode(dict);
				if(list.size() > 0){
					for(IfmBaseDict ifmBaseDict2 : list){
						JSONObject json = new JSONObject();
						json.put("code", ifmBaseDict2.getItemKey());
						json.put("name", ifmBaseDict2.getItemValue());
						jsonArray2.add(json);
					}
						object.put("isPull", "1");  //下拉类型
				}else{
					if("8".equals(configId) || "12".equals(configId)){
						object.put("isPull", "2");  //地址类型
					}else{
						object.put("isPull", "0");
					}
				}
				object.put("configId", configId);
				object.put("configName", configName);
				object.put("pullDownList",jsonArray2);
				object.put("currentValue",currentValue);
				object.put("verityType", StringUtil.nvl(ifmBaseDict.getParentId()));
				object.put("promptText", StringUtil.nvl(ifmBaseDict.getDictDesc()));
				jsonArray.add(object);
			}
			detail.put("personInfoConfig", jsonArray);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "资料数据有误，请联系客服");
		}
		return detail;
	}
	
	

	public String baseInfo(AppLoanCtm appLoanCtm,String type){
		StringBuffer familyAddressBuffer = new StringBuffer();
		StringBuffer companyAddressBuffer = new StringBuffer();
		if(!"".equals(StringUtil.nvl(appLoanCtm.getCommProvince()))&&!"".equals(StringUtil.nvl(appLoanCtm.getCommCity()))&&!"".equals(StringUtil.nvl(appLoanCtm.getCommTown()))){
			companyAddressBuffer.append(StringUtil.nvl(appLoanCtm.getCommProvince())).append(",").append(appLoanCtm.getCommCity()).append(",").append(appLoanCtm.getCommTown());
		}else{
			companyAddressBuffer.append("");
		}
		
		if(!"".equals(StringUtil.nvl(appLoanCtm.getProvince()))&&!"".equals(StringUtil.nvl(appLoanCtm.getCity()))&&!"".equals(StringUtil.nvl(appLoanCtm.getTown()))){
			familyAddressBuffer.append(StringUtil.nvl(appLoanCtm.getProvince())).append(",").append(appLoanCtm.getCity()).append(",").append(appLoanCtm.getTown());
		}else{
			familyAddressBuffer.append("");
		}
		switch(type){
			case "0":return StringUtil.nvl(appLoanCtm.getMonthlyAmt());  //房租/月供/车贷
			case "1":return StringUtil.nvl(appLoanCtm.getEducational());   //文化程度
			case "2":return StringUtil.nvl(appLoanCtm.getCar());   //是否有车
			case "3":return StringUtil.nvl(appLoanCtm.getProfession());   //职业
			case "4":return StringUtil.nvl(appLoanCtm.getSocial_security());  //社保缴纳
			case "5":return StringUtil.nvl(appLoanCtm.getWork_years());   //当前单位工龄
			case "6":return StringUtil.nvl(appLoanCtm.getIncome());   //每月收入
			case "7":return StringUtil.nvl(appLoanCtm.getIsMarry());  //婚姻状况
			case "8":return StringUtil.nvl(familyAddressBuffer);   //家庭所在地
			case "9":return StringUtil.nvl(appLoanCtm.getBdrAddr());  //家庭详细地址
			case "10":return StringUtil.nvl(appLoanCtm.getLive_way());  //居住方式
			case "11":return StringUtil.nvl(appLoanCtm.getCompanyName());  //公司名称
			case "12":return StringUtil.nvl(companyAddressBuffer);   //公司地址
			case "13":return StringUtil.nvl(appLoanCtm.getCompanyAddress());   //公司详细地址
			case "14":return StringUtil.nvl(appLoanCtm.getCompanyPhone());  //公司电话
			case "15":return StringUtil.nvl(appLoanCtm.getCompayPayDay());   //发薪日
			default: return "";
		}
	}



	@Override
	public JSONObject saveInfo(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		JSONArray personInfoList = params.getJSONArray("personInfoList");
		AppUser appUser = appUserMapper.selectByMchVersion(userId);
		AppLoanAppl loanAppl = appLoanApplMapper.selectByUserId(appUser.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(loanAppl.getId());
		if(null != loanAppl.getAccountStatus() && 3 == loanAppl.getAccountStatus()){ //账户关闭
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
			return detail;
		}else{
			if("1".equals(StringUtil.nvl(loanAppl.getStatus()))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "正在授信中，不可修改资料");
				return detail;
			}
			if("2".equals(StringUtil.nvl(loanAppl.getStatus()))){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "授信已通过，不可修改资料");
				return detail;
			}
			if(personInfoList.size()>0){
				for (Iterator<Object> tor=personInfoList.iterator();tor.hasNext();) {
					JSONObject job = (JSONObject)tor.next();
					switch(StringUtil.nvl(job.get("configId"))){
					case "0": appLoanCtm.setMonthlyAmt(job.getString("currentValue"));break;
					case "1": appLoanCtm.setEducational(job.getString("currentValue"));break;
					case "2": appLoanCtm.setCar(job.getString("currentValue"));break;
					case "3": appLoanCtm.setProfession(job.getString("currentValue"));break;
					case "4": appLoanCtm.setSocial_security(job.getString("currentValue"));break;
					case "5": appLoanCtm.setWork_years(job.getString("currentValue"));break;
					case "6": appLoanCtm.setIncome(job.getString("currentValue"));break;
					case "7": appLoanCtm.setIsMarry(job.getString("currentValue"));break;
					case "8": setAppLoanCtm(appLoanCtm,StringUtil.nvl(job.get("configId")),job.getString("currentValue"));break;
					case "9": appLoanCtm.setBdrAddr(job.getString("currentValue"));break;
					case "10": appLoanCtm.setLive_way(job.getString("currentValue"));break;
					case "11": appLoanCtm.setCompanyName(job.getString("currentValue"));break;
					case "12": setAppLoanCtm(appLoanCtm,StringUtil.nvl(job.get("configId")),job.getString("currentValue"));break;
					case "13": appLoanCtm.setCompanyAddress(job.getString("currentValue"));break;
					case "14": appLoanCtm.setCompanyPhone(job.getString("currentValue"));break;
					case "15": appLoanCtm.setCompayPayDay(job.getString("currentValue"));break;
					default :break;
					}
				}
				//风控退回的资料不需要改认证状态
				if(!"1".equals(loanAppl.getBaseInfoStatus())){
					if (Integer.parseInt(appLoanCtm.getSchedule_status()) <= 5) {
						appLoanCtm.setSchedule_status("5");
					}
				}else{
					loanAppl.setBaseInfoStatus("0");
				}
				appUser.setOrgId("1");
				appLoanApplMapper.updateByPrimaryKeySelective(loanAppl);
				appUserMapper.updateByPrimaryKeySelective(appUser);
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
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "资料数据有误，请联系客服");
			}
		}
		return detail;
	}
	
	public AppLoanCtm setAppLoanCtm(AppLoanCtm appLoanCtm,String configId,String currentValue){
		String[] str = currentValue.split(",");
		if("8".equals(configId)){
			appLoanCtm.setProvince(str[0]);
			appLoanCtm.setCity(str[1]);
			appLoanCtm.setTown(str[2]);
		}else if("12".equals(configId)){
			appLoanCtm.setCommProvince(str[0]);
			appLoanCtm.setCommCity(str[1]);
			appLoanCtm.setCommTown(str[2]);
		}
		return appLoanCtm;
	}
}
