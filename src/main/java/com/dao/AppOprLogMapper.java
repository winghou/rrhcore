package com.dao;

import java.util.List;

import com.model.AppOprLog;

public interface AppOprLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppOprLog record);

    int insertSelective(AppOprLog record);

    AppOprLog selectByPrimaryKey(Integer id);
    
    List<AppOprLog> selectByUserId(Integer id);

    int updateByPrimaryKeySelective(AppOprLog record);

    int updateByPrimaryKey(AppOprLog record);
}