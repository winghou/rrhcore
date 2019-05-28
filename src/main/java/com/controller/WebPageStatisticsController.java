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
import com.service.intf.DrainagePromotionService;
import com.service.intf.WebPageStatisticsService;

@Controller
public class WebPageStatisticsController {
	private static final Logger logger = Logger.getLogger(WebPageStatisticsController.class);
	
	@Autowired
	private WebPageStatisticsService webPageStatisticsService;
	
	/** @author zw
	 * 类名称： pageStatistics
	 * 创建时间：2018年04月20日 
	 * @param jo
	 * @return JSONObject
	 * 类描述：渠道页面访问统计
	 */
	@RequestMapping("/pageStatistics")
	public @ResponseBody JSONObject  pageStatistics(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = webPageStatisticsService.pageStatistics(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
