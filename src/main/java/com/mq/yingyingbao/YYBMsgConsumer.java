package com.mq.yingyingbao;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

/**
 * MQ 接收消息示例 Demo
 */
public class YYBMsgConsumer {

	private final Logger logger = LoggerFactory.getLogger(YYBMsgConsumer.class);

	private Consumer consumer;
	
	private String consumerId;
	private String accessKey;
	private String secretKey;
	private String onsAddr;
	private String topic;
	private String tag;
	@Autowired
	private YYBMessageListener tcpMessageListener;
	
	public void init() {
		Properties consumerProperties = new Properties();
        consumerProperties.setProperty(PropertyKeyConst.ConsumerId, consumerId);
        consumerProperties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        consumerProperties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        consumerProperties.setProperty(PropertyKeyConst.ONSAddr, onsAddr);
        consumerProperties.put(PropertyKeyConst.ConsumeThreadNums,60);
        
        consumer = ONSFactory.createConsumer(consumerProperties);
        consumer.subscribe(topic, tag, tcpMessageListener);
        consumer.start();
	}
	
	public void destroy() {
		consumer.shutdown();
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getOnsAddr() {
		return onsAddr;
	}

	public void setOnsAddr(String onsAddr) {
		this.onsAddr = onsAddr;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
