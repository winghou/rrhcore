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
import com.service.intf.AppPayZfbService;
@Controller
public class AppPayZfbController {
private static final Logger logger = Logger.getLogger(AppPayZfbController.class);
	
	@Autowired
	AppPayZfbService appPayZfbService;
	
	/**
	 * 支付宝还款
	 * @param jo
	 * @return
	 */
	@RequestMapping("/payZfb")
	public @ResponseBody JSONObject payZfb (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPayZfbService.payZfb(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 还款结果
	 * @param jo
	 * @return
	 */
	@RequestMapping("/payResult")
	public @ResponseBody JSONObject payResult (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPayZfbService.payResult(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 快捷代扣
	 * @param jo
	 * @return
	 */
	@RequestMapping("/fastWithholding")
	public @ResponseBody JSONObject fastWithholding (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String withholdingSwitch = appPayZfbService.withholdingSwitch();
			if("2".equals(withholdingSwitch)){
				JSONObject detail = appPayZfbService.fastBoofProtocolPay(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}else{
				JSONObject detail = appPayZfbService.fastWithholding(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 支付选择页面
	 * @param jo
	 * @return
	 */
	@RequestMapping("/payWayChoosonPage")
	public @ResponseBody JSONObject PayWayChoosonPage (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPayZfbService.payWayChoosonPage(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 支付完成查询还款结果
	 * @param jo
	 * @return
	 */
	@RequestMapping("/qryPayResultFrequently")
	public @ResponseBody JSONObject qryPayResultFrequently (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = appPayZfbService.qryPayResultFrequently(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
}
