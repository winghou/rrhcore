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
import com.service.intf.AgreementToPayService;

@Controller
public class AgreementToPayController {
	private static final Logger logger = Logger.getLogger(AgreementToPayController.class);
	
	@Autowired
	private AgreementToPayService agreementToPayService;
	
	/**
	 * @author zw 
	 * 协议支付预绑卡(获取验证码)
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/preBindingCard")
	public @ResponseBody JSONObject preBindingCard (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = agreementToPayService.preBindingCard(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	 /**
     * @author zw 
     * 确认绑卡协议支付接口(验证验证码 绑卡)
     * @param params
     * @return
     * @throws Exception
     */
	@RequestMapping("/preBindingCardPhoneValicode")
	public @ResponseBody JSONObject preBindingCardPhoneValicode (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = agreementToPayService.preBindingCardPhoneValicode(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
