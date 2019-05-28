package com.dao;

import com.model.APPCredit;

public interface APPCreditMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(APPCredit record);

    int insertSelective(APPCredit record);

    APPCredit selectByPrimaryKey(Integer id);
    
    APPCredit selectByApprId(Integer apprId);

    int updateByPrimaryKeySelective(APPCredit record);

    int updateByPrimaryKey(APPCredit record);
}