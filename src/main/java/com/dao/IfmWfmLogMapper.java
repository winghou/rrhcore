package com.dao;

import java.util.Map;

import com.model.IfmWfmLog;

public interface IfmWfmLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmWfmLog record);

    int insertSelective(IfmWfmLog record);

    IfmWfmLog selectByPrimaryKey(Integer id);
    
    IfmWfmLog selectCheck(Integer id);
    
    IfmWfmLog otherIfmLog(Map<String,Object> params);
    
    int updateNextNode(Map<String,Object> params);
    
    int updateByPrimaryKeySelective(IfmWfmLog record);

    int updateByPrimaryKey(IfmWfmLog record);
}