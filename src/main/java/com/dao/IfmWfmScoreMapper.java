package com.dao;

import com.model.IfmWfmScore;

public interface IfmWfmScoreMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmWfmScore record);

    int insertSelective(IfmWfmScore record);

    IfmWfmScore selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IfmWfmScore record);

    int updateByPrimaryKey(IfmWfmScore record);
}