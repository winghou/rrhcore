package com.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.AppGoSumbitService;
@Controller
public class GosumbitController {
	private static final Logger logger = Logger.getLogger(AppGoSumbitService.class);
	
	@Autowired
	AppGoSumbitService appGoSumbitService;
	
	/**
	 * 订单流程
	 */
	@RequestMapping("/appgoSumbit")
	public @ResponseBody JSONObject appgoSumbit (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appGoSumbitService.AppgoSumbit(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * 用户授信流程
	 */
	@RequestMapping("/sumbitUserInfoToDecisionEngine")
	public @ResponseBody JSONObject sumbitUserInfoToDecisionEngine (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appGoSumbitService.sumbitUserInfoToDecisionEngine(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}

}
