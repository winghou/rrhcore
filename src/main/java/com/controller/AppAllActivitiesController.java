package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.AppAllActivitiesService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppAllActivitiesController {

	private static final Logger logger = Logger.getLogger(AppAllActivitiesController.class);
	
	@Autowired
	private AppAllActivitiesService appAllActivitiesService;
	
	/**
	 * 查询用户获得奖励金额
	 * @param jo
	 * @return
	 */
	@RequestMapping("/queryUserRewardAmt")
	public @ResponseBody JSONObject queryUserRewardAmt (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appAllActivitiesService.queryUserRewardAmt(rt.getParams());
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
	@RequestMapping("/showLotteryTable")
	public @ResponseBody JSONObject showLotteryTable (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appAllActivitiesService.showLotteryTable(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}

	/**
	 * 抽奖
	 *
	 * @param jo
	 * @return
	 */
	@RequestMapping("/lotteryActivity")
	public @ResponseBody
	JSONObject lotteryActivity(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appAllActivitiesService.lotteryActivity(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}

	/**
	 * 情人节活动
	 * 统计借款金额及排名
	 *
	 * @param jo
	 * @return
	 */
	@RequestMapping("/loverActivity")
	public @ResponseBody
	JSONObject loverActivity(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appAllActivitiesService.loverActivity(rt.getParams());
			// 针对未登录的用户返回状态为-1
			if (detail != null && detail.getString("resultNote") != null && detail.getString("resultNote").equals("请求报文中缺少userId参数")) {
				detail.put("resultNote", "未登录");
				detail.put("result", "-1");
			}
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}

}
