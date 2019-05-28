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
import com.service.intf.YouDunService;

@Controller
public class YouDunController {

	private static final Logger logger = Logger.getLogger(YouDunController.class);
	
	@Autowired
	private YouDunService youDunService;
	
	/**
	 * 保存OCR信息
	 * @param jo
	 * @return
	 */
	@RequestMapping("/saveYouDunOCRInfo")
	public @ResponseBody JSONObject saveYouDunOCRInfo  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = youDunService.saveYouDunOCRInfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 保存身份证校验、人像校验结果
	 * @param jo
	 * @return
	 */
	@RequestMapping("/saveYouDunIDCheckInfo")
	public @ResponseBody JSONObject saveYouDunIDCheckInfo  (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = youDunService.saveYouDunIDCheckInfo(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/* @author yang.wu
	 * 类名称： getYouDunSign
	 * 创建时间：2017年5月11日 下午5:35:56
	 * @param jo
	 * @return JSONObject
	 * 类描述：有盾签名
	 */
	@RequestMapping("/getYouDunSign")
	public @ResponseBody JSONObject getYouDunSign (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = youDunService.getYouDunSign(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 有盾活体识别分数比对
	 * @param jo
	 * @return
	 */
	@RequestMapping("/getYouDunVerifyScore")
	public @ResponseBody JSONObject getYouDunVerifyScore (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = youDunService.getYouDunVerifyScore(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
	
	/**
	 * 有盾扫描身份证正面检查身份证是否已使用
	 * @param jo
	 * @return
	 */
	@RequestMapping("/checkIDUseOrNot")
	public @ResponseBody JSONObject checkIDUseOrNot (@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = youDunService.checkIDUseOrNot(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}
}
