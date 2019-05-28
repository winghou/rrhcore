package com.dao;

import com.model.AppPrizePersonInfo;

public interface AppPrizePersonInfoMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppPrizePersonInfo record);

	AppPrizePersonInfo selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppPrizePersonInfo record);
	
	AppPrizePersonInfo selectByApprId(Integer apprId);
	
}
