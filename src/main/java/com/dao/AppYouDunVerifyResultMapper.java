package com.dao;

import java.util.List;

import com.model.AppYouDunVerifyResult;

public interface AppYouDunVerifyResultMapper {

	
	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppYouDunVerifyResult record);

    AppYouDunVerifyResult selectByPrimaryKey(Integer id);
    
    List<AppYouDunVerifyResult> selectByApprId(Integer apprId);
    
    List<AppYouDunVerifyResult> selectByApprIdThisDay(Integer apprId);
    
    AppYouDunVerifyResult selectByOrderId(String orderId);
    
    int updateByPrimaryKeySelective(AppYouDunVerifyResult record);
}
