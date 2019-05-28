package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.MchInterviewService;

@Controller
public class MchInterviewController {
	@Autowired
	private MchInterviewService mchInterviewService;

	@RequestMapping("/countInterview")
	public @ResponseBody JSONObject countInterview(@RequestBody JSONObject jsonObject) {

		JSONObject detail=new JSONObject();
		try {
//			mchInterviewService.save(jsonObject);
			detail.put("result", "0");
		} catch (Exception e) {
			detail.put("result", "1");
		}
		JSONObject jsonobj=new ResponseTemplate(jsonObject, detail).getReturn();
		return jsonobj;
	}

}
