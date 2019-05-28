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
import com.service.intf.AuthenticationCustomInfo;

@Controller
public class AuthenticationCustomController {
	private static final Logger logger = Logger.getLogger(AuthenticationCustomController.class);
	
	@Autowired
	private AuthenticationCustomInfo authenticationCustomInfo;
	
	@RequestMapping("/authenticationCustom")
	public @ResponseBody JSONObject authenticationCustom (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = authenticationCustomInfo.authenticationCustom(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/saveXykAndGjj")
	public @ResponseBody JSONObject saveXykAndGjj (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = authenticationCustomInfo.saveXykAndGjj(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
