package com.dao;

import com.model.AppMorphoVelocity;

public interface AppMorphoVelocityCheckMapper {

	AppMorphoVelocity selectByApprId(Integer apprId);

	void updateByPrimaryKeySelective(AppMorphoVelocity morphoVelocity);

	void insertSelective(AppMorphoVelocity amv);

}
