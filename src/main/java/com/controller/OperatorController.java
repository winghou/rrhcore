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
import com.service.intf.OperatorService;

@Controller
public class OperatorController {
	private static final Logger logger = Logger.getLogger(OperatorController.class);
	
	@Autowired
	private OperatorService operatorService;
	/* @author yang.wu
	 * 类名称： operator
	 * 创建时间：2017年4月25日 下午5:12:31
	 * @param jo
	 * @returnJSONObject
	 * 类描述：提交数据源采集请求
	 */
	@RequestMapping("/operator")
	public @ResponseBody JSONObject operator (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = operatorService.operator(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	/* @author yang.wu
	 * 类名称： operatorResetServiceNumber
	 * 创建时间：2017年4月25日 下午5:11:36
	 * @param jo
	 * @returnJSONObject
	 * 类描述：修改服务密码
	 */
	@RequestMapping("/operatorResetServiceNumber")
	public @ResponseBody JSONObject operatorResetServiceNumber (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = operatorService.operatorResetServiceNumber(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/* @author yang.wu
	 * 类名称： getBaseInfoToOperator
	 * 创建时间：2017年5月19日 上午10:51:32
	 * @param jo
	 * @return JSONObject
	 * 类描述：获取运营商所需基本资料
	 */
	@RequestMapping("/getBaseInfoToOperator")
	public @ResponseBody JSONObject getBaseInfoToOperator (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = operatorService.getBaseInfoToOperator(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	/**
	 * @author yang.wu
	 * @Description: 获取账户状态
	 * @return JSONObject
	 * @date 2017年8月8日下午3:40:55
	 */
	@RequestMapping("/getAccountStatus")
	public @ResponseBody JSONObject getAccountStatus (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = operatorService.getAccountStatus(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}

}
