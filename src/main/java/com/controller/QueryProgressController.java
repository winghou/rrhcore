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
import com.service.intf.QueryProgressService;
@Controller
public class QueryProgressController {
private static final Logger logger = Logger.getLogger(QueryProgressController.class);
	
	@Autowired
	QueryProgressService queryProgressService;
	
	@RequestMapping("/queryProgress ")
	public @ResponseBody JSONObject queryProgress (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryProgressService.queryProgress(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryMyStatus ")
	public @ResponseBody JSONObject queryMyStatus (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryProgressService.queryMyStatus(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryPercent ")
	public @ResponseBody JSONObject queryPercent (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryProgressService.queryPercent(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryTbAndJdStutus ")
	public @ResponseBody JSONObject queryTbAndJdStutus (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryProgressService.queryTbAndJdStutus(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryXykAndYx ")
	public @ResponseBody JSONObject queryXykAndYx (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryProgressService.queryXykAndYx(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
