package com.dao;

import java.util.List;

import com.model.IfmWithhold;

public interface IfmWithholdMapper {

    int insertSelective(IfmWithhold record);
    
    /**
     * 根据商户订单号查询
     * @param merchOrderNo
     * @return
     */
    IfmWithhold selectByMerchOrderNo(String merchOrderNo);

	List<IfmWithhold> selectByPlanId(String string);

	void updateByPrimaryKeySelective(IfmWithhold ifmWithhold);

}
