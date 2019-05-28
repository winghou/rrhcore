package com.dao;

import com.model.AppXiaoanCheckcardRecord;

public interface AppXiaoanCheckcardRecordMapper {

	AppXiaoanCheckcardRecord selectByPrimaryKey(Integer id);
	
	void insertSelective(AppXiaoanCheckcardRecord appXiaoanCheckcardRecord);
		
	AppXiaoanCheckcardRecord selectByApprId(Integer appr_id);
}
