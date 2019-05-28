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
import com.service.RegisterInfoIsCompleteImpl;
@Controller
public class RegisterInfoIsCompleteController {
	private static final Logger logger = Logger.getLogger(RegisterInfoIsCompleteController.class);
	
	@Autowired
	RegisterInfoIsCompleteImpl registerInfoIsCompleteImpl;
	
	@RequestMapping("/registerInfoIsComplete")
	public @ResponseBody JSONObject registerInfoIsComplete(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = registerInfoIsCompleteImpl.registerInfoIsComplete(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/mine")
	public @ResponseBody JSONObject mine(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = registerInfoIsCompleteImpl.mine(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	
}
