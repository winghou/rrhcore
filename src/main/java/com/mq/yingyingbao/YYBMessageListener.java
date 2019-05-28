package com.mq.yingyingbao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.service.intf.YingYongBaoService;

public class YYBMessageListener implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(YYBMessageListener.class);

	@Autowired
	private  YingYongBaoService    yingYongBaoService;

	@Override
	public Action consume(Message msg, ConsumeContext context) {
		System.out.println("Receive: " + msg);
		String body = "";
		try {
			body = new String(msg.getBody(), "UTF-8");
			JSONObject jsonObject =JSONObject.parseObject(body);			
			yingYongBaoService.updateCurrentDataToYYB(jsonObject);
			return Action.CommitMessage;
		} catch (Exception e) {
			log.error("消费订单号为:" + body + "的消息失败，错误详情为：" + e);
			// 消费失败
			return Action.ReconsumeLater;
		}
	}

	
}
