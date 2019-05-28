package com.dao;

import java.util.List;

import com.model.AppzfbOrderList;

public interface AppzfbOrderListMapper {
    int deleteByPrimaryKey(Integer orderId);
    
    int deleteByUuid(String uuid);

    int insert(AppzfbOrderList record);

    int insertSelective(AppzfbOrderList record);

    AppzfbOrderList selectByPrimaryKey(Integer orderId);
    
    AppzfbOrderList selectByRepayId(Integer orderId);
    
    List<AppzfbOrderList> selectAllByRepayPlanId(Integer replanId);
    
    List<AppzfbOrderList> selectNotPaiedByRepayPlanId(Integer replanId);
    
    List<AppzfbOrderList> selectByUUID(String orderId);
    
    int updateByPrimaryKeySelective(AppzfbOrderList record);

    int updateByPrimaryKey(AppzfbOrderList record);
    
    public List<AppzfbOrderList> selectZfbOrderList(String repayId); 
    
    int updateByPrimaryKeySelectiveAndPaySouce(AppzfbOrderList record);
    
    public List<AppzfbOrderList> selectZfbOrderListAndPaySouce(String repayId); 
    
}