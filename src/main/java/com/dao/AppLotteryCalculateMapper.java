package com.dao;

import com.model.AppLotteryCalculate;

public interface AppLotteryCalculateMapper {

	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppLotteryCalculate record);

    AppLotteryCalculate selectByPrimaryKey(Integer id);
    
    AppLotteryCalculate selectByApprId(Integer apprId);

    int updateByPrimaryKeySelective(AppLotteryCalculate record);

}
