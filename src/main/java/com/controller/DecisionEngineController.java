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
import com.service.intf.DecisionEngineService;

@Controller
public class DecisionEngineController {
	
	private static final Logger logger = Logger.getLogger(DecisionEngineController.class);

	@Autowired
	private DecisionEngineService decisionEngineService;
	
	/**
	 * 查询用户在决策引擎是否有报告
	 */
	@RequestMapping("/checkReportExsit")
	public @ResponseBody JSONObject checkReportExsit (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = decisionEngineService.checkReportExsit(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
}
