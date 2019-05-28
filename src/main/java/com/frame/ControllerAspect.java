package com.frame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.service.intf.CheckTokenService;
import com.util.ErrorCode;
import com.util.JsonUtil;

/**
 * 利用aop来打印日志和token校验
 * 
 * @author bill
 *
 */
@Aspect
// 该注解标示该类为切面类
@Component
// 注入依赖
public class ControllerAspect {
	private Logger logger = Logger.getLogger(ControllerAspect.class);

	// private static RedisService redisService;

	// static
	// {
	// redisService = RedisService.getInstance();
	// }

	@Autowired
	private CheckTokenService checkTokenService;

	/**
	 * 
	 * token校验及日志记录
	 */
	@Around("within(com.controller..*)")
	public Object validIdentityAndSecure(ProceedingJoinPoint pjp) throws Throwable {
		Object o = null;

		if (pjp.getArgs()[0] instanceof JSONObject) {
			JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
			JSONObject detail = new JSONObject();
			String userId = JsonUtil.getJString(reqJson, Consts.USERID);
			String token = JsonUtil.getJString(reqJson, Consts.TOKEN);
			String cmd = JsonUtil.getJString(reqJson, Consts.CMD_LABEL);
			String commonds = Consts.notTokenValidation;
			String[] commond = commonds.split("\\|");
			if (!Arrays.asList(commond).contains(cmd)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", userId);
				map.put("token", token);
				if (!"-100".equals(userId)) {
					String num = checkTokenService.checkToken(map);
					if (!"1".equals(num)) {
						if ("0".equals(num)) {
							detail.put(Consts.RESULT, ErrorCode.TOKEN_FAILED);
							detail.put(Consts.RESULT_NOTE, "登录已失效，请重新登录");
						} else {
							detail.put(Consts.RESULT, ErrorCode.TOKEN_FAILED);
							detail.put(Consts.RESULT_NOTE, "当前帐号在" + num + "点在其他设备上登录，请注意");
						}
						return new ResponseTemplate(reqJson, detail).getReturn();
					}
				}
			}
		} else {
			String method = pjp.getSignature().getName().toLowerCase();
		}

		long begin = System.nanoTime();
		try {
			o = pjp.proceed();
		} catch (SecurityException se) {
			if (pjp.getArgs()[0] instanceof JSONObject) {
				JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
				JSONObject detail = new JSONObject();
				detail.put(Consts.RESULT, Integer.parseInt(se.getMessage()));
				return new ResponseTemplate(reqJson, detail).getReturn();
			} else {
				String method = pjp.getSignature().getName().toLowerCase();
				JSONObject detail = new JSONObject();
				detail.put(Consts.RESULT, Integer.parseInt(se.getMessage()));
				return new ResponseTemplate(method, detail).getReturn();
			}
		}
		long end = System.nanoTime();

		if (pjp.getArgs()[0] instanceof JSONObject) {
			JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
			String cmd = JsonUtil.getJString(reqJson, Consts.CMD_LABEL);
			logger.info(cmd + " req <<" + JSON.toJSONString(reqJson));
			logger.info(cmd + " res >>" + JSON.toJSONString(o));
			logger.info(cmd + " >> " + (end - begin) / 1000000 + "ms");
		} else {
			String method = pjp.getSignature().getName().toLowerCase();
			logger.info(method + " >> " + (end - begin) / 1000000 + "ms");
		}

		return o;
	}
	
	
	/**
	 * 
	 * 微信token校验及日志记录
	 */
	@Around("within(com.wxcontroller..*)")
	public Object validwxSession(ProceedingJoinPoint pjp) throws Throwable {
		Object o = null;

		if (pjp.getArgs()[0] instanceof JSONObject) {
			JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
			JSONObject detail = new JSONObject();
			String userId = JsonUtil.getJString(reqJson, Consts.USERID);
			String token = JsonUtil.getJString(reqJson, Consts.TOKEN);
			String cmd = JsonUtil.getJString(reqJson, Consts.CMD_LABEL);
			String commonds = "bind|welogin|queryWxOrder|relieveBind|qurMyQuota|doPayToWx|qurMyPay|registerWx|wxShare";
			String[] commond = commonds.split("\\|");
			if (!Arrays.asList(commond).contains(cmd)) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("apprId", userId);
				map.put("token", token);
				if (!"-100".equals(userId)) {
					String num = checkTokenService.checkwxToken(map);
					if (!"1".equals(num)) {
						if ("0".equals(num)) {
							detail.put(Consts.RESULT, ErrorCode.TOKEN_FAILED);
							detail.put(Consts.RESULT_NOTE, "微信端登录已失效，请重新登录");
						} else {
							detail.put(Consts.RESULT, ErrorCode.TOKEN_FAILED);
							detail.put(Consts.RESULT_NOTE, "当前帐号在" + num + "点在其他设备上登录。若非本人操作，你的登录密码可能已经泄漏，请及时改密");
						}
						return new ResponseTemplate(reqJson, detail).getReturn();
					}
				}
			}
		} else {
			String method = pjp.getSignature().getName().toLowerCase();
		}

		long begin = System.nanoTime();
		try {
			o = pjp.proceed();
		} catch (SecurityException se) {
			if (pjp.getArgs()[0] instanceof JSONObject) {
				JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
				JSONObject detail = new JSONObject();
				detail.put(Consts.RESULT, Integer.parseInt(se.getMessage()));
				return new ResponseTemplate(reqJson, detail).getReturn();
			} else {
				String method = pjp.getSignature().getName().toLowerCase();
				JSONObject detail = new JSONObject();
				detail.put(Consts.RESULT, Integer.parseInt(se.getMessage()));
				return new ResponseTemplate(method, detail).getReturn();
			}
		}
		long end = System.nanoTime();

		if (pjp.getArgs()[0] instanceof JSONObject) {
			JSONObject reqJson = (JSONObject) pjp.getArgs()[0];
			String cmd = JsonUtil.getJString(reqJson, Consts.CMD_LABEL);
			logger.info(cmd + " req <<" + JSON.toJSONString(reqJson));
			logger.info(cmd + " res >>" + JSON.toJSONString(o));
			logger.info(cmd + " >> " + (end - begin) / 1000000 + "ms");
		} else {
			String method = pjp.getSignature().getName().toLowerCase();
			logger.info(method + " >> " + (end - begin) / 1000000 + "ms");
		}

		return o;
	}

}
