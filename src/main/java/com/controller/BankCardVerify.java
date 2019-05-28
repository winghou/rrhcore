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
import com.service.intf.BankCardVerifyService;

@Controller
public class BankCardVerify {

	private static final Logger logger = Logger.getLogger(BankCardVerify.class);

	@Autowired
	private BankCardVerifyService bankCardVerifyService;

	/**
	 * 银行卡校验
	 * @param jo
	 * @return
	 */
	@RequestMapping("/bankCardVerify")
	public @ResponseBody JSONObject bankCardVerify(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = bankCardVerifyService.bankCardVerify(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * 小安银行卡校验外部调用方法
	 * @param jo
	 * @return
	 */
	@RequestMapping("/xiaoanBankCardCheck")
	public @ResponseBody JSONObject xiaoanBankCardCheck(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = bankCardVerifyService.xiaoanBankCardCheck(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
}
