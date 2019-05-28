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
import com.service.intf.RedEnvelopeService;

/**
 * 红包管理 控制类
 * 
 * @author fum
 *
 */
@Controller
public class RedEnvelopeController {
	
	private static final Logger logger = Logger.getLogger(RedEnvelopeController.class);

	@Autowired
	RedEnvelopeService redEnvelopeService;

	/**
	 * 查询红包明细 入口方法
	 * 
	 * @param jsonObject
	 * @return JSONObject
	 */
	@RequestMapping("/qryRedEnvelopeDetail")
	public @ResponseBody JSONObject qryRedEnvelopeDetail(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = redEnvelopeService.qryRedEnvelopeDetail(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	

	/**
	 * 查询 红包 个人情况
	 * 
	 * @param jsonObject
	 * @return JSONObject
	 */
	@RequestMapping("/qryRedEnvelope")
	public @ResponseBody JSONObject qryRedEnvelope(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = redEnvelopeService.qryRedEnvelope(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	 * 红包提现接口
	 * 
	 * @param jsonObject
	 * @return JSONObject
	 */
	@RequestMapping("/withdrawalsRedEnvelope")
	public @ResponseBody JSONObject withdrawalsRedEnvelope(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = redEnvelopeService.withdrawalsRedEnvelope(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
