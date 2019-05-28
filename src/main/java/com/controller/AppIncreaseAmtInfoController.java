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
import com.service.intf.AppIncreaseAmtInfoService;

@Controller
public class AppIncreaseAmtInfoController {
	private static final Logger logger = Logger.getLogger(AppIncreaseAmtInfoController.class);
	
	@Autowired
	private AppIncreaseAmtInfoService appIncreaseAmtInfoService;
	
	/**
	 * @author yang.wu
	 * @Description: 获得提额资料状态
	 * @return JSONObject
	 * @date 2017年7月20日下午11:27:33
	 */
	@RequestMapping("/getIncreaseAmtInfoStatus ")
	public @ResponseBody JSONObject getIncreaseAmtInfoStatus  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appIncreaseAmtInfoService.getIncreaseAmtInfoStatus(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * @author yang.wu
	 * @Description: 提额资料授权
	 * @return JSONObject
	 * @date 2017年7月21日上午12:05:12
	 */
	@RequestMapping("/increaseAmtInfoAccredit ")
	public @ResponseBody JSONObject increaseAmtInfoAccredit  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appIncreaseAmtInfoService.increaseAmtInfoAccredit(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
