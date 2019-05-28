package com.dao;

import java.util.List;

import com.model.AppYiTuScore;

public interface AppYiTuScoreMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppYiTuScore record);

    int insertSelective(AppYiTuScore record);

    AppYiTuScore selectByPrimaryKey(Integer id);
    
    List<AppYiTuScore> selectSizeOneDay(Integer appr_id);

    int updateByPrimaryKeySelective(AppYiTuScore record);

    int updateByPrimaryKey(AppYiTuScore record);
}