package com.dao;

import com.model.IfmAliPayOrder;

public interface IfmAliPayOrderMapper {
	
    int deleteIfmAliPayOrder(String orderNo);
    
    int insertIfmAliPayOrder(IfmAliPayOrder order);

}
