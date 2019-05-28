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
import com.service.intf.GetValiCodeFromPhoneService;
@Controller
public class GetValiCodeFromPhone {
	private static final Logger logger = Logger.getLogger(GetValiCodeFromPhone.class);
	
	@Autowired
	GetValiCodeFromPhoneService getValiCodeFromPhoneService;
	
	@RequestMapping("/getValiCodeFromPhone")
	public @ResponseBody JSONObject getValiCodeFromPhone (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = getValiCodeFromPhoneService.getValiCodeFromPhone(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	/**
	 * 语音验证码
	 * @param jo
	 * @return
	 */
	@RequestMapping("/getVoiceMsgFromPhone")
	public @ResponseBody JSONObject getVoiceMsgFromPhone (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = getValiCodeFromPhoneService.getVoiceMsgFromPhone(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
		
	}
	
	/**
	 * 语音验证码提示语
	 * @param jo
	 * @return
	 */
	@RequestMapping("/getVoiceMsgClues")
	public @ResponseBody JSONObject getVoiceMsgClues (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = getValiCodeFromPhoneService.getVoiceMsgClues(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
		
	}

}
