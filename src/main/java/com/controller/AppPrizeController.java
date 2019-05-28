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
import com.service.intf.AppPrizeService;

@Controller
public class AppPrizeController {
	
	private static final Logger logger = Logger.getLogger(AppPrizeController.class);

	@Autowired
	private AppPrizeService appPrizeService;
	
	/**
	 * 抽奖
	 * @param jo
	 * @return
	 */
	@RequestMapping("/prizeLottery")
	public @ResponseBody JSONObject prizeLottery (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPrizeService.prizeLottery(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 展示抽奖转盘
	 * @param jo
	 * @return
	 */
	@RequestMapping("/prizeLotteryTable")
	public @ResponseBody JSONObject prizeLotteryTable (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPrizeService.prizeLotteryTable(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 查询中奖记录
	 * @param jo
	 * @return
	 */
	@RequestMapping("/showPrizeRecord")
	public @ResponseBody JSONObject showPrizeRecord (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPrizeService.showPrizeRecord(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 修改中奖资料
	 * @param jo
	 * @return
	 */
	@RequestMapping("/updatePrizePersonInfo")
	public @ResponseBody JSONObject updatePrizePersonInfo (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPrizeService.updatePrizePersonInfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
}
