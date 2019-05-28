package com.mq.yingyingbao;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;

public class YYBMsgProducer {

	private final Logger logger = LoggerFactory.getLogger(YYBMsgProducer.class);

	private Producer producer;
	
	private String producerId;
	private String accessKey;
	private String secretKey;
	private String onsAddr;
	public void init() {
    	Properties producerProperties = new Properties();
    	producerProperties.setProperty(PropertyKeyConst.ProducerId, producerId);
    	producerProperties.setProperty(PropertyKeyConst.AccessKey, accessKey);
    	producerProperties.setProperty(PropertyKeyConst.SecretKey, secretKey);
    	producerProperties.setProperty(PropertyKeyConst.ONSAddr, onsAddr);
    	producer = ONSFactory.createProducer(producerProperties);
    	producer.start();
				
	}

    public void sendMessage(String topic, String tag, String text) {
        Message msg = new Message(topic, tag, text.getBytes());
        SendResult sendResult = null;
        try {
            sendResult = producer.send(msg);
            System.out.println("Rule MSG_ID:"+sendResult.getMessageId());
            logger.info("Rule MSG_ID:"+sendResult.getMessageId());
        } catch (Exception e) {
            logger.error(e.getMessage() + String.valueOf(sendResult));
        }
        // 当消息发送失败时如何处理
        if (sendResult == null) {
            // TODO
        }
    }
    
	/**
	 * Spring bean destroy-method
	 */
	public void destroy() {
		producer.shutdown();
	}

	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
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

	
}
