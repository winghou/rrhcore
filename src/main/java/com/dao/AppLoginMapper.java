package com.dao;

import com.model.AppLogin;

public interface AppLoginMapper {
    int deleteByPrimaryKey(Integer lgnid);

    int insert(AppLogin record);

    int insertSelective(AppLogin record);

    AppLogin selectByPrimaryKey(Integer lgnid);
    
    AppLogin login(AppLogin ifmLogin);
    
    AppLogin selectByPhone(String phone);

    int updateByPrimaryKeySelective(AppLogin record);

    int updateByPrimaryKey(AppLogin record);
}