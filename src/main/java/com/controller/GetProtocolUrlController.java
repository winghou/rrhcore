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
import com.service.intf.GetProtocolUrlService;
import com.service.intf.GetWhiteKnightInfoService;
@Controller
public class GetProtocolUrlController {
	private static final Logger logger = Logger.getLogger(GetValiCodeFromPhone.class);
	
	@Autowired
	GetProtocolUrlService getProtocolUrlService;
	
	@RequestMapping("/getProtocolUrl")
	public @ResponseBody JSONObject getProtocolUrl (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = getProtocolUrlService.getProtocolUrl(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}

	@RequestMapping("/getProtocolInfo")
	public @ResponseBody JSONObject getProtocolInfo (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = getProtocolUrlService.getProtocolInfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}