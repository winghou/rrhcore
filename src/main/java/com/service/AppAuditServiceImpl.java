package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.IfmBaseDictMapper;
import com.service.intf.AppAuditService;
import com.util.StringUtil;

import net.sf.json.JSONObject;

@Service
public class AppAuditServiceImpl implements AppAuditService {

	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	
	@Override
	public JSONObject appAudit(JSONObject params) {
		JSONObject jo = new JSONObject();
		String version = StringUtil.nvl(params.get("appVersion"));   
		String bundleId = StringUtil.nvl(params.get("bundleId"));
		List<Map<String,Object>> ver= ifmBaseDictMapper.selectBaseDict("AUDIT_VERSION_NUMBER");
		
		String[] str1 = version.split("\\.");
		//ios版本
		Map<String, Object> ver1=ver.get(0);
		String[] str2 = StringUtil.toString(ver1.get("ITEM_VALUE")).split("\\.");
		int j= str2.length;
		if(str1.length<str2.length){
			j = str1.length;
		}else if(str1.length>str2.length){
			j = str2.length;
		}
		boolean f = false;
		for(int i=0;i<j;i++){
			if(Double.parseDouble(str1[i])>Double.parseDouble(str2[i])){
				f = true;
				break;
			}else if(Double.parseDouble(str1[i])<Double.parseDouble(str2[i])){
				jo.put("type", "0");
				return jo;
			}
		}
		if(f){
			jo.put("type", "1");
		}else if(str1.length<str2.length){
			jo.put("type", "0");
		}else if(str1.length>str2.length){
			int k = str1.length-str2.length;
			for(int i=0;i<=k;i++){
				if(!"0".equals(str1[str1.length-k])){
					jo.put("type", "1");
					return jo;
				}
				k--;
			}
			jo.put("type", "0");
		}else{
			jo.put("type", "0");
		}
		return jo;
	}

}
