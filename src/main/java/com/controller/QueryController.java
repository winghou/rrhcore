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
import com.service.intf.QueryBaseInfoService;
@Controller
public class QueryController {

	private static final Logger logger = Logger.getLogger(QueryController.class);
	@Autowired
	private QueryBaseInfoService queryBaseInfoService;
	
	@RequestMapping("/queryBaseInfo ")
	public @ResponseBody JSONObject queryBaseInfo  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryBaseInfoService.info(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/saveBaseInfo ")
	public @ResponseBody JSONObject saveBaseInfo  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = queryBaseInfoService.saveInfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
