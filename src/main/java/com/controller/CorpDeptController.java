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
import com.service.intf.CorpDeptServiceIntf;

@Controller("corpDeptController")
public class CorpDeptController {

	private static final Logger logger = Logger.getLogger(CorpDeptController.class);

	CorpDeptServiceIntf corpDeptService;

	public CorpDeptServiceIntf getCorpDeptService() {
		return corpDeptService;
	}

	@Autowired
	public void setCorpDeptService(CorpDeptServiceIntf corpDeptService) {
		this.corpDeptService = corpDeptService;
	}

	@RequestMapping("/queryDeptList")
	public @ResponseBody JSONObject queryDeptList(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = corpDeptService.queryDeptList(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
}
