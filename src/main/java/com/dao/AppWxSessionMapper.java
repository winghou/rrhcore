package com.dao;

import java.util.Map;

import com.model.AppWxSession;

public interface AppWxSessionMapper {
    int deleteByPrimaryKey(Integer id);
    
    int deleteByApprid(Integer id);

    int insert(AppWxSession record);

    int insertSelective(AppWxSession record);

    AppWxSession selectByPrimaryKey(Integer id);
    
    AppWxSession selectBywxToken(Map<String, Object> map);
    
    AppWxSession selectBywxUserId(Integer userId);

    int updateByPrimaryKeySelective(AppWxSession record);

    int updateByPrimaryKey(AppWxSession record);
}