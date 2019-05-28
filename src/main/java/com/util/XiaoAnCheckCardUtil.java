package com.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppXiaoanCheckcardRecordMapper;
import com.dao.IfmBaseDictMapper;
import com.model.AppLoanCtm;
import com.model.AppXiaoanCheckcardRecord;
import com.model.CheckMobileNameIdCardAccountNumber.Req;

public class XiaoAnCheckCardUtil {
	
	public static void checkCard(AppLoanCtm ctm,IfmBaseDictMapper ifmBaseDictMapper,AppXiaoanCheckcardRecordMapper appXiaoanCheckcardRecordMapper){
		//小安验卡
		List<Map<String,Object>> list = ifmBaseDictMapper.selectBaseDict("XIAOAN_CHECK_CARD_SWITCH"); //验卡阀值开关
		List<Map<String,Object>> list2 = ifmBaseDictMapper.selectBaseDict("XIAOAN_URL"); //小安验卡url
		List<Map<String,Object>> list3 = ifmBaseDictMapper.selectBaseDict("XIAOAN_XA_KEY"); 
		AppXiaoanCheckcardRecord appXiaoanCheckcardRecord = new AppXiaoanCheckcardRecord();
		if(list.size()>0 && null != list){
			if(("1").equals(list.get(0).get("ITEM_VALUE"))){
				String url =(String) list2.get(0).get("ITEM_VALUE"); //小安验卡url
				String perPayKey = (String) list3.get(0).get("ITEM_VALUE");// 这里填写xa-key
				RestTemplate restTemplate = new RestTemplate();
			    HttpHeaders header = new HttpHeaders();
			    header.set("xa-key", perPayKey);
				Req req = new Req();
				req.setName(ctm.getCustomName());
			    req.setIdCard(ctm.getIdentityCard());
			    req.setMobile(ctm.getBankPhone());
			    req.setAccountNo(ctm.getBankCard());
			    HttpEntity<Req> h = new HttpEntity<>(req, header);
		     try {
		         ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, h, String.class);
		         JSONObject object = JSON.parseObject(resp.getBody());
		         if(("2000").equals(object.get("code"))){
		        	 JSONObject object2 = (JSONObject) object.get("payload");
			         appXiaoanCheckcardRecord.setApprId(ctm.getApprId());
			         appXiaoanCheckcardRecord.setBankCard(ctm.getBankCard());
			         appXiaoanCheckcardRecord.setCheckTime(new Date());
			         appXiaoanCheckcardRecord.setExtCode(object2.getString("extCode"));
			         appXiaoanCheckcardRecord.setUuid(object.getString("uuid"));
			         appXiaoanCheckcardRecord.setMobile(ctm.getBankPhone());
			         appXiaoanCheckcardRecord.setName(ctm.getCustomName());
			         appXiaoanCheckcardRecord.setExtMessage(object2.getString("extMessage"));
			         appXiaoanCheckcardRecord.setIdCard(ctm.getIdentityCard());
			         appXiaoanCheckcardRecordMapper.insertSelective(appXiaoanCheckcardRecord);
		         }
		       } catch (HttpClientErrorException e) {
		    	   JSONObject object = JSON.parseObject(e.getResponseBodyAsString());
		    	   appXiaoanCheckcardRecord.setApprId(ctm.getApprId());
		           appXiaoanCheckcardRecord.setBankCard(ctm.getBankCard());
		           appXiaoanCheckcardRecord.setCheckTime(new Date());
		           appXiaoanCheckcardRecord.setUuid(object.getString("uuid"));
		           appXiaoanCheckcardRecord.setExtCode(object.getString("code"));
		           appXiaoanCheckcardRecord.setMobile(ctm.getBankPhone());
		           appXiaoanCheckcardRecord.setName(ctm.getCustomName());
		           appXiaoanCheckcardRecord.setExtMessage(object.getString("message"));
		           appXiaoanCheckcardRecord.setIdCard(ctm.getIdentityCard());
		           appXiaoanCheckcardRecordMapper.insertSelective(appXiaoanCheckcardRecord);
		       }
			}
		}
	}
}
