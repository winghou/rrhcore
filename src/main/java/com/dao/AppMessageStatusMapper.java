package com.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.model.AppMessageStatus;

public interface AppMessageStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppMessageStatus record);

    int insertSelective(AppMessageStatus record);

    AppMessageStatus selectByPrimaryKey(Integer id);
    
    AppMessageStatus selectIsRead(Map<String,Object> params);
    
    List<AppMessageStatus> selectByMessageId(@Param("map")Map<String,Object> params);

    int updateByPrimaryKeySelective(AppMessageStatus record);

    int updateByPrimaryKey(AppMessageStatus record);
}