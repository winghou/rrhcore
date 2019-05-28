package com.dao;

import com.model.AppMorphoBlackList;

public interface AppMorphoBlackListMapper {

	AppMorphoBlackList selectByApprId(Integer apprId);

	void updateByPrimaryKeySelective(AppMorphoBlackList morphoBlackList);

	void insertSelective(AppMorphoBlackList abl);
	
}
