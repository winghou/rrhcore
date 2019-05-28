package com.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppBankCardBinMapper;
import com.dao.AppCheckcardRecordMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppPhoneValicodeMapper;
import com.dao.AppSysSessionMapper;
import com.dao.AppUserMapper;
import com.dao.AppXiaoanCheckcardRecordMapper;
import com.dao.IfmBankCardCheckMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppBankCardBin;
import com.model.AppCheckcardRecord;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppPhoneValicode;
import com.model.AppSysSession;
import com.model.AppUser;
import com.model.IfmBankCardCheck;
import com.model.IfmBaseDict;
import com.model.CheckMobileNameIdCardAccountNumber.Req;
import com.service.intf.YiXingTongBankCardCheckService;
import com.util.BankCardCheckUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class YiXingTongBankCardCheckServiceImpl implements YiXingTongBankCardCheckService {

	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppPhoneValicodeMapper appPhoneValicodeMapper;
	@Autowired
	private IfmBankCardCheckMapper ifmBankCardCheckMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppCheckcardRecordMapper appCheckcardRecordMapper;
	@Autowired
	private AppSysSessionMapper appSysSessionMapper;
	@Autowired
	private AppBankCardBinMapper appBankCardBinMapper;
	/* @author yang.wu
	 * 类名称： YiXingTongBankCardCheckServiceImpl
	 * 创建时间：2017年5月16日 下午3:03:56
	 * @see com.service.intf.YiXingTongBankCardCheckService#bankCardCheck(com.alibaba.fastjson.JSONObject)
	 * 类描述：验卡
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject bankCardCheck(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String bankCard = JsonUtil.getJStringAndCheck(params, "bankCard", null, true, detail);    //银行卡号
		String bankPhone = JsonUtil.getJStringAndCheck(params, "bankPhone", null, true, detail);    //银行卡绑定手机号
		String valiCode = JsonUtil.getJStringAndCheck(params, "valiCode", null, true, detail);    //验证码
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail);   //获取验证码类型
		String bankName = JsonUtil.getJStringAndCheck(params, "bankName", null, true, detail); //银行卡名称
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(mch.getUserName());
		if(null != appLoanAppl.getAccountStatus() && appLoanAppl.getAccountStatus() == 3){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "你的账户已关闭");
			return detail;
		}
		List<AppBankCardBin> appBankCardBinList = appBankCardBinMapper.selectByBankCode(bankCard.substring(0, 6),bankCard.substring(0, 8));
		if(appBankCardBinList.size() <= 0){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "该银行卡不支持银行卡验证");
			return detail;
		}
		if(!appBankCardBinList.get(0).getBank_name().equals(bankName)){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "银行卡与银行卡类型不一致");
			return detail;
		}
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		String customName = appLoanCtm.getCustomName();
		String identityCard = appLoanCtm.getIdentityCard();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", bankPhone);
		map.put("status", type);
		AppPhoneValicode appPhoneValicode = appPhoneValicodeMapper.selectByPhoneAndStatus(map);
		if(appPhoneValicode == null){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "验证码错误");
			return detail;
		}
		long creatTime = appPhoneValicode.getCreatTime().getTime();
		Date date = new Date();
		long nowTime = date.getTime();
		if(nowTime - creatTime <= 180000){
			if(valiCode.equals(appPhoneValicode.getValicode())){
				List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("CHECKCARD_SWITCH"); //验卡开关  1 易行通 0 小安科技
				if(("1").equals(list.get(0).get("ITEM_VALUE"))){
					List<IfmBankCardCheck> bankCardChecks = ifmBankCardCheckMapper.selectIfmBankCardByCertNoThisDay(identityCard);
					if(bankCardChecks.size()<3){
						String outOrderNo = UUID.randomUUID().toString().replace("-", "");   //易行通订单号
						// 获取易行通URL
						Map<String, Object> map0 = new HashMap<>();
						map0.put("dataType", "YIXINGTONG");
						map0.put("perion", "yixingtong_url");
						IfmBaseDict baseDict0 = ifmBaseDictMapper.selectfWFAndRate(map0);
						String url = baseDict0.getItemValue();
						// 获取易行通安全码
						Map<String, Object> map1 = new HashMap<>();
						map1.put("dataType", "YIXINGTONG");
						map1.put("perion", "privatekey");
						IfmBaseDict baseDict1 = ifmBaseDictMapper.selectfWFAndRate(map1);
						String privateKey = baseDict1.getItemValue();
						// 获取易行通商户ID
						Map<String, Object> map2 = new HashMap<>();
						map2.put("dataType", "YIXINGTONG");
						map2.put("perion", "partnerid");
						IfmBaseDict baseDict2 = ifmBaseDictMapper.selectfWFAndRate(map2);
						String partnerId = baseDict2.getItemValue();
						//获取易行通同步回调URL
						Map<String, Object> map3 = new HashMap<>();
						map3.put("dataType", "YIXINGTONG");
						map3.put("perion", "return_url");
						IfmBaseDict baseDict3 = ifmBaseDictMapper.selectfWFAndRate(map3);
						String returnUrl = baseDict3.getItemValue();
						//获取易行通异步回调URL
						Map<String, Object> map4 = new HashMap<>();
						map4.put("dataType", "YIXINGTONG");
						map4.put("perion", "notify_url");
						IfmBaseDict baseDict4 = ifmBaseDictMapper.selectfWFAndRate(map4);
						String notifyUrl = baseDict4.getItemValue();
						
						String result = BankCardCheckUtil.bankCardCheck(customName, identityCard, bankCard, bankPhone,
								outOrderNo, url, privateKey, partnerId, returnUrl, notifyUrl);
						JSONObject object = (JSONObject) JSON.parse(result);
						String success = StringUtil.nvl(object.get("success"));
						if ("false".equals(success)) {
							String resultMessage = StringUtil.nvl(object.get("resultMessage"));
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, resultMessage);
						}else{
							detail.put("outOrderNo", outOrderNo);
						}
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您今日绑卡已达上线，请次日重新验证绑卡信息。");
					}
				}else{
					//小安验卡
					List<AppCheckcardRecord> bankCardChecks = appCheckcardRecordMapper.selectAppCheckcardRecordThisDay(identityCard);
					if(bankCardChecks.size()<3){
						/*String uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
						AppCheckcardRecord appCheckcardRecord = new AppCheckcardRecord();
						appCheckcardRecord.setApprId(appLoanCtm.getApprId());
			        	appCheckcardRecord.setBankCard(bankCard);
			        	appCheckcardRecord.setCheckTime(new Date());
			        	appCheckcardRecord.setExtCode("10000");
			        	appCheckcardRecord.setUuid(uuid);
			        	appCheckcardRecord.setMobile(bankPhone);
			        	appCheckcardRecord.setName(appLoanCtm.getCustomName());
			        	appCheckcardRecord.setExtMessage("没有经过三方验卡的操作");
			        	appCheckcardRecord.setIdCard(appLoanCtm.getIdentityCard());
				        appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
			        	detail.put("outOrderNo", uuid);
			        	return detail;*/
						List<Map<String,Object>> list2 = ifmBaseDictMapper.selectBaseDict("XIAOAN_URL"); //小安验卡url
						List<Map<String,Object>> list3 = ifmBaseDictMapper.selectBaseDict("XIAOAN_XA_KEY"); 
						AppCheckcardRecord appCheckcardRecord = new AppCheckcardRecord();
						String url =(String) list2.get(0).get("ITEM_VALUE"); //小安验卡url
						String perPayKey = (String) list3.get(0).get("ITEM_VALUE");// 这里填写xa-key
						RestTemplate restTemplate = new RestTemplate();
					    HttpHeaders header = new HttpHeaders();
					    header.set("xa-key", perPayKey);
						Req req = new Req();
						req.setName(appLoanCtm.getCustomName());
					    req.setIdCard(appLoanCtm.getIdentityCard());
					    req.setMobile(bankPhone);
					    req.setAccountNo(bankCard);
					    HttpEntity<Req> h = new HttpEntity<>(req, header);
				     try {
				         ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, h, String.class);
				         JSONObject object = JSON.parseObject(resp.getBody());
				         if(("2000").equals(object.get("code"))){
				        	 JSONObject object2 = (JSONObject) object.get("payload");
				        	 appCheckcardRecord.setApprId(appLoanCtm.getApprId());
				        	 appCheckcardRecord.setBankCard(bankCard);
				        	 appCheckcardRecord.setCheckTime(new Date());
				        	 appCheckcardRecord.setExtCode(object2.getString("extCode"));
				        	 appCheckcardRecord.setUuid(object.getString("uuid"));
				        	 appCheckcardRecord.setMobile(bankPhone);
				        	 appCheckcardRecord.setName(appLoanCtm.getCustomName());
				        	 appCheckcardRecord.setExtMessage(object2.getString("extMessage"));
				        	 appCheckcardRecord.setIdCard(appLoanCtm.getIdentityCard());
					         appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
				        	 detail.put("outOrderNo", object.getString("uuid"));
				        	 return detail;
				         }else{
				        	 detail.put(Consts.RESULT, ErrorCode.FAILED);
							 detail.put(Consts.RESULT_NOTE, object.getString("message"));
				         }
				       } catch (HttpClientErrorException e) {
				    	   JSONObject object = JSON.parseObject(e.getResponseBodyAsString());
				    	   detail.put(Consts.RESULT, ErrorCode.FAILED);
						   detail.put(Consts.RESULT_NOTE, object.getString("message"));
				       }
					}else{
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "您今日绑卡已达上线，请次日重新验证绑卡信息。");
					}
				}
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "验证码错误");
			}
		}else{
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "验证码超时");
		}
		return detail;
	}

	/* @author yang.wu
	 * 类名称： YiXingTongBankCardCheckServiceImpl
	 * 创建时间：2017年5月16日 下午3:03:36
	 * @see com.service.intf.YiXingTongBankCardCheckService#querySupportedBank(com.alibaba.fastjson.JSONObject)
	 * 类描述：查询支持的银行卡
	 */
	@Override
	public JSONObject querySupportedBank(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		List<Map<String, Object>> list = ifmBaseDictMapper.selectBankName("BANK_NAME");
		JSONArray supportBankCard = new JSONArray();
		for (Map<String, Object> map : list) {
			JSONObject bankList = new JSONObject();
			bankList.put("bankName", map.get("ITEM_VALUE")); //银行卡名称
			bankList.put("bankCode", map.get("ITEM_KEY")); //银行卡编号
			supportBankCard.add(bankList);
		}
		detail.put("supportBankCard",supportBankCard);
		return detail;
	}

	/**
	 * @author yang.wu
	 * @Description: 查询异步回调验卡结果
	 * @return JSONObject
	 * @date 2017年7月12日下午5:24:18
	 */
	@Override
	public JSONObject queryVerifyCardResult(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String outOrderNo = JsonUtil.getJStringAndCheck(params, "outOrderNo", null, true, detail); //验卡商户订单号
		String bankName = JsonUtil.getJStringAndCheck(params, "bankName", null, true, detail); //银行卡名称
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByitemCode(mch.getUserName());
		List<Map<String,Object>> list2 = ifmBaseDictMapper.selectBaseDict("CHECKCARD_SWITCH"); //验卡开关  1 易行通 0 小安科技
		if(("1").equals(list2.get(0).get("ITEM_VALUE"))){
			IfmBankCardCheck bankCardCheck = ifmBankCardCheckMapper.selectIfmBankCardCheckByOutOrderNo(outOrderNo);
			if(bankCardCheck != null){
				String cardType = bankCardCheck.getBank_card_type();
				String status = bankCardCheck.getStatus();
				if ("VERIFY_CARD_SUCCESS".equals(status)) {
					if("DEBIT_CARD".equals(cardType)) {
						IfmBaseDict ifmBaseDict = new IfmBaseDict();
						ifmBaseDict.setDataType("BANK_NAME");
						ifmBaseDict.setOutDataFrom(bankCardCheck.getBank_code());
						List<IfmBaseDict> list = ifmBaseDictMapper.selectTypeBankCode(ifmBaseDict);
						AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
						AppLoanCtm appLoanCtm2 = new AppLoanCtm();
						Date date2 = new Date();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						appLoanCtm2.setBind_card_time(dateFormat.format(date2));    //绑卡时间
						appLoanCtm2.setId(appLoanCtm.getId());
						appLoanCtm2.setBankCard(bankCardCheck.getBank_no());
						appLoanCtm2.setBankPhone(bankCardCheck.getPhone_no());
						appLoanCtm2.setBank(list.get(0).getItemKey());
						appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm2);
						appSysSessionMapper.updateByUserId(mch.getUserid());
						detail.put("message", "验卡成功");
						detail.put("status", "1");
					}else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "银行卡必须为借记卡");
						detail.put("status", "2");
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "银行卡验证失败");
					detail.put("status", "2");
				}
			}else{
				detail.put("status", "0");
			}
		}else if(("2").equals(list2.get(0).get("ITEM_VALUE"))){
			AppCheckcardRecord bankCardCheck = appCheckcardRecordMapper.selectIfmBankCardCheckByUuid(outOrderNo);
			if(bankCardCheck != null){
				String status = bankCardCheck.getExtCode();
				if ("1".equals(status)) {
					IfmBaseDict ifmBaseDict = new IfmBaseDict();
					ifmBaseDict.setDataType("BANK_NAME");
					ifmBaseDict.setItemValue(bankName);
					List<IfmBaseDict> list = ifmBaseDictMapper.selectBankCode(ifmBaseDict);
					AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
					AppLoanCtm appLoanCtm2 = new AppLoanCtm();
					Date date2 = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					appLoanCtm2.setBind_card_time(dateFormat.format(date2));    //绑卡时间
					appLoanCtm2.setId(appLoanCtm.getId());
					appLoanCtm2.setBankCard(bankCardCheck.getBankCard());
					appLoanCtm2.setBankPhone(bankCardCheck.getMobile());
					appLoanCtm2.setBank(list.get(0).getItemKey());
					appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm2);
					appSysSessionMapper.updateByUserId(mch.getUserid());
					detail.put("message", "验卡成功");
					detail.put("status", "1");
				}else if ("2".equals(status)){
					detail.put("status", "0");
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "银行卡验证失败");
					detail.put("status", "2");
				}
			}else{
				detail.put("status", "0");
			}
		}else{
			AppCheckcardRecord appCheckcardRecord = appCheckcardRecordMapper.selectIfmBankCardCheckByUuid(outOrderNo);
			if(appCheckcardRecord != null){
				if(("10000").equals(appCheckcardRecord.getExtCode())){
					IfmBaseDict ifmBaseDict = new IfmBaseDict();
					List<Map<String,Object>> list3 = ifmBaseDictMapper.selectBaseDict("QUERY_BANKCARD_SWITCH"); //卡BIN开关 1 打开 0 关闭
					List<IfmBaseDict> list = null;
					if(("1").equals(list3.get(0).get("ITEM_VALUE"))){
						/*String name = BankUtil.getNameOfBank(appCheckcardRecord.getBankCard());
						if(("").equals(name)){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "该银行卡不支持银行卡验证");
							return detail;
						}
						String bankname = name.substring(0, name.indexOf("·"));
						ifmBaseDict.setDataType("BANK_NAME");
						ifmBaseDict.setItemValue(bankname);
						list = ifmBaseDictMapper.selectBankCode(ifmBaseDict);*/
						List<AppBankCardBin> appBankCardBinList = appBankCardBinMapper.selectByBankCode(appCheckcardRecord.getBankCard().substring(0, 6),appCheckcardRecord.getBankCard().substring(0, 8));
						if(appBankCardBinList.size() <= 0){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "该银行卡不支持银行卡验证");
							return detail;
						}
						ifmBaseDict.setDataType("BANK_NAME");
						ifmBaseDict.setItemValue(appBankCardBinList.get(0).getBank_name());
						list = ifmBaseDictMapper.selectBankCode(ifmBaseDict);	
						if(!appBankCardBinList.get(0).getBank_name().equals(bankName)){
							detail.put(Consts.RESULT, ErrorCode.FAILED);
							detail.put(Consts.RESULT_NOTE, "银行卡与银行卡类型不一致");
							return detail;
						}
					}else{
						ifmBaseDict.setDataType("BANK_NAME");
						ifmBaseDict.setItemValue(bankName);
						list = ifmBaseDictMapper.selectBankCode(ifmBaseDict);
					}
					AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
					AppLoanCtm appLoanCtm2 = new AppLoanCtm();
					Date date2 = new Date();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					appLoanCtm2.setBind_card_time(dateFormat.format(date2));    //绑卡时间
					appLoanCtm2.setId(appLoanCtm.getId());
					appLoanCtm2.setBankCard(appCheckcardRecord.getBankCard());
					appLoanCtm2.setBankPhone(appCheckcardRecord.getMobile());
					appLoanCtm2.setBank(list.get(0).getItemKey());
					appLoanCtmMapper.updateByPrimaryKeySelective(appLoanCtm2);
					appSysSessionMapper.updateByUserId(mch.getUserid());
					detail.put("message", "验卡成功");
					detail.put("status", "1");
				}else{
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "银行卡验证失败");
					detail.put("status", "2");
				}
			}else{
				detail.put("status", "0");
			}
		}
		return detail;
	}

	/**
	 * @author yang.wu
	 * @Description: 查看绑卡信息或进入绑卡页面
	 * @return JSONObject
	 * @date 2017年7月12日下午6:54:40
	 */
	@Override
	public JSONObject lookBindBankinfo(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail); // 0 没绑卡   1 有绑卡   2更换银行卡
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(mch.getUserid());
		AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
		Date date = new Date();
		long nowDate = date.getTime();
		long tieNoTime;
		if(type.equals("2")){
			if(null != appLoanAppl.getAccountStatus() && appLoanAppl.getAccountStatus() == 3){
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "你的账户已关闭");
				return detail;
			}
			/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(appLoanCtm.getBind_card_time().equals("") || appLoanCtm.getBind_card_time() == null){ //处理老用户在上版本时绑卡时间为空，但列为已经绑过卡问题
				tieNoTime = 0;
			}else{
				tieNoTime = dateFormat.parse(appLoanCtm.getBind_card_time()).getTime();
			}
			if(nowDate - tieNoTime > 30*24*3600*1000L){
				IfmBaseDict ifmBaseDict = new IfmBaseDict();
				ifmBaseDict.setDataType("BANK_NAME");
				ifmBaseDict.setItemKey(appLoanCtm.getBank());
				ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
				detail.put("customName", appLoanCtm.getCustomName()); //持卡人
				detail.put("bankCard", appLoanCtm.getBankCard());  //银行卡号
				detail.put("bankName", ifmBaseDict.getItemValue()); //银行卡名称
				detail.put("bankPhone", appLoanCtm.getBankPhone()); //绑卡手机号
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "本月已更换银行卡，请次月更换");
			}*/
			AppSysSession appSysSession = appSysSessionMapper.selectByUserId(appLoanAppl.getUserId());
			if(appSysSession.getBank_count() < 3){
				IfmBaseDict ifmBaseDict = new IfmBaseDict();
				ifmBaseDict.setDataType("BANK_NAME");
				ifmBaseDict.setItemKey(appLoanCtm.getBank());
				ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
				detail.put("customName", appLoanCtm.getCustomName()); //持卡人
				detail.put("bankCard", appLoanCtm.getBankCard());  //银行卡号
				detail.put("bankName", ifmBaseDict.getItemValue()); //银行卡名称
				detail.put("bankPhone", appLoanCtm.getBankPhone()); //绑卡手机号
			}else{
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "每月限定3次");
			}
		}else if(type.equals("0")){
			detail.put("customName", appLoanCtm.getCustomName()); //持卡人
			detail.put("bankPhone", appLoanAppl.getItemCode()); //用户第一次绑卡默认将注册手机带入
		}else{
			IfmBaseDict ifmBaseDict = new IfmBaseDict();
			ifmBaseDict.setDataType("BANK_NAME");
			ifmBaseDict.setItemKey(appLoanCtm.getBank());
			ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
			String bankCard = appLoanCtm.getBankCard();
			StringBuffer bankCard2 = new StringBuffer();
			String bankCard3;
			bankCard3 = bankCard.substring(bankCard.length()-4, bankCard.length());
			bankCard2.append("**** **** **** ").append(bankCard3);
			detail.put("bankCard", bankCard2);   //银行卡号
			detail.put("bankIcon", ifmBaseDict.getXh()); //银行卡图片
			detail.put("bankName", ifmBaseDict.getItemValue()); //银行卡名称
		}
		return detail;
	}
}
