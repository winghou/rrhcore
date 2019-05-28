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
import com.service.intf.AccountConfirmService;
import com.service.intf.AppInsuranceService;

@Controller
public class AppInsuranceController {
	private static final Logger logger = Logger.getLogger(AppInsuranceController.class);
	
	@Autowired
	private AppInsuranceService appInsuranceService;
	
	@RequestMapping("/getInsurance")
	public @ResponseBody JSONObject getInsurance (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appInsuranceService.getInsurance(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
