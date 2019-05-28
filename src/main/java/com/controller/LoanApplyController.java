package com.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.frame.RequestTemplate;
import com.frame.ResponseTemplate;
import com.service.intf.LoanApplyService;
@Controller
public class LoanApplyController{
	private static final Logger logger = Logger.getLogger(LoanApplyController.class);
	
	@Autowired
	LoanApplyService  loanApplyService;
	
	@RequestMapping("/queryAmtAndDaysAndCoupons")
	public @ResponseBody JSONObject queryAmtAndDaysAndCoupons  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = loanApplyService.queryAmtAndDaysAndCoupons(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
		
	}
	
	@RequestMapping("/IneedMoney ")
	public @ResponseBody JSONObject IneedMoney  (@RequestBody JSONObject jo, HttpServletRequest request) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			String projectUrl = request.getSession().getServletContext().getRealPath("/");
			JSONObject object = rt.getParams();
			object.put("projectUrl", projectUrl);
			JSONObject detail = loanApplyService.IneedMoney(object);
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/qureyUrlAndMonthpay ")
	public @ResponseBody JSONObject qureyUrlAndMonthpay  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = loanApplyService.QureyUrlAndMonthpay(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/myBill ")
	public @ResponseBody JSONObject myBill  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = loanApplyService.myBill(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	//一键借款
	@RequestMapping("/loanByOneStep ")
	public @ResponseBody JSONObject loanByOneStep  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = loanApplyService.loanByOneStep(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
}
