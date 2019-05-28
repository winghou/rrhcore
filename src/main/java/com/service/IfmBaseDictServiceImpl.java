package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppAdvertMapper;
import com.dao.AppMessageMapper;
import com.dao.IfmBaseDictMapper;
import com.model.AppAdvert;
import com.model.AppMessage;
import com.service.intf.IfmBaseDictServiceIntf;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service("ifmBaseDictService")
public class IfmBaseDictServiceImpl implements IfmBaseDictServiceIntf {
	
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	@Autowired
	private AppAdvertMapper appAdvertMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;

	/**
	 * 查询服务URL(第一个请求接口)
	 */
	@Override
	public JSONObject queryServiceUrl(JSONObject params) {
		JSONObject detail=new JSONObject();
		String version=JsonUtil.getJStringAndCheck(params, "version", null, false, detail);
		String from=JsonUtil.getJStringAndCheck(params, "from", null, false, detail);
		String isCode=JsonUtil.getJStringAndCheck(params, "isCode", null, false, detail);
		List<Map<String,Object>> url= ifmBaseDictMapper.selectBaseDict("SERVICE_API_URL");
		List<Map<String,Object>> ver= ifmBaseDictMapper.selectBaseDict("VERSION_NUMBER");
		List<Map<String,Object>> anzhuogx= ifmBaseDictMapper.selectBaseDict("ANZHUO_GX");
		List<Map<String,Object>> updateUrl = ifmBaseDictMapper.selectBaseDict("MCH_UPDATE_URL");
		List<Map<String,Object>> updateStatus = ifmBaseDictMapper.selectBaseDict("MCH_UPDATE_STATUS");
		Map<String, Object> url1=url.get(0);
		//ios版本
		Map<String, Object> ver1=ver.get(0);
		//安卓版本
		Map<String, Object> ver2=ver.get(1);
		//iOS专业版版本
		Map<String, Object> ver3=ver.get(2);
		//安卓专业版版本
		Map<String, Object> ver4=ver.get(3);
		//iOS企业版版本
		Map<String, Object> ver5 = ver.get(4);
		Map<String, Object> gx=anzhuogx.get(0);
		//安卓更新地址
		Map<String, Object> azUpUrl = updateUrl.get(0);
		//iOS更新地址
		Map<String, Object> iosUpUrl = updateUrl.get(1);
		//安卓专业版更新
		Map<String, Object> azUpUrlPro = updateUrl.get(2);
		//iOS专业版更新
		Map<String, Object> iosUpUrlPro = updateUrl.get(3);
		//iOS企业版更新
		Map<String, Object> iosUpUrlEnt = updateUrl.get(4);
		String iosUpdateStatus = StringUtil.nvl(updateStatus.get(0).get("ITEM_VALUE")); //iOS更新状态
		String androidUpdateStatus = StringUtil.nvl(updateStatus.get(1).get("ITEM_VALUE")); //安卓更新状态
		String iosProUpdateStatus = StringUtil.nvl(updateStatus.get(2).get("ITEM_VALUE")); //iOS专业版更新状态
		String androidProUpdateStatus = StringUtil.nvl(updateStatus.get(3).get("ITEM_VALUE")); //安卓专业版更新状态
		String iosEntUpdateStatus = StringUtil.nvl(updateStatus.get(4).get("ITEM_VALUE"));  //ios企业版更新状态
		//detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
		//通过ITEM_KEY值去判断是否更新 0：不更新 1：强更新 2：可更新或不更新
		//iOS普通版
		if("0".equals(from)){
			if("test".equals(StringUtil.toString(url1.get("XH")))){
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver1.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else if(Double.parseDouble(version)>Double.parseDouble(StringUtil.toString(ver1.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("OUT_DATA_FROM")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus","1");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrl.get("ITEM_VALUE")));
				}
			}else{
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver1.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus",iosUpdateStatus);
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrl.get("ITEM_VALUE")));
				}
			}
		}else if("1".equals(from)){ //安卓普通版
			if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver2.get("ITEM_VALUE")))){
				detail.put("updateStatus","0");
				detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
			}else {
				detail.put("newVersion", "新版本");
				detail.put("updateStatus",androidUpdateStatus);
				detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
				detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
				detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
				detail.put("updateUrl", StringUtil.toString(azUpUrl.get("ITEM_VALUE")));
			}
		}else if("2".equals(from)){ //iOS专业版
			if("test".equals(StringUtil.toString(url1.get("XH")))){
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver3.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else if(Double.parseDouble(version)>Double.parseDouble(StringUtil.toString(ver3.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("OUT_DATA_FROM")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus","1");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrlPro.get("ITEM_VALUE")));
				}
			}else{
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver3.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus",iosProUpdateStatus);
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrlPro.get("ITEM_VALUE")));
				}
			}
		}else if("3".equals(from)){ //安卓专业版
			if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver4.get("ITEM_VALUE")))){
				detail.put("updateStatus","0");
				detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
			}else {
				detail.put("newVersion", "新版本");
				detail.put("updateStatus",androidProUpdateStatus);
				detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
				detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
				detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
				detail.put("updateUrl", StringUtil.toString(azUpUrlPro.get("ITEM_VALUE")));
			}
		}else if("5".equals(from)){  //ios企业版
			if("test".equals(StringUtil.toString(url1.get("XH")))){
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver5.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else if(Double.parseDouble(version)>Double.parseDouble(StringUtil.toString(ver5.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("OUT_DATA_FROM")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus","1");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrlEnt.get("ITEM_VALUE")));
				}
			}else{
				if(Double.parseDouble(version)==Double.parseDouble(StringUtil.toString(ver5.get("ITEM_VALUE")))){
					detail.put("updateStatus","0");
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
				}else {
					detail.put("newVersion", "新版本");
					detail.put("updateStatus",iosEntUpdateStatus);
					detail.put("serviceUrl", StringUtil.toString(url1.get("ITEM_VALUE")));
					detail.put("size",  StringUtil.toString(gx.get("ITEM_KEY")));
					detail.put("time", StringUtil.toString(gx.get("ITEM_VALUE")));
					detail.put("comtent", StringUtil.toString(gx.get("DICT_DESC")));
					detail.put("updateUrl", StringUtil.toString(iosUpUrlEnt.get("ITEM_VALUE")));
				}
			}
		
		}
		JSONArray dataList = new JSONArray();
		List<AppAdvert> appAdverts=appAdvertMapper.queryAdvrByModule("0");
		if (null != appAdverts && appAdverts.size() > 0) {
			for (AppAdvert appAdvert : appAdverts) {
				String shareStatus = "0";
				JSONObject jo = new JSONObject();
				jo.put("url", StringUtil.nvl(appAdvert.getUrl()));
				jo.put("h5_url", StringUtil.nvl(appAdvert.getPicJumpUrl()));
				String messageId = StringUtil.nvl(appAdvert.getMessageId());
				String title = "";
				String trackStatus = "0";
				if (!"".equals(messageId)) {
					AppMessage appMessage = appMessageMapper.selectByPrimaryKey(Integer.parseInt(messageId));
					String shareUrl = StringUtil.nvl(appMessage.getShareUrl());
					String isTrack = StringUtil.nvl(appMessage.getIsTrack());
					if(!"".equals(shareUrl)){
						shareStatus = "1";
					}
					if("1".equals(isTrack)){
						trackStatus = "1";
					}
					title = appMessage.getTitle();
				} else {
					messageId = "0";
				}
				jo.put("messageId", messageId);
				jo.put("title", title);
				jo.put("shareStatus", shareStatus);
				jo.put("trackStatus", trackStatus);
				dataList.add(jo);
			}
		}else{
			JSONObject jo = new JSONObject();
			jo.put("url", "");
			jo.put("h5_url", "");
			jo.put("messageId", "0");
			jo.put("title", "");
			jo.put("shareStatus", "0");
			jo.put("trackStatus", "0");
			dataList.add(jo);
		}
		detail.put("dataList", dataList);
		if(null != isCode && !"".equals(isCode) && "false".equals(isCode)){
			detail.put("isCode", isCode);
		}
		return detail;
	}

}
