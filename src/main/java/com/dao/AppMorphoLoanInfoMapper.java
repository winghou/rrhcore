package com.dao;

import com.model.AppMorphoLoanInfo;

public interface AppMorphoLoanInfoMapper {

	AppMorphoLoanInfo selectByApprId(Integer apprId);

	void insertSelective(AppMorphoLoanInfo mli);

	void updateByPrimaryKeySelective(AppMorphoLoanInfo morphoLoanInfo);

}
