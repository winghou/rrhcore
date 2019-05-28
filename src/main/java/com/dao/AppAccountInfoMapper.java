package com.dao;

import com.model.AppAccountInfo;

public interface AppAccountInfoMapper {

	AppAccountInfo selectByApprId(Integer apprId);
}
