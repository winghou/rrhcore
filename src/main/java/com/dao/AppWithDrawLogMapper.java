package com.dao;

import java.util.List;

import com.model.AppWithDrawLog;

public interface AppWithDrawLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppWithDrawLog record);

    int insertSelective(AppWithDrawLog record);

    AppWithDrawLog selectByPrimaryKey(Integer id);
    
    List<AppWithDrawLog> selectBywithDrawId(Integer id);

    int updateByPrimaryKeySelective(AppWithDrawLog record);

    int updateByPrimaryKey(AppWithDrawLog record);
}