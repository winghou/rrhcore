package com.dao;

import com.model.AppPrizeAcount;

public interface AppPrizeAcountMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppPrizeAcount record);

	AppPrizeAcount selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppPrizeAcount record);
	
	int updateRemainningNum(Integer id);
	
	AppPrizeAcount selectByapprId(Integer apprId);
	
}
