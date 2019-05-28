package com.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frame.Consts;
import com.model.SmsBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonUtil {
	/**
	 * 得到json中string类型的值
	 *
	 * @param jo
	 * @param key
	 * @return
	 */
	public static String getJString(JSONObject jo, String key) {
		if (jo == null) {
			return "";
		}
		if (jo.containsKey(key)) {
			return jo.get(key).toString();
		}
		return "";
	}

	/**
	 * 得到json中string类型的值
	 *
	 * @param jo
	 * @param key
	 * @return
	 */
	public static String getJString(JSONObject jo, String key, String defaultVal) {
		if (jo == null) {
			return defaultVal;
		}
		if (jo.containsKey(key)) {
			return "".equals(jo.get(key).toString()) ? defaultVal : jo.get(key).toString();
		}
		return defaultVal;
	}

	/**
	 * 从JSON对象中获取字符串并做必要性判断
	 *
	 * @param jsonObject
	 * @param key
	 * @param detail
	 * @return
	 */
	public static String getJStringAndCheck(JSONObject jsonObject, String key, JSONObject detail) {
		return getJStringAndCheck(jsonObject, key, null, true, detail, ErrorCode.ERROR_NOTE_TEMPLATE);
	}

	public static String getJStringAndCheck(JSONObject jsonObject, String key, boolean required, JSONObject detail) {
		return getJStringAndCheck(jsonObject, key, null, required, detail, ErrorCode.ERROR_NOTE_TEMPLATE);
	}

	public static String getJStringAndCheck(JSONObject jsonObject, String key, String defaultVal, boolean required, JSONObject detail) {
		return getJStringAndCheck(jsonObject, key, defaultVal, required, detail, ErrorCode.ERROR_NOTE_TEMPLATE);
	}

	/**
	 * 获取字符串值并做必要性检查
	 *
	 * @param jsonObject
	 * @param key
	 * @param defaultVal
	 * @param required      true必填，false非必填
	 * @param detail        返回的JsonObject
	 * @param errorNoteTemp 必要性检查报错的模版
	 * @return
	 */
	public static String getJStringAndCheck(JSONObject jsonObject, String key, String defaultVal, boolean required, JSONObject detail, String errorNoteTemp) {
		String defaultValue = "";
		if (defaultVal != null) {
			defaultValue = defaultVal;
		}
		String jString = (jsonObject != null && jsonObject.containsKey(key) && !Utils.isBnull(jsonObject.get(key).toString().trim())) ? jsonObject.get(key).toString().trim() : defaultValue;
		if (required) {
			if (Utils.isBnull(jString)) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				if (errorNoteTemp.contains("${filedName}")) {
					detail.put(Consts.RESULT_NOTE, errorNoteTemp.replace("${filedName}", key));
				} else {
					detail.put(Consts.RESULT_NOTE, errorNoteTemp);
				}
			}
		}
		return (jsonObject != null && jsonObject.containsKey(key) && !Utils.isBnull(jsonObject.get(key).toString().trim())) ? jsonObject.get(key).toString().trim() : defaultValue;
	}

	/**
	 * 以int形式返回JSONObject中的值
	 */
	public static Integer getJInt(JSONObject JSONObject, String key, Integer defaultVal) {
		Integer defaultValue = 0;
		if (defaultVal != null) {
			defaultValue = defaultVal;
		}
		return JSONObject.containsKey(key) ? ("".equals(JSONObject.getString(key)) ? defaultValue : JSONObject.getInteger(key)) : defaultValue;
	}

	public static Integer getJInt(JSONObject JSONObject, String key) {
		return getJInt(JSONObject, key, null);
	}

	/**
	 * 以int形式返回JSONObject中的值
	 */
	public static Integer getJIntDefault(JSONObject JSONObject, String key, Integer defaultVal) {
		return JSONObject.getInteger(key) == null ? defaultVal : JSONObject.getInteger(key);
	}

	public static Integer getJIntAndCheck(JSONObject jsonObject, String key, Integer defaultVal, boolean required, JSONObject detail) {
		return getJIntAndCheck(jsonObject, key, defaultVal, required, detail, ErrorCode.ERROR_NOTE_TEMPLATE);
	}

	/**
	 * 获取字符串值并做必要性检查
	 *
	 * @param jsonObject
	 * @param key
	 * @param defaultVal
	 * @param required      true必填，false非必填
	 * @param detail        返回的JsonObject
	 * @param errorNoteTemp 必要性检查报错的模版
	 * @return
	 */
	public static Integer getJIntAndCheck(JSONObject jsonObject, String key, Integer defaultVal, boolean required, JSONObject detail, String errorNoteTemp) {
		Integer jInt = getJInt(jsonObject, key, defaultVal);
		if (required) {
			if (Utils.isBnull(jInt)) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				if (errorNoteTemp.contains("${filedName}")) {
					detail.put(Consts.RESULT_NOTE, errorNoteTemp.replace("${filedName}", key));
				} else {
					detail.put(Consts.RESULT_NOTE, errorNoteTemp);
				}
			}
		}
		return jInt;
	}

	public static Double getJDouble(JSONObject JSONObject, String key) {
		return getJDouble(JSONObject, key, null);
	}

	/**
	 * 以Double形式返回JSONObject中的值
	 */
	public static Double getJDouble(JSONObject JSONObject, String key, Double defaultVal) {
		Double defaultValue = 0.0;
		if (defaultVal != null) {
			defaultValue = defaultVal;
		}
		return JSONObject.containsKey(key) ? ("".equals(JSONObject.getString(key)) ? defaultValue : JSONObject.getDouble(key)) : defaultValue;
	}

	/**
	 * JSONArray排序
	 *
	 * @param mJSONArray
	 * @param field      排序字段
	 * @param order      排序：desc降序 asc 升序
	 * @param fileType   字段类型：string ,integer
	 */
	public static void sortJsonArray(JSONArray mJSONArray, String field, String order, String fileType) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject jsonObj = null;
		for (int i = 0; i < mJSONArray.size(); i++) {
			jsonObj = mJSONArray.getJSONObject(i);
			list.add(jsonObj);
		}
		// 排序操作
		JSONComparator pComparator = new JSONComparator(field, order, fileType);
		Collections.sort(list, pComparator);
		// 把数据放回去
		mJSONArray.clear();
		for (int i = 0; i < list.size(); i++) {
			jsonObj = list.get(i);
			mJSONArray.add(jsonObj);
		}
	}

	public static JSONObject covertSms(SmsBean bean) {
		JSONObject reqParam = new JSONObject();
		reqParam.put("cmd", Constants.SMS_METHOD);
		reqParam.put("token", "245Y7BSfDHIWEie34");
		reqParam.put("version", "1.0");
		reqParam.put("params", JSONObject.toJSON(bean));
		return reqParam;
	}

	/**
	 * @param jsonRequest  请求的json
	 * @param jsonResponse 返回的json
	 * @param key          请求的json里需要验证的key
	 * @param required     验证必要性
	 * @param defaultVal   默认值
	 * @param errorNote    错误信息
	 * @return
	 * @throws Exception
	 * @Author: WangChun
	 * @Date: 2017/10/25 16:07
	 */
	public static String getRongStringAndCheck(JSONObject jsonRequest, JSONObject jsonResponse, String key, boolean required, String defaultVal, String errorNote) throws RongNullPointerException {
		String defaultValue = "";
		if (defaultVal != null) {
			defaultValue = defaultVal;
		}
		String jString = (jsonRequest != null && jsonRequest.containsKey(key) && !Utils.isBnull(jsonRequest.get(key).toString().trim())) ? jsonRequest.get(key).toString().trim() : defaultValue;
		if (required) {
			if (Utils.isBnull(jString)) {
				jsonResponse.put("code", "400");
				jsonResponse.put("msg", errorNote);
				throw new RongNullPointerException(errorNote);
			}
		}
		return jString;
	}


	/**
	 * @param jsonRequest  请求的json
	 * @param jsonResponse 返回的json
	 * @param key          请求的json里需要验证的key
	 * @param required     验证必要性
	 * @return
	 * @throws Exception
	 * @Author: WangChun
	 * @Date: 2017/10/25 16:08
	 */
	public static String getRongStringAndCheck(JSONObject jsonRequest, JSONObject jsonResponse, String key, boolean required) throws RongNullPointerException {
		String defaultValue = "";
		String jString = (jsonRequest != null && jsonRequest.containsKey(key) && !Utils.isBnull(jsonRequest.get(key).toString().trim())) ? jsonRequest.get(key).toString().trim() : defaultValue;
		if (required) {
			if (Utils.isBnull(jString)) {
				jsonResponse.put("code", "400");
				jsonResponse.put("msg", key + "为空");
				throw new RongNullPointerException(key + "为空");
			}
		}
		return jString;
	}

	/**
	 * @param jsonRequest  请求的json
	 * @param jsonResponse 返回的json
	 * @param key          请求的json里需要验证的key
	 * @return
	 * @throws Exception
	 * @Author: WangChun
	 * @Date: 2017/10/25 16:08
	 */
	public static String getRongStringAndCheck(JSONObject jsonRequest, JSONObject jsonResponse, String key) throws RongNullPointerException {
		return getRongStringAndCheck(jsonRequest, jsonResponse, key, true);
	}


	/**
	 * @param jsonRequest
	 * @param jsonResponse
	 * @param key
	 * @param required
	 * @return
	 * @throws RongNullPointerException
	 * @author: WangChun
	 * @date: 2017/10/26 10:33
	 */
	public static JSONObject getRongJsonAndCheck(JSONObject jsonRequest, JSONObject jsonResponse, String key, boolean required) throws RongNullPointerException {
		JSONObject defaultValue = null;
		JSONObject jJson = (jsonRequest != null && jsonRequest.containsKey(key) && !Utils.isBnull(jsonRequest.get(key).toString().trim())) ? (JSONObject) JSONObject.parse(jsonRequest.get(key).toString().trim()) : defaultValue;
		if (required) {
			if (jJson == null) {
				jsonResponse.put("code", "400");
				jsonResponse.put("msg", key + "为空");
				throw new RongNullPointerException(key + "为空");
			}
		}
		return jJson;
	}

	/**
	 * @param jsonRequest
	 * @param jsonResponse
	 * @param key
	 * @param required
	 * @return
	 * @throws RongNullPointerException
	 * @author: WangChun
	 * @date: 2017/10/26 15:31
	 */
	public static JSONArray getRongJsonArrayAndCheck(JSONObject jsonRequest, JSONObject jsonResponse, String key, boolean required) throws RongNullPointerException {
		JSONArray defaultValue = null;
		JSONArray jsonArray = (jsonRequest != null && jsonRequest.containsKey(key) && !Utils.isBnull(jsonRequest.get(key).toString().trim())) ? JSONArray.parseArray(jsonRequest.get(key).toString().trim()) : defaultValue;
		if (required) {
			if (jsonArray == null) {
				jsonResponse.put("code", "400");
				jsonResponse.put("msg", key + "为空");
				throw new RongNullPointerException(key + "为空");
			}
		}
		return jsonArray;
	}

	/**
	 * @param jsonResponse
	 * @param jsonDetail
	 * @return
	 * @author: WangChun
	 * @date: 2017/10/26 16:52
	 */
	public static JSONObject jsonDetailSuccess(JSONObject jsonResponse, JSONObject jsonDetail) {
		jsonResponse.put("code", 200);
		jsonResponse.put("msg", "success");
		jsonResponse.put("data", jsonDetail);
		return jsonResponse;
	}

	/**
	 * @param jsonResponse
	 * @param code
	 * @param error
	 * @return
	 * @author: WangChun
	 * @date: 2017/10/26 16:55
	 */
	public static JSONObject jsonDetailFail(JSONObject jsonResponse, Integer code, String error) {
		jsonResponse.put("code", code);
		jsonResponse.put("msg", error);
		return jsonResponse;
	}

}
