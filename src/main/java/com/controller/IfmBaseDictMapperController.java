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
import com.service.IfmBaseDictServiceImpl;
@Controller("ifmBaseDictMapperController")
public class IfmBaseDictMapperController {
	private static final Logger logger = Logger.getLogger(IfmBaseDictMapperController.class);
	
	@Autowired
	IfmBaseDictServiceImpl ifmBaseDictServiceImpl;
	
	/**
	 * 查询服务URL(第一个请求接口)
	 * @param jo
	 * @return
	 */
	@RequestMapping("/queryServiceUrl")
	public @ResponseBody JSONObject queryServiceUrl (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = ifmBaseDictServiceImpl.queryServiceUrl(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
}
