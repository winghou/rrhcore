package com.dao;

import com.model.IfmSysLoginLog;

public interface IfmSysLoginLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmSysLoginLog record);

    int insertSelective(IfmSysLoginLog record);

    IfmSysLoginLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IfmSysLoginLog record);

    int updateByPrimaryKey(IfmSysLoginLog record);
}