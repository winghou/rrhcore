package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppSysSession;

public interface AppSysSessionMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(AppSysSession record);

    int insertSelective(AppSysSession record);

    AppSysSession selectByPrimaryKey(Integer id);
    
    AppSysSession selectByToken(Map<String, Object> map);
    
    AppSysSession selectByUserId(Integer userId);
    
    int updateByPrimaryKeySelective(AppSysSession record);

    int updateByPrimaryKey(AppSysSession record);
    
    void updateByUserId(Integer userId);
}
