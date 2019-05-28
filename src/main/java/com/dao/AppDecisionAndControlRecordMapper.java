package com.dao;

import com.model.AppDecisionAndControlRecord;

public interface AppDecisionAndControlRecordMapper {

	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppDecisionAndControlRecord record);

    AppDecisionAndControlRecord selectByPrimaryKey(Integer id);
    
    AppDecisionAndControlRecord selectByOrderId(String orderId);
    
    AppDecisionAndControlRecord selectByWithId(Integer withId);
    
    AppDecisionAndControlRecord selectLatestPassedRecord(Integer apprId);
    
    int updateByPrimaryKeySelective(AppDecisionAndControlRecord record);
	
}
