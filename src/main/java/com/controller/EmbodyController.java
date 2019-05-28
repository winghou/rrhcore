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
import com.service.intf.EmbodyService;
@Controller
public class EmbodyController {
private static final Logger logger = Logger.getLogger(EmbodyController.class);
	
	@Autowired
	EmbodyService embodyService;
	
	/**
	 * 首页
	 * @param jo
	 * @return
	 */
	@RequestMapping("/homePage")
	public @ResponseBody JSONObject  homePage (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = embodyService.homePage(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * 首页未读消息数量分开查询
	 * @param jo
	 * @return
	 */
	@RequestMapping("/queryNotReadMessage")
	public @ResponseBody JSONObject  queryNotReadMessage (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = embodyService.queryNotReadMessage(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * 首页用户未还款还款计划轮播图
	 * @param jo
	 * @return
	 */
	@RequestMapping("/payPlanCarousel")
	public @ResponseBody JSONObject  payPlanCarousel (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = embodyService.payPlanCarousel(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}

