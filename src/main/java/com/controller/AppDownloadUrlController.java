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
import com.service.intf.AppDownloadUrlService;

@Controller
public class AppDownloadUrlController {
private static final Logger logger = Logger.getLogger(AppDownloadUrlController.class);
	
	@Autowired
	private AppDownloadUrlService appDownloadUrlService;
	
	/**
	 * 查询用户获得奖励金额
	 * @param jo
	 * @return
	 */
	@RequestMapping("/downloadUrl")
	public @ResponseBody JSONObject downloadUrl (@RequestBody JSONObject jo) {
		try {
			JSONObject detail = appDownloadUrlService.downloadUrl();
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
