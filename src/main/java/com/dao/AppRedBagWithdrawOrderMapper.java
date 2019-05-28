package com.dao;

import java.util.List;

import com.model.AppRedBagWithdrawOrder;

public interface AppRedBagWithdrawOrderMapper {

	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppRedBagWithdrawOrder record);

    AppRedBagWithdrawOrder selectByPrimaryKey(Integer id);
    
    List<AppRedBagWithdrawOrder> selectByApprId(Integer apprId);
    
    int updateByPrimaryKeySelective(AppRedBagWithdrawOrder record);

}
