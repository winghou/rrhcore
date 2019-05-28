package com.dao;

import java.util.List;

import com.model.AppLevel;

public interface AppLevelMapper {
	
	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppLevel record);

    AppLevel selectByPrimaryKey(Integer id);

    AppLevel selectByLevel(String level);
    
    int updateByPrimaryKeySelective(AppLevel record);
    
    List<AppLevel> selectAll();

}
