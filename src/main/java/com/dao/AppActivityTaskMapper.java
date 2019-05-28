package com.dao;

import java.util.List;

import com.model.AppActivityTask;

public interface AppActivityTaskMapper {

	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppActivityTask record);

    AppActivityTask selectByPrimaryKey(Integer id);
    
    List<AppActivityTask> selectEffectiveByType(String type);
    
    int updateByPrimaryKeySelective(AppActivityTask record);

}
