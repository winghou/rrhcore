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
import com.service.intf.QueryAdvrService;
import com.service.intf.QueryLoanOrgService;

@Controller
public class QueryAdvrController {
private static final Logger logger = Logger.getLogger(QueryAdvrController.class);
	
	@Autowired
	private QueryAdvrService queryAdvrService;
	@Autowired
	private QueryLoanOrgService queryLoanOrgService;
	
	@RequestMapping("/queryAdvr")
	public @ResponseBody JSONObject queryAdvr(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryAdvrService.queryAdvr(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryLoanOrg")
	public @ResponseBody JSONObject queryLoanOrg(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryLoanOrgService.queryLoanOrg(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryAdvrByModule")
	public @ResponseBody JSONObject queryAdvrByModule(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryAdvrService.queryAdvrByModule(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
