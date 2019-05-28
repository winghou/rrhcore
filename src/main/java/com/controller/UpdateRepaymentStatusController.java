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
import com.service.intf.UpdateRepaymentStatusService;
@Controller
public class UpdateRepaymentStatusController {
private static final Logger logger = Logger.getLogger(UpdateRepaymentStatusController.class);
	
	@Autowired
	UpdateRepaymentStatusService updateRepaymentStatusService;
	
	@RequestMapping("/updateRepaymentStatus")
	public @ResponseBody JSONObject updateRepaymentStatus  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = updateRepaymentStatusService.updateRepaymentStatus(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
