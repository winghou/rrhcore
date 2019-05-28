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
import com.service.intf.WhiteKnightOperatorService;
@Controller
public class WhiteKnightOperatorController {

	private static final Logger logger = Logger.getLogger(WhiteKnightOperatorController.class);
	
	@Autowired
	private WhiteKnightOperatorService whiteKnightOperator;
	
	
	/**
	  *  Description:白骑士运营商登录授权
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:41:49
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorLogin")
	public @ResponseBody JSONObject whiteKnightOperatorLogin (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorLogin(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	  *  Description:白骑士运营商重发登录短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:42:11
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorSendLoginSms")
	public @ResponseBody JSONObject whiteKnightOperatorSendLoginSms (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorSendLoginSms(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	  *  Description:白骑士运营商校验二次鉴权短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:42:20
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorVerifyAuthSms")
	public @ResponseBody JSONObject whiteKnightOperatorVerifyAuthSms (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorVerifyAuthSms(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	  *  Description:白骑士运营商重发二次鉴权短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:42:22
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorSendAuthSms")
	public @ResponseBody JSONObject whiteKnightOperatorSendAuthSms (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorSendAuthSms(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	  *  Description:白骑士运营商上报联系人参数
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 上午11:42:26
	  *  @param jo
	  *  @return
	  */
	/*@RequestMapping("/whiteKnightOperatorReportext")
	public @ResponseBody JSONObject whiteKnightOperatorReportext (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorReportext(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}*/
	
	
	/**
	  *  Description:白骑士运营商重置服务密码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 下午4:15:48
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorResetpwd")
	public @ResponseBody JSONObject whiteKnightOperatorResetpwd (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorResetpwd(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	
	/**
	  *  Description:白骑士运营商重发重置密码短信验证码
	  *  @author  yang.wu  
	  *	 DateTime 2017年12月4日 下午4:21:02
	  *  @param jo
	  *  @return
	  */
	@RequestMapping("/whiteKnightOperatorSendResetpwdSms")
	public @ResponseBody JSONObject whiteKnightOperatorSendResetpwdSms (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = whiteKnightOperator.whiteKnightOperatorSendResetpwdSms(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
