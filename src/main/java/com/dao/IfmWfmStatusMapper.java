package com.dao;

import java.util.List;
import java.util.Map;

import com.model.IfmWfmStatus;

public interface IfmWfmStatusMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmWfmStatus record);

    int insertSelective(IfmWfmStatus record);

    IfmWfmStatus selectByPrimaryKey(Integer id);
    
    List<IfmWfmStatus> selectIfmWfmStatus(Map<String,Object> params);

    int updateByPrimaryKeySelective(IfmWfmStatus record);

    int updateByPrimaryKey(IfmWfmStatus record);
}