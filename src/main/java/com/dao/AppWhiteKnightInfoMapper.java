package com.dao;

import com.model.AppWhiteKnightInfo;

public interface AppWhiteKnightInfoMapper {

	AppWhiteKnightInfo selectByPrimaryKey(Integer id);
	
	void insertSelective(AppWhiteKnightInfo appWhiteKnightInfo);
	
	void updateByApprIdSelective(AppWhiteKnightInfo appWhiteKnightInfo);
	
	AppWhiteKnightInfo selectByApprId(Integer appr_id);
}
