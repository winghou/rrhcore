package com.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.AppAliPayMapper;
import com.model.AppAliPay;
import com.util.AliPayUtil;

@Service("myJob")
public class MyJobService
{
    private static final Logger logger = Logger.getLogger(MyJobService.class);
    @Autowired
    private AppAliPayMapper aliPayMapper;

    /*@Resource(name = "tMaintainService")
    private TMaintainServiceIntf tMaintainServiceIntf;*/


    public void init()
    {
//    	AppAliPay appAliPay=aliPayMapper.selectByPrimaryKey(1);
//    	AliPayUtil.SERVICE=appAliPay.getService();
//    	AliPayUtil.PARTNER=appAliPay.getPartner();
//    	AliPayUtil.SELLER=appAliPay.getSellerId();
//    	AliPayUtil.CHARSET=appAliPay.getInputCharset();
//    	AliPayUtil.NOTIFY_URL=appAliPay.getNotifyUrl();
//    	AliPayUtil.PAYMENT_TYPE=appAliPay.getPaymentType();
//    	AliPayUtil.PRIVATEKEY=appAliPay.getPrivateKey();
    }

    public void orderMonitor()
    {
        logger.debug("begin do  monitor.");
    }
}
