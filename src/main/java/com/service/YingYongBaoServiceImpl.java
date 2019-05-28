package com.service;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppSysLoginLogMapper;
import com.frame.Consts;
import com.frame.RedisService;

import com.model.AppSysLoginLog;
import com.service.intf.YingYongBaoService;
import com.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class YingYongBaoServiceImpl implements YingYongBaoService {

	public static Logger logger = Logger.getLogger(YingYongBaoServiceImpl.class);

	private static final String advertiserId = "5249577";
	private static final String appId = "1105742645";
//	private static final String encryptKey = "BAAAAAAAAAAAUBop";
	private static final String url = "https://t.gdt.qq.com/conv/app/" + appId + "/conv";
	private static final String signKey = "43c0b306ec6d9ab3";
	private static final String encver = "1.0";
	private static final String conv_type = "MOBILEAPP_ACTIVITE";

	@Autowired
	private RedisService redisService;

	@Autowired
	private AppSysLoginLogMapper appSysLoginLogMapper;

	@Override
	public JSONObject updateCurrentDataToYYB(JSONObject params) {
		JSONObject detail = new JSONObject();
		String app_type = "";
		String conv_time = "";
		String deviceType =JsonUtil.getJStringAndCheck(params, "deviceType", null, true, detail);
		String client_ip = JsonUtil.getJStringAndCheck(params, "client_ip", null, true, detail);
		String muid  = JsonUtil.getJStringAndCheck(params, "deviceId", null, true, detail).toLowerCase();		
		// 加密muid
		muid = MD5Utils.MD5Encrpytion(muid).toLowerCase();
		String timestamp = String.valueOf(new Date().getTime());
		int length = timestamp.length();
		if (length > 3) {
			conv_time = timestamp.substring(0, length - 3);
		} else {
			conv_time = timestamp;
		}
		
		if ("1".equals(deviceType)) {
			app_type="ANDROID";
		} else {
			detail.put(Consts.RESULT_NOTE, "设备类型错误");
		}
		// 参数缺失,直接返回
		if (detail.containsKey(Consts.RESULT_NOTE)) {
			return detail;
		}
		// 设备不是首次激活,直接返回
		if (redisService.contiansValue("muidSet", muid)) {
			detail.put(Consts.RESULT_NOTE, "设备不是首次激活");
			return detail;
		} else {
			redisService.sadd("muidSet", muid);
		}
		//拼接加密字符串
		String md5Str = "app_type=" + app_type + "&click_id=&client_ip=" + client_ip + "&conv_time=" + conv_time
				+ "&muid=" + muid + "&sign_key=" + signKey;
		// 加密
		String encstr = MD5Utils.MD5Encrpytion(md5Str).toLowerCase();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("click_id", "");
		map.put("appid", this.appId);
		map.put("muid", muid);
		map.put("conv_time", conv_time);
		map.put("client_ip", client_ip);
		map.put("encstr", encstr);
		map.put("encver", this.encver);
		map.put("advertiser_id", this.advertiserId);
		map.put("app_type", app_type);
		map.put("conv_type", this.conv_type);

		String res = updateData(map);
		if (!"0".equals(res)) {
			logger.error("上报激活数据到应用宝失败，错误码:" + res + ";  激活数据为：" + params);
		} else {
			logger.info("上报激活数据到应用宝成功");
		}
		return null;

	}

	@Override
	public void updateLastMonthDataToYYB() {

		List<AppSysLoginLog> list = appSysLoginLogMapper.getLastMonthRRHLogninData();
		if (list != null && !list.isEmpty()) {
			for (AppSysLoginLog log : list) {
				ThreadPoolUtil.exec(new updateDataYYBThread(log));
			}
		}
	}

	// 上传数据
	private String updateData(Map<String, String> map) {

		String paramStr = "";
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = iterator.next();
			String key = entry.getKey();
			String value = entry.getValue();
			paramStr += key + "=" + value + "&";
		}
		// 去掉最后一个&
		paramStr = paramStr.substring(0, paramStr.lastIndexOf("&"));
		System.out.println(new Date());
		String res = HttpClientUtil.doPostStr(this.url, paramStr);
		System.out.println(new Date());
		if (res != null) {
			try {
				JSONObject jsonObject = JSONObject.parseObject(res);
				String ret = StringUtil.nvl(jsonObject.get("ret"));
				return ret;
			} catch (Exception e) {
				logger.error("字符串转json失败，错误详情:" + e);
			}

		}
		return "";

	}

	// 上传激活数据到应用宝的多线程
	class updateDataYYBThread implements Runnable {
		private AppSysLoginLog log;
		private static final String advertiserId = "5249577";
		private static final String appId = "1105742645";
		// private static final String encryptKey = "BAAAAAAAAAAAUBop";
		private static final String url = "https://t.gdt.qq.com/conv/app/" + appId + "/conv";
		private static final String signKey = "43c0b306ec6d9ab3";
		private static final String encver = "1.0";
		private static final String conv_type = "MOBILEAPP_ACTIVITE";

		public updateDataYYBThread(AppSysLoginLog log) {
			this.log = log;
		}

		@Override
		public void run() {
			try {
				String app_type = "ANDROID";
				String client_ip = log.getAddress();
				String conv_time = String.valueOf(log.getLogindate().getTime());
				int length = conv_time.length();
				if (length > 3) {
					conv_time = conv_time.substring(0, length - 3);
				}
				// 加密muid
				String muid = log.getDeviceId().toLowerCase();
				muid = MD5Utils.MD5Encrpytion(muid).toLowerCase();
				//拼接加密字符串
				String md5Str = "app_type=" + app_type + "&click_id=&client_ip=" + client_ip + "&conv_time=" + conv_time
						+ "&muid=" + muid + "&sign_key=" + signKey;
				// 加密
				String encstr = MD5Utils.MD5Encrpytion(md5Str).toLowerCase();
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

				map.put("click_id", "");
				map.put("appid", this.appId);
				map.put("muid", muid);
				map.put("conv_time", conv_time);
				map.put("client_ip", client_ip);
				map.put("encstr", encstr);
				map.put("encver", this.encver);
				map.put("advertiser_id", this.advertiserId);
				map.put("app_type", app_type);
				map.put("conv_type", this.conv_type);
				String res = updateData(map);
				if (!"0".equals(res)) {
					logger.error("上报激活数据到应用宝失败，错误码:" + res + ";  激活数据为：" + log);
				} else {
					logger.info("上报激活数据到应用宝成功");
				}
			} catch (Exception e) {
				logger.error("激活数据缺失，错误详情:" + e);
			}
		}
	}

}
