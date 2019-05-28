package com.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppCheckcardRecordMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.IfmBankCardCheckMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppCheckcardRecord;
import com.model.AppLoanCtm;
import com.model.CheckMobileNameIdCardAccountNumber.Req;
import com.model.IfmBankCardCheck;
import com.model.IfmBaseDict;
import com.service.intf.BankCardVerifyService;
import com.util.BankCardCheckUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class BankCardVerifyServiceImpl implements BankCardVerifyService {
	
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private IfmBankCardCheckMapper ifmBankCardCheckMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppCheckcardRecordMapper appCheckcardRecordMapper;

	@Override
	public JSONObject bankCardVerify(JSONObject params) {
		JSONObject detail = new JSONObject();
		String customName = JsonUtil.getJStringAndCheck(params, "customName", null, false, detail);
		String idetityCard = JsonUtil.getJStringAndCheck(params, "idetityCard", null, false, detail);
		String bankCard = JsonUtil.getJStringAndCheck(params, "bankCard", null, false, detail);
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, false, detail);
		String outOrderNo = JsonUtil.getJStringAndCheck(params, "outOrderNo", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}

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
		
		String result = BankCardCheckUtil.bankCardCheck(customName, idetityCard, bankCard, phone,
				outOrderNo, url, privateKey, partnerId, returnUrl, notifyUrl);
		JSONObject object = (JSONObject) JSON.parse(result);
		String success = StringUtil.nvl(object.get("success"));
		if ("false".equals(success)) {
			String resultMessage = StringUtil.nvl(object.get("resultMessage"));
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, resultMessage);
			detail.put("status", "1");
			return detail;
		}
		for (int i = 0; i < 4; i++) {
//			//获取Spring容器的对象
//			WebApplicationContext contextLoader = ContextLoader.getCurrentWebApplicationContext();
//			//1.获取事务控制管理器
//			DataSourceTransactionManager transactionManager = contextLoader.getBean("transactionManager", DataSourceTransactionManager.class);
//			//2.获取事务定义
//			DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//			//3.设置事务隔离级别，开启新事务
//			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
//			//4.获得事务状态
//			TransactionStatus status0 = transactionManager.getTransaction(def);
//			transactionManager.commit(status0);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			IfmBankCardCheck bankCardCheck = ifmBankCardCheckMapper.selectIfmBankCardCheckByOutOrderNo(outOrderNo);
			if (null != bankCardCheck) {
				String cardType = bankCardCheck.getBank_card_type();
				String status = bankCardCheck.getStatus();
				if ("VERIFY_CARD_SUCCESS".equals(status)) {
					if ("DEBIT_CARD".equals(cardType)) {
						detail.put(Consts.RESULT_NOTE, "验卡成功");
						detail.put("status", "0");
						return detail;
					} else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "银行卡必须为借记卡");
						detail.put("status", "1");
						return detail;
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "银行卡验证失败");
					detail.put("status", "1");
					return detail;
				}
			}
		}
		detail.put(Consts.RESULT, ErrorCode.FAILED);
		detail.put(Consts.RESULT_NOTE, "请求超时,请重试！");
		detail.put("status", "5");
		return detail;
	}
	
	/**
	 * 小安银行卡校验外部调用方法
	 */
	@Override
	public JSONObject xiaoanBankCardCheck(JSONObject params) {
		JSONObject detail = new JSONObject();
		String customName = JsonUtil.getJStringAndCheck(params, "customName", null, true, detail);
		String idetityCard = JsonUtil.getJStringAndCheck(params, "idetityCard", null, true, detail);
		String bankCard = JsonUtil.getJStringAndCheck(params, "bankCard", null, true, detail);
		String phone = JsonUtil.getJStringAndCheck(params, "phone", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppLoanCtm loanCtm = appLoanCtmMapper.selectByIdentityCard(idetityCard);
		if (null != phone) {
			List<Map<String,Object>> list2 = ifmBaseDictMapper.selectBaseDict("XIAOAN_URL"); //小安验卡url
			List<Map<String,Object>> list3 = ifmBaseDictMapper.selectBaseDict("XIAOAN_XA_KEY"); 
			AppCheckcardRecord appCheckcardRecord = new AppCheckcardRecord();
			String url =(String) list2.get(0).get("ITEM_VALUE"); //小安验卡url
			String perPayKey = (String) list3.get(0).get("ITEM_VALUE");// 这里填写xa-key
			RestTemplate restTemplate = new RestTemplate();
		    HttpHeaders header = new HttpHeaders();
		    header.set("xa-key", perPayKey);
			Req req = new Req();
			req.setName(customName);
		    req.setIdCard(idetityCard);
		    req.setMobile(phone);
		    req.setAccountNo(bankCard);
		    HttpEntity<Req> h = new HttpEntity<>(req, header);
	     try {
	         ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, h, String.class);
	         JSONObject object = JSON.parseObject(resp.getBody());
	         if(("2000").equals(object.get("code"))){
	        	 JSONObject object2 = (JSONObject) object.get("payload");
	        	 appCheckcardRecord.setApprId(loanCtm.getApprId());
	        	 appCheckcardRecord.setBankCard(bankCard);
	        	 appCheckcardRecord.setCheckTime(new Date());
	        	 appCheckcardRecord.setExtCode(object2.getString("extCode"));
	        	 appCheckcardRecord.setUuid(object.getString("uuid"));
	        	 appCheckcardRecord.setMobile(phone);
	        	 appCheckcardRecord.setName(customName);
	        	 appCheckcardRecord.setExtMessage(object2.getString("extMessage"));
	        	 appCheckcardRecord.setIdCard(idetityCard);
	        	 appCheckcardRecordMapper.insertSelective(appCheckcardRecord);
	        	 if("10000".equals(object2.getString("extCode"))){
	        		 detail.put(Consts.RESULT, ErrorCode.SUCCESS);
	        		 detail.put(Consts.RESULT_NOTE, object2.getString("extMessage"));
	        	 }else{
	        		 detail.put(Consts.RESULT, ErrorCode.FAILED);
	        	 	 detail.put(Consts.RESULT_NOTE, object2.getString("extMessage"));
	        	 }
	         }
	       } catch (HttpClientErrorException e) {
	    	   JSONObject object = JSON.parseObject(e.getResponseBodyAsString());
	    	   detail.put(Consts.RESULT, ErrorCode.FAILED);
			   detail.put(Consts.RESULT_NOTE, object.getString("extMessage"));
	       }
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户非随心花注册用户");
		}
		return detail;
	}

}
