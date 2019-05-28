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
import com.service.intf.PullPostboxListService;

@Controller
public class AppPostboxValidateController {
	private static final Logger logger = Logger.getLogger(AppPostboxValidateController.class);
	
	@Autowired
	private PullPostboxListService  pullPostboxListService;
	
	
	/**
	 * 拉取邮箱列表
	 * @param jo
	 */
	
	@RequestMapping("/pullPostboxList")
	public @ResponseBody JSONObject postboxList(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = pullPostboxListService.pullPostboxList(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * 将获取的邮箱登录情况交给爬虫验证
	 * @param jo
	 */
	@RequestMapping("/postboxListValidate")
	public @ResponseBody JSONObject  postboxListValidate(@RequestBody JSONObject jo){
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = pullPostboxListService.postboxListValidate(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
}
