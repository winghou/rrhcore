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
import com.service.intf.OrderDetailAndMessageService;

@Controller
public class OrderDetailAndMessageController {
	private static final Logger logger = Logger.getLogger(OrderDetailAndMessageService.class);
	@Autowired
	private OrderDetailAndMessageService orderDetailAndMessageService;
	
	//查询订单详情(订单消息)
	@RequestMapping("/queryOrderDetail")
	public @ResponseBody JSONObject queryOrderDetail(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.queryOrderDetail(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	@RequestMapping("/queryMessaheCentre")
	public @ResponseBody JSONObject queryMessaheCentre(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.queryMessaheCentre(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	//查询一种消息
	@RequestMapping("/queryOneTypeMessage")
	public @ResponseBody JSONObject queryOneTypeMessage(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.queryOneTypeMessage(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	//查看消息详情
	@RequestMapping("/queryOneMessageByMesId")
	public @ResponseBody JSONObject queryOneMessageByMesId(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.queryOneMessageByMesId(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	//查询消息和公告
	@RequestMapping("/queryMessageAndAnnouncement")
	public @ResponseBody JSONObject queryMessageAndAnnouncement(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.queryMessageAndAnnouncement(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}

	}
	
	//消息已读标记
	@RequestMapping("/signMessageIsRead")
	public @ResponseBody JSONObject signMessageIsRead(@RequestBody JSONObject jo) {
		try {
			RequestTemplate rt = new RequestTemplate(jo);
			JSONObject detail = orderDetailAndMessageService.signMessageIsRead(rt.getParams());
			return new ResponseTemplate(jo, detail).getReturn();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new ResponseTemplate(jo).getReturn();
		}
	}

}
