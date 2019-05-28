package com.dao;

import com.model.IfmLogin;

public interface IfmLoginMapper {
    int deleteByPrimaryKey(Integer lgnid);

    int insert(IfmLogin record);

    int insertSelective(IfmLogin record);

    IfmLogin selectByPrimaryKey(Integer lgnid);
    
    IfmLogin login(IfmLogin ifmLogin);
    
    IfmLogin selectByPhone(String phone);

    int updateByPrimaryKeySelective(IfmLogin record);

    int updateByPrimaryKey(IfmLogin record);
}