package com.dao;

import com.model.AppSysLoginLog;

import java.util.List;

public interface AppSysLoginLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppSysLoginLog record);

    int insertSelective(AppSysLoginLog record);

    AppSysLoginLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppSysLoginLog record);

    int updateByPrimaryKey(AppSysLoginLog record);


    List<AppSysLoginLog> getLastMonthRRHLogninData();

}