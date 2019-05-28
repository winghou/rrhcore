package com.dao;

import java.util.List;

import com.model.AppLoanCtmShip;

public interface AppLoanCtmShipMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanCtmShip record);

    int insertSelective(AppLoanCtmShip record);

    AppLoanCtmShip selectByPrimaryKey(Integer id);
    
    List<AppLoanCtmShip> selectByApprId(Integer apprId);

    int updateByPrimaryKeySelective(AppLoanCtmShip record);

    int updateByPrimaryKey(AppLoanCtmShip record);
}