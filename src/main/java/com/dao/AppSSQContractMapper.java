package com.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.model.AppSSQContract;

public interface AppSSQContractMapper {

    int insertSelective(AppSSQContract record);

    AppSSQContract selectByPrimaryKey(Integer id);
    
    // 根据合同号查询
    AppSSQContract selectByContractIdAndType(@Param("contractId") String contractId, @Param("type") String type);
    
    //根据用户ID查询
    List<AppSSQContract> selectByApprIdAndType(@Param("apprId") Integer apprId, @Param("type") String type);
    
    //根据订单号查询
    AppSSQContract selectByWithdrawIdAndType(@Param("withdrawId") Integer withdrawId, @Param("type") String type);
    
    int updateByPrimaryKeySelective(AppSSQContract record);
	
}
