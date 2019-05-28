package com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

/** 
 * @ClassName: JsonParser 
 * @Description: json解析器
 */
public class JsonParser {
	private static Logger logger = LoggerFactory.getLogger(JsonParser.class);
	
	
	/**
	 * @Title: JsonToObject 
	 * @Description: 根据指定的json字符串并使用指定的类型进行解析
	 * @param json 要进行解析的json字符串
	 * @param clazz 解析的类型
	 * @return 当解析失败时返回null，否则返回基类对象
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			// 使用mapper对json字符串进行解析
            return JSONObject.parseObject(json, clazz);
        }
        catch (Exception e)
        {
			logger.error(json, e);
			// 如果解析失败，直接返回null
			return null;
		}
	}

	/**
	 * @Title: JsonToObject
	 * @Description: 根据指定的json字符串并使用指定的类型进行解析（针对泛型）
	 * @param json 要进行解析的json字符串
	 * @param reference 泛型的类型引用
	 * @return 当解析失败时返回null，否则返回基类对象
	 */
	public static <T> T toObject(String json, TypeReference<?> reference) {
		try {
			// 使用mapper对json字符串进行解析
            return (T)JSONObject.parseObject(json, reference);
        }
        catch (Exception e)
        {
			logger.error(json, e);
			// 如果解析失败，直接返回null
			return null;
		}
	}
	
	/**
	 * @Title: ObjectToJson 
	 * @Description: 将对象转换为json字符串
	 * @param t 要进行转换的实体
	 * @return 转换成功时返回json字符串，否则返回null
	 */
	public static <T> String toJson(T t) {
		try {
            return JSONObject.toJSONString(t);
        }
        catch (Exception e)
        {
			logger.error(t.toString(), e);
			
			// 如果转换失败则返回null
			return null;
		}
	}
}
