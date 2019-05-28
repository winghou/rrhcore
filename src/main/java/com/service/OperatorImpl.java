package com.service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.service.intf.DecisionEngineService;
import com.service.intf.OperatorService;
import com.util.APIHttpClient;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class OperatorImpl implements OperatorService {
	
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
	
	/* @author yang.wu
	 * 类名称： OperatorImpl
	 * 创建时间：2017年4月25日 上午9:16:53
	 * @see com.service.intf.OperatorService#operator(com.alibaba.fastjson.JSONObject)
	 * 类描述：提交数据源采集请求
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject operator(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String account = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail); //手机号码
		String token = JsonUtil.getJStringAndCheck(params, "token", null, true, detail);  //用户令牌
		String password = JsonUtil.getJStringAndCheck(params, "password", null, true, detail);  //服务号
		String website = JsonUtil.getJStringAndCheck(params, "website", null, true, detail); //网站英文名称
		String captcha = JsonUtil.getJStringAndCheck(params, "captcha", null, false, detail); //验证码
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		//type RESEND_CAPTCHA 重发短信验证码   SUBMIT_CAPTCHA  提交短信验证码
	    String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);   //提交短信验证码
	    List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("JUXINGLI_URL"); //获取请求接口url
	    List<Map<String,Object>> ifmBaseDicts1= ifmBaseDictMapper.selectBaseDict("JUXINGLI_ISTOKEN"); //token失效判断条件
		String url = (String) ifmBaseDicts.get(1).get("ITEM_VALUE");
	    JSONObject json = new JSONObject();
		json.put("account", account);
		json.put("token", token);
		json.put("password", password);
		json.put("website", website);
		json.put("type", type);
		json.put("captcha", captcha);
		json.put("product_id", "80068533");
		String result = APIHttpClient.doPost(url, json,60000);
		JSONObject result2 = (JSONObject) JSON.parse(result);
		JSONObject res = (JSONObject) JSON.parse(result2.getString("res"));
		if(res == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("success") == "false"){
				if(res.getString("message").equals(ifmBaseDicts1.get(0).get("ITEM_VALUE"))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "30000"); //需重新认证
					detail.put(Consts.RESULT_NOTE, res.getString("message"));
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "10000");  //自定义流程码
					detail.put(Consts.RESULT_NOTE, res.getString("message"));
				}
			}else{
				JSONObject res1 = res.getJSONObject("data");
				String process_code =res1.getString("process_code");  //流程码
				if(process_code.equals("10008")){    //如果认证成功 保存或更新服务密码
					AppLoanAppl appl = appLoanApplMapper.selectByitemCode(mch.getUserName());
					Map<String , Object> mp=new HashMap<>();
					mp.put("cntType", "2");
					mp.put("apprId", appl.getId());
					AppLoanCtmCnt cnt=appLoanCtmCntMapper.queryByType(mp);
					AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
					Date operatorTime = new Date();
					JSONObject params2 = new JSONObject();
					params2.put("userId", mch.getUserid());
					params2.put("token", token);
					params2.put("report_id", result2.getString("report_id"));
					if(null == cnt){
						appLoanCtmCnt.setCntCommt(account);
						appLoanCtmCnt.setCntPass(password);
						appLoanCtmCnt.setApprId(appl.getId());
						appLoanCtmCnt.setCntType("2");
						appLoanCtmCnt.setCntDesc("1");
						appLoanCtmCnt.setCntLx(token);
						appLoanCtmCnt.setGroupId(result2.getString("report_id"));
						appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
						AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(appl.getId());
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
					}else{
						appLoanCtmCnt.setId(cnt.getId());
						appLoanCtmCnt.setCntCommt(account);
						appLoanCtmCnt.setCntPass(password);
						appLoanCtmCnt.setApprId(appl.getId());
						appLoanCtmCnt.setCntType("2");
						appLoanCtmCnt.setCntDesc("1");
						appLoanCtmCnt.setCntLx(token);
						appLoanCtmCntMapper.updateByPrimaryKeySelective(appLoanCtmCnt);
						AppLoanCtm ctm=appLoanCtmMapper.selectByapprId(appl.getId());
						ctm.setSchedule_status("8");
						ctm.setOperatorTime(operatorTime);
						appLoanCtmMapper.updateByPrimaryKeySelective(ctm);
						if(!(("1").equals(appl.getZhimaStatus()) || ("1").equals(appl.getBaseInfoStatus()) || ("1").equals(appl.getContactInfoStatus()))){
							JSONObject jsonObject = decisionEngineService.checkReportExsit(params2);
							if(!jsonObject.getString("result").equals("0")){
								detail.put(Consts.RESULT, ErrorCode.FAILED);
								detail.put("process_code", "30000");
								detail.put(Consts.RESULT_NOTE, "请重新认证");
								return detail;
							}
						}
					}
				}
				if(process_code.equals("30000")){//需重新认证
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", process_code);
					detail.put(Consts.RESULT_NOTE, res1.getString("content"));
				}else if(process_code.equals("0")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", process_code);
					detail.put(Consts.RESULT_NOTE, "请求超时请重新提交");
				}else if(process_code.equals("10004")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", process_code);
					detail.put(Consts.RESULT_NOTE, "动态密码错误");
				}else if(process_code.equals("10007")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", process_code);
					detail.put(Consts.RESULT_NOTE, "简单密码/初始密码或错误密码无法登陆");
				}else if(process_code.equals("10008")||process_code.equals("10002")||process_code.equals("10017")||process_code.equals("10018")||process_code.equals("10001")){
					detail.put("process_code", process_code);
				    detail.put("content", res1.getString("content"));
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", process_code);
					detail.put(Consts.RESULT_NOTE, res1.getString("content"));
				}
			}
		}
		return detail;
	}

	/* @author yang.wu
	 * 类名称： OperatorImpl
	 * 创建时间：2017年4月25日 上午9:16:26
	 * @see com.service.intf.OperatorService#operatorResetServiceNumber(com.alibaba.fastjson.JSONObject)
	 * 类描述：重置服务密码
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject operatorResetServiceNumber(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String token = JsonUtil.getJStringAndCheck(params, "token", null, true, detail);  //用户令牌
		String account = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);  //手机号码
		String password = JsonUtil.getJStringAndCheck(params, "password", null, false, detail);  //服务号密码
		String website = JsonUtil.getJStringAndCheck(params, "website", null, true, detail);  //网站英文名称
		String captcha = JsonUtil.getJStringAndCheck(params, "captcha", null, false, detail);  //验证码 
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		//type  RESEND_RESET_PWD_CAPTCHA 重发短信验证码   SUBMIT_RESET_PWD  提交短信验证码
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail); 
		String reset_pwd_method = JsonUtil.getJStringAndCheck(params, "reset_pwd_method", null, false, detail);  //判断是否支持重置服务号密码
		List<Map<String,Object>> ifmBaseDicts= ifmBaseDictMapper.selectBaseDict("JUXINGLI_URL");  //获取请求接口url
		List<Map<String,Object>> ifmBaseDicts1= ifmBaseDictMapper.selectBaseDict("JUXINGLI_ISTOKEN"); //token失效判断条件
		String url = (String) ifmBaseDicts.get(2).get("ITEM_VALUE");
		JSONObject json = new JSONObject();
		if(reset_pwd_method.equals("2")){
			json.put("password", password);
		}
		json.put("captcha", captcha);
		json.put("account", account);
		json.put("token", token);
		json.put("website", website);
		json.put("type", type);
		String result = APIHttpClient.doPost(url, json,60000);
		JSONObject res =(JSONObject) JSON.parse(result);
		if(result == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put("process_code", "10000"); //自定义流程码
			detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
		}else{
			if(res.getString("success") == "false"){
				if(res.getString("message").equals(ifmBaseDicts1.get(0).get("ITEM_VALUE"))){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "30000"); //需重新认证
					detail.put(Consts.RESULT_NOTE, res.getString("message"));
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", "10000"); //自定义流程码
					detail.put(Consts.RESULT_NOTE, res.getString("message"));
				}
			}else{
				JSONObject res1 = res.getJSONObject("data");
				if(res1.getString("process_code").equals("11000")&&reset_pwd_method.equals("2")){    //保存或更新服务密码
					AppLoanAppl appl = appLoanApplMapper.selectByitemCode(account);
					Map<String , Object> mp=new HashMap<>();
					mp.put("cntType", "2");
					mp.put("apprId", appl.getId());
					AppLoanCtmCnt cnt=appLoanCtmCntMapper.queryByType(mp);
					AppLoanCtmCnt appLoanCtmCnt = new AppLoanCtmCnt();
					if(null == cnt){
						appLoanCtmCnt.setCntCommt(account);
						appLoanCtmCnt.setCntPass(password);
						appLoanCtmCnt.setApprId(appl.getId());
						appLoanCtmCnt.setCntType("2");
						appLoanCtmCnt.setCntDesc("1");
						appLoanCtmCntMapper.insertSelective(appLoanCtmCnt);
					}else{
						appLoanCtmCnt.setId(cnt.getId());
						appLoanCtmCnt.setCntCommt(account);
						appLoanCtmCnt.setCntPass(password);
						appLoanCtmCnt.setApprId(appl.getId());
						appLoanCtmCnt.setCntType("2");
						appLoanCtmCnt.setCntDesc("1");
						appLoanCtmCntMapper.updateByPrimaryKeySelective(appLoanCtmCnt);
					}
				}
				if(res1.getString("process_code").equals("30000")){//需重新认证
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", res1.getString("process_code"));
					detail.put(Consts.RESULT_NOTE, res1.getString("content"));
				}else if(res1.getString("process_code").equals("0")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", res1.getString("process_code"));
					detail.put(Consts.RESULT_NOTE, "请求超时请重新提交");
				}else if(res1.getString("process_code").equals("10004")){
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", res1.getString("process_code"));
					detail.put(Consts.RESULT_NOTE, "动态密码错误");
				}else if(res1.getString("process_code").equals("11000")||res1.getString("process_code").equals("10002")||res1.getString("process_code").equals("10001")){
					detail.put("process_code", res1.getString("process_code"));
		            detail.put("content",res1.getString("content"));
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put("process_code", res1.getString("process_code"));
					detail.put(Consts.RESULT_NOTE, res1.getString("content"));
				}
			}  
		}
		return detail;
	}

	/* @author yang.wu
	 * 类名称： OperatorImpl
	 * 创建时间：2017年5月19日 上午10:50:55
	 * @see com.service.intf.OperatorService#getBaseInfoToOperator(com.alibaba.fastjson.JSONObject)
	 * 类描述：获取运营商认证所需基本资料
	 */
	@Override
	public JSONObject getBaseInfoToOperator(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);   
		String newPhone = JsonUtil.getJStringAndCheck(params, "newPhone", null, false, detail);   //新手机号
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		//AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		if(appLoanAppl != null){
			AppLoanCtm appLoanCtm=appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
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
			if("".equals(StringUtil.nvl(str))) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "未获得身份证号码！");
				return detail;
			}else {
				basic_info.put("id_card_num",(appLoanCtm.getIdentityCard()).toUpperCase());   //身份证号码
			}
			if(newPhone.equals("")){
				basic_info.put("cell_phone_num",appLoanAppl.getItemCode());  //手机号码
			}else{
				basic_info.put("cell_phone_num",newPhone);  //手机号码
			}
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
	        	detail.put("code", "10000"); //自定义流程码
	        	detail.put(Consts.RESULT_NOTE, "网络不稳定请求超时");
	        }else{
	        	if(res.getString("code").equals("65556")){
	        		detail.put(Consts.RESULT, ErrorCode.FAILED);
	        		detail.put("code", res.getString("code"));
		        	detail.put(Consts.RESULT_NOTE, "运营商异常请重新提交！");
	        	}else if(res.getString("code").equals("65542")){
	        		detail.put(Consts.RESULT, ErrorCode.FAILED);
	        		detail.put("code", res.getString("code"));
		        	detail.put(Consts.RESULT_NOTE, "手机号码不合法");
	        	}else{
	        		if(res1 == null){
		        		detail.put(Consts.RESULT, ErrorCode.FAILED);
			        	detail.put("code", res.getString("code"));
			        	detail.put(Consts.RESULT_NOTE, res.getString("message"));
			        }else{
			        	JSONObject res2 =res1.getJSONObject("datasource");
				        detail.put("token",res1.getString("token"));
				        detail.put("website", res2.getString("website"));
				        detail.put("reset_pwd_method", res2.getString("reset_pwd_method"));
			        }
	        	}
	        }
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
        	detail.put(Consts.RESULT_NOTE, "未登录");
		}
		return detail;
	}

	/**
	 * @author yang.wu
	 * @Description: 获取账户状态
	 * @date 2017年8月8日下午3:40:36
	 * @see com.service.intf.OperatorService#getAccountStatus(com.alibaba.fastjson.JSONObject)
	 */
	@Override
	public JSONObject getAccountStatus(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
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
		if(null != appLoanAppl.getAccountStatus() && appLoanAppl.getAccountStatus() == 3){ //账户关闭
		    detail.put(Consts.RESULT, ErrorCode.FAILED);
		    detail.put(Consts.RESULT_NOTE, "您的账户已关闭");
		    return detail;
	    }
		return detail;
	}

}
