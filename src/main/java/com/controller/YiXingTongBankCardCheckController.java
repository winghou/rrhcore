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
import com.service.intf.AppPayZfbService;
import com.service.intf.YiXingTongBankCardCheckService;

@Controller
public class YiXingTongBankCardCheckController {
	private static final Logger logger = Logger.getLogger(YiXingTongBankCardCheckController.class);

	@Autowired
	private YiXingTongBankCardCheckService yiXingTongBankCardCheckService;
	@Autowired
	private AppPayZfbService appPayZfbService;
	@Autowired
	private AgreementToPayService agreementToPayService;

	/**
	 * 修改银行卡信息
	 * 
	 * @param jo
	 * @return
	 */
	@RequestMapping("/bankCardCheck")
	public @ResponseBody JSONObject bankCardCheck(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String withholdingSwitch = appPayZfbService.withholdingSwitch();
			if("2".equals(withholdingSwitch)){
				JSONObject detail =agreementToPayService.preBindingCard(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}else{
				JSONObject detail = yiXingTongBankCardCheckService.bankCardCheck(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * @author yang.wu
	 * @Description: 查看绑卡信息或进入绑卡页面
	 * @return JSONObject
	 * @date 2017年7月14日上午9:52:34
	 */
	@RequestMapping("/lookBindBankinfo")
	public @ResponseBody JSONObject lookBindBankinfo(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = yiXingTongBankCardCheckService.lookBindBankinfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * @author yang.wu
	 * @Description: 查询支持银行卡
	 * @return JSONObject
	 * @date 2017年7月14日上午9:52:14
	 */
	@RequestMapping("/querySupportedBank")
	public @ResponseBody JSONObject querySupportedBank(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String withholdingSwitch = appPayZfbService.withholdingSwitch();
			if("2".equals(withholdingSwitch)){
				JSONObject detail =agreementToPayService.queryAgreementPayBank(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}else{
				JSONObject detail = yiXingTongBankCardCheckService.querySupportedBank(rt.getParams());
				return new ResponseTemplate(jo, detail).getReturn();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	 * @author yang.wu
	 * @Description: 查询验卡异步回调结果
	 * @return JSONObject
	 * @date 2017年7月12日下午4:44:57
	 */
	@RequestMapping("/queryVerifyCardResult")
	public @ResponseBody JSONObject queryVerifyCardResult(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = yiXingTongBankCardCheckService.queryVerifyCardResult(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
