package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppJobUser;

public interface AppJobUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppJobUser record);

    int insertSelective(AppJobUser record);

    AppJobUser selectByPrimaryKey(Integer id);
    
    AppJobUser selectByJobIdAndUserId(Map<String,Object> param);
    
    List<AppJobUser> queryMyJobList(String userId);

    int updateByPrimaryKeySelective(AppJobUser record);

    int updateByPrimaryKey(AppJobUser record);
}