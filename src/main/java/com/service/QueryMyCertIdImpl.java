package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.APPCreditMapper;
import com.dao.AppAgreementPayBankMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppUserMapper;
import com.dao.AppZhimaScoreMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.APPCredit;
import com.model.AppAgreementPayBank;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppLoanCtmShip;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.DecisionEngineService;
import com.service.intf.QueryMyCertIdService;
import com.util.APIHttpClient;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;
@Service
public class QueryMyCertIdImpl implements QueryMyCertIdService {
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppZhimaScoreMapper appZhimaScoreMapper;
	@Autowired
	private APPCreditMapper appCreditMapper;
	@Autowired
	private DecisionEngineService decisionEngineService;
	@Autowired
	private AppAgreementPayBankMapper appAgreementPayBankMapper;
	

	/**
	 * 查询用户资本资料
	 */
	@Override
	public JSONObject queryMyCertId(JSONObject params) throws Exception {
		JSONObject detail=new JSONObject();
		String userId=JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String qryType=JsonUtil.getJStringAndCheck(params, "qryType", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser user = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(user.getUserName());
		if(null!=appLoanAppl){
			//获取资料配置项
			JSONObject object0 = queryPersonInfoConfiguration();
			AppLoanCtm appLoanCtm=appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			if("0".equals(qryType)){ //姓名、身份证
				detail.put("userName", StringUtil.nvl(appLoanCtm.getCustomName()));
				detail.put("identityCard", StringUtil.nvl(appLoanCtm.getIdentityCard()));
			}else if("1".equals(qryType)){ //学校信息
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "0");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				detail.put("schoolName", StringUtil.nvl(appLoanCtm.getSchoolName()));
				detail.put("province", StringUtil.nvl(appLoanCtm.getProvince()));
				detail.put("city", StringUtil.nvl(appLoanCtm.getCity()));
				detail.put("town", StringUtil.nvl(appLoanCtm.getTown()));
				detail.put("bedRoomAddress", StringUtil.nvl(appLoanCtm.getBdrAddr()));
				detail.put("xjzh", StringUtil.nvl(yx.getCntCommt()));
				detail.put("xjmm", StringUtil.nvl(yx.getCntPass()));
			}else if("2".equals(qryType)){ //工作信息
				detail.put("isMarry", StringUtil.nvl(appLoanCtm.getIsMarry()));
				detail.put("province", StringUtil.nvl(appLoanCtm.getProvince()));
				detail.put("city", StringUtil.nvl(appLoanCtm.getCity()));
				detail.put("town", StringUtil.nvl(appLoanCtm.getTown()));
				detail.put("bedRoomAddress", StringUtil.nvl(appLoanCtm.getBdrAddr()));
				detail.put("companyName", StringUtil.nvl(appLoanCtm.getCompanyName()));
				detail.put("commProvince", StringUtil.nvl(appLoanCtm.getCommProvince()));
				detail.put("commCity", StringUtil.nvl(appLoanCtm.getCommCity()));
				detail.put("commTown", StringUtil.nvl(appLoanCtm.getCommTown()));
				detail.put("comPayDay",StringUtil.nvl(appLoanCtm.getCompayPayDay()));
				detail.put("companyAddress",StringUtil.nvl(appLoanCtm.getCompanyAddress()));
				detail.put("companyPhone",StringUtil.nvl(appLoanCtm.getCompanyPhone()));
				detail.put("orgId",StringUtil.nvl(appLoanAppl.getPrvince()));
				detail.put("monthlyAmt",StringUtil.nvl(appLoanCtm.getMonthlyAmt())); //房租/月供
				detail.put("education",StringUtil.nvl(appLoanCtm.getEducational())); //学历
				detail.put("car",StringUtil.nvl(appLoanCtm.getCar())); //车辆情况
				detail.put("profession",StringUtil.nvl(appLoanCtm.getProfession())); //职业
				detail.put("socialSecurity",StringUtil.nvl(appLoanCtm.getSocial_security())); //社保缴纳
				detail.put("workYears",StringUtil.nvl(appLoanCtm.getWork_years())); //车辆情况
				detail.put("income",StringUtil.nvl(appLoanCtm.getIncome())); //收入
				detail.put("liveWay",StringUtil.nvl(appLoanCtm.getLive_way())); //居住情况
			}else if("3".equals(qryType)){ //运营商信息
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "2");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("yyzh", yx.getCntCommt());
					detail.put("yymm", yx.getCntPass());
				}else{
					detail.put("yyzh", "");
					detail.put("yymm", "");
				}
				List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("JUXINGLI_URL");
				String url = (String) ifmBaseDicts.get(0).get("ITEM_VALUE");
				boolean skip_mobile = false;
				String uid = "";
				JSONObject json=new JSONObject();
				JSONArray selected_website=new JSONArray();
				JSONObject basic_info=new JSONObject();
				JSONArray contacts=new JSONArray();
				basic_info.put("name",appLoanCtm.getCustomName()); //用户名称
				String str = appLoanCtm.getIdentityCard();
				//身份证号码(身份证最后一位的X统一为大写)
				if(str.charAt(str.length()-1) =='x'){
					String newString = str.substring(0,str.length()-1);
					StringBuilder builder = new StringBuilder();
					builder.append(newString).append("X");
					basic_info.put("id_card_num",builder);
				}else{
					basic_info.put("id_card_num",appLoanCtm.getIdentityCard());   //身份证号码
				}
				basic_info.put("cell_phone_num",user.getUserName());  //手机号码
				List<AppLoanCtmShip> list= appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId()); //获取联系人数据
				basic_info.put("home_addr ",list.get(0).getShipAddr());        //我的家庭地址
				for(AppLoanCtmShip ship : list){
					JSONObject contacts02=new JSONObject();
					contacts02.put("contact_tel", ship.getShipCnt());         //联系人电话
					contacts02.put("contact_name", ship.getShipName());        //联系人姓名
					//联系人类型（"0":配偶，"1":父母，"2":兄弟姐妹,"3":子女,"4":同事,"5": 同学,"6": 朋友）聚信立联系规则
					if(ship.getShipType().equals("0")){    //shipType 0:为第一联系人 1：为第二联系人
						if(ship.getRelationship().equals("配偶")){
							contacts02.put("contact_type", "0");
						}else{
							contacts02.put("contact_type", "1");
						}
					}else{
						if(ship.getRelationship().equals("配偶")){
							contacts02.put("contact_type", "0");
						}else if(ship.getRelationship().equals("父/母")){
							contacts02.put("contact_type", "1");
						}else if(ship.getRelationship().equals("同事")){
							contacts02.put("contact_type", "4");
						}else if(ship.getRelationship().equals("朋友")){
							contacts02.put("contact_type", "6");
						}else{
							contacts02.put("contact_type", "5");  //将其他类型定为同学
						}
					}      
					contacts.add(contacts02);
				}
				json.put("selected_website", selected_website);
				json.put("basic_info", basic_info);
				json.put("contacts", contacts);
				json.put("skip_mobile", skip_mobile);
				json.put("uid", uid);
				String result=APIHttpClient.doPost(url, json, 60000);
		        JSONObject res =(JSONObject) JSON.parse(result);
		        JSONObject res1 =res.getJSONObject("data");
		        if(result == null){
		        	detail.put(Consts.RESULT, ErrorCode.FAILED);
		        	detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		        }else{
		        	if(res.getString("code").equals("65556")){
		        		detail.put(Consts.RESULT, ErrorCode.FAILED);
		        		detail.put("code", res.getString("code"));
		        		detail.put("isToken", "1");
			        	detail.put(Consts.RESULT_NOTE, "运营商异常请重新刷新");
		        	}else if(res.getString("code").equals("65542")){
		        		detail.put(Consts.RESULT, ErrorCode.FAILED);
		        		detail.put("code", res.getString("code"));
			        	detail.put("isToken", "0");
			        	detail.put(Consts.RESULT_NOTE, "手机号码不合法");
		        	}else{
		        		if(res1 == null){
			        		detail.put(Consts.RESULT, ErrorCode.FAILED);
				        	detail.put("code", res.getString("code"));
				        	detail.put("isToken", "0");
				        	detail.put(Consts.RESULT_NOTE, res.getString("message"));
				        }else{
				        	JSONObject res2 =res1.getJSONObject("datasource");
					        detail.put("token",res1.getString("token"));
					        detail.put("website", res2.getString("website"));
					        detail.put("reset_pwd_method", res2.getString("reset_pwd_method"));
				        }
		        	}
		        }
			}else if("4".equals(qryType)){ //银行卡信息
				if(!"".equals(appLoanCtm.getBank())&&null!=appLoanCtm.getBank()){
					List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("BANK_NAME");
					for (Map<String, Object> map : ifmBaseDicts) {
						String bankNum=StringUtil.toString(map.get("ITEM_KEY"));
						if(bankNum.equals(appLoanCtm.getBank())){
							detail.put("bankName", StringUtil.toString(map.get("ITEM_VALUE")));
							detail.put("bankLogUrl", StringUtil.toString(map.get("XH")));
							detail.put("bankCard", appLoanCtm.getBankCard());
							detail.put("bankPhone", appLoanCtm.getBankPhone()+"");
							detail.put("userName", appLoanCtm.getCustomName());
							//StringBuffer idCard=new StringBuffer(appLoanCtm.getIdentityCard());
							detail.put("identityCard", appLoanCtm.getIdentityCard());
							break;
						}else{
							AppAgreementPayBank agreementPayBank = appAgreementPayBankMapper.selectByCode(StringUtil.nvl(appLoanCtm.getBank()));
							if(agreementPayBank!=null){
								detail.put("bankName", agreementPayBank.getBankName());
								detail.put("bankLogUrl", agreementPayBank.getBankUrl());
								detail.put("bankCard", appLoanCtm.getBankCard());
								detail.put("bankPhone", appLoanCtm.getBankPhone()+"");
								detail.put("userName", appLoanCtm.getCustomName());
								//StringBuffer idCard=new StringBuffer(appLoanCtm.getIdentityCard());
								detail.put("identityCard", appLoanCtm.getIdentityCard());
								break;
							}
						}
					}
				}else{
					detail.put("bankName", "");
					detail.put("bankLogUrl", "");
					detail.put("bankCard", "");
					detail.put("bankPhone", "");
					detail.put("userName", appLoanCtm.getCustomName());
					//StringBuffer idCard=new StringBuffer(appLoanCtm.getIdentityCard());
					detail.put("identityCard", appLoanCtm.getIdentityCard());
				}
			}else if("5".equals(qryType)){ //联系人信息
				List<AppLoanCtmShip> ships= appLoanCtmShipMapper.selectByApprId(appLoanAppl.getId());
				if(null!=ships&&ships.size()>0){
					JSONArray detaList=new JSONArray();
					for (AppLoanCtmShip ship : ships) {
						JSONObject jo=new JSONObject();
						jo.put("contactType", ship.getShipType());
						jo.put("contactName", ship.getShipName());
						jo.put("contactPhone", ship.getShipCnt());
						jo.put("relationship", ship.getRelationship()+"");
						if("0".equals(ship.getShipType())){
						if(ship.getShipAddr().lastIndexOf(",")<=0){
							jo.put("familyAdress","");
							jo.put("contactAdress", ship.getShipAddr());
						}else{
						String sd=ship.getShipAddr().substring(0, ship.getShipAddr().lastIndexOf(","));
						String cd=ship.getShipAddr().substring(ship.getShipAddr().lastIndexOf(",")+1, ship.getShipAddr().length());
						jo.put("familyAdress",sd);
						jo.put("contactAdress", cd);
						}
						}
						detaList.add(jo);
					}
					detail.put("dataList", detaList);
				}else{
					JSONArray detaList=new JSONArray();
					JSONObject jo=new JSONObject();
					JSONObject qo=new JSONObject();
					jo.put("contactType", "");
					jo.put("contactName", "");
					jo.put("contactPhone", "");
					jo.put("relationship", "");
					jo.put("familyAdress","");
					jo.put("contactAdress", "");
					qo.put("contactType", "");
					qo.put("contactName", "");
					qo.put("contactPhone", "");
					qo.put("relationship", "");
					qo.put("familyAdress","");
					qo.put("contactAdress", "");
					detaList.add(jo);
					detaList.add(qo);
					detail.put("dataList", detaList);
				}
			}else if("6".equals(qryType)){ //信用卡账号
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "6");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt zh = appLoanCtmCntMapper.queryByType(map);
				if(null!=zh){
					List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("BANK_NAME");
					for (Map<String, Object> map1 : ifmBaseDicts) {
						String bankNum=StringUtil.toString(map1.get("ITEM_KEY"));
						if(bankNum.equals(zh.getCntLx()+"")){
							detail.put("bankName", StringUtil.toString(map1.get("ITEM_VALUE")));
							detail.put("bankLogUrl", StringUtil.toString(map1.get("XH")));
							detail.put("xykzh", zh.getCntCommt());
							detail.put("zhmm", zh.getCntPass());
							break;
						}
					}
				}else{
					detail.put("bankName", "");
					detail.put("bankLogUrl", "");
					detail.put("xykzh", "");
					detail.put("zhmm", "");
				}
			}else if("7".equals(qryType)){ //信用卡邮箱
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "5");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("xykyx", yx.getCntCommt());
					detail.put("yxmm", yx.getCntPass());
				}else{
					detail.put("xykyx", "");
					detail.put("yxmm", "");
				}
			}else if("8".equals(qryType)){ //社交账号信息
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "1");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("qqzh", yx.getCntCommt()+"");
					detail.put("wxzh", yx.getCntPass()+"");
				}else{
					detail.put("qqzh", "");
					detail.put("wxzh", "");
				}
				String creditAmt = "0.00";
				APPCredit appCredit = appCreditMapper.selectByApprId(appLoanAppl.getId());
				if(null != appCredit){
					creditAmt = appCredit.getCreditAmt() + "";
				}
				detail.put("creditAmt", creditAmt);
			}else if("9".equals(qryType)){ //京东账号
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "3");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("jdzh", yx.getCntCommt());
					detail.put("jdmm", yx.getCntPass());
				}else{
					detail.put("jdzh", "");
					detail.put("jdmm", "");
				}
			}else if("10".equals(qryType)){ //淘宝账号
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "4");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("tbzh", yx.getCntCommt());
					detail.put("tbmm", yx.getCntPass());
				}else{
					detail.put("tbzh", "");
					detail.put("tbmm", "");
				}
			}else if("11".equals(qryType)){ //公积金信息
				Map<String, Object> map = new HashMap<>();
				map.put("cntType", "7");
				map.put("apprId", appLoanAppl.getId());
				AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map);
				if(null!=yx){
					detail.put("gjjzh", yx.getCntCommt());
					detail.put("gjjmm", yx.getCntPass());
					detail.put("city", yx.getCntLx()+"");
				}else{
					detail.put("gjjzh", "");
					detail.put("gjjmm", "");
					detail.put("city", "");
				}
			}else if("12".equals(qryType)){ //芝麻信用、运营商认证状态
				String zhimaStatus = StringUtil.nvl(appLoanAppl.getZhimaStatus());
				String baseInfoStatus = StringUtil.nvl(appLoanAppl.getBaseInfoStatus());
				String contactInfoStatus = StringUtil.nvl(appLoanAppl.getContactInfoStatus());
				String schduleStatus = StringUtil.nvl(appLoanCtm.getSchedule_status());
				if("6".equals(schduleStatus)){
					detail.put("zhimaStatus", "0"); //芝麻未认证
					detail.put("operatorStatus", "0"); //运营商未认证 
				}
				if("7".equals(schduleStatus)){
					detail.put("zhimaStatus", "1"); //芝麻已认证
					detail.put("operatorStatus", "0"); //运营商未认证 
				}
				if("8".equals(schduleStatus)){
					if("1".equals(zhimaStatus)){ //芝麻分到期
						detail.put("zhimaStatus", "0"); //芝麻需重新授权
						detail.put("operatorStatus", "1"); //运营商已认证 
					}else if(!"1".equals(baseInfoStatus) && !"1".equals(zhimaStatus) && !"1".equals(contactInfoStatus)){
						detail.put("zhimaStatus", "1"); //芝麻已认证
						detail.put("operatorStatus", "1"); //运营商已认证 
						JSONObject object = new JSONObject();
						object.put("userId", appLoanAppl.getUserId());
						Map<String, Object> m = new HashMap<String, Object>();
						m.put("cntType", "2");
						m.put("apprId", appLoanAppl.getId());
						AppLoanCtmCnt loanCtmCnt = appLoanCtmCntMapper.queryByType(m);
						object.put("token", loanCtmCnt.getCntLx());
						object.put("report_id", loanCtmCnt.getGroupId());
						// 查询运营商报告，有报告取授信
						JSONObject object2 = decisionEngineService.checkReportExsit(object);
					}
				}
				List<Map<String, Object>> maps = ifmBaseDictMapper.selectBaseDict("JXL_OPERATOR_URL");
				String operatorUrl = StringUtil.nvl(maps.get(0).get("ITEM_VALUE"));
				detail.put("operatorUrl", operatorUrl);
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "资料类型错误");
			}
			detail.putAll(object0);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

	/**
	 * 查询基本资料配置项
	 * 
	 * 0 房租/月供 1 学历 2 是否有车 3 职业 4 社保缴纳 5 当前单位工龄 6 每月收入 7 婚姻状况 8 家庭所在地 9 家庭详细地址
	 * 10 居住方式 11 公司名称 12 公司地址 13 公司详细地址 14 公司电话 15 发薪日
	 */
	public JSONObject queryPersonInfoConfiguration(){
		JSONObject detail = new JSONObject();
		// 查询基本资料配置项
		List<IfmBaseDict> ifmBaseDicts = ifmBaseDictMapper.qryMchPersonInfoConfiguration("MCH_PERSON_INFO_CONFIGURATION");
		if(null != ifmBaseDicts && ifmBaseDicts.size() > 0){
			JSONArray jsonArray = new JSONArray();
			JSONObject object = null;
			String configId = "";
			String configName = "";
			for(IfmBaseDict ifmBaseDict : ifmBaseDicts){
				configId = StringUtil.nvl(ifmBaseDict.getItemKey());
				configName = StringUtil.nvl(ifmBaseDict.getItemValue());
				object = new JSONObject();
				object.put("configId", configId);
				object.put("configName", configName);
				jsonArray.add(object);
			}
			detail.put("personInfoConfig", jsonArray);
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "资料数据有误，请联系客服");
		}
		return detail;
	}

}
