package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppZhimaScore;

public interface AppZhimaScoreMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppZhimaScore record);

    int insertSelective(AppZhimaScore record);

    AppZhimaScore selectByPrimaryKey(Integer id);
    
    AppZhimaScore selectZhimaScoreByUserId(Integer userId);
    
    List<AppZhimaScore> selectByUserIdAndStatus(Map<String, Object> map);
    
    Map<String,Object> selectByUserId(Integer id);

    int updateByPrimaryKeySelective(AppZhimaScore record);

    int updateByPrimaryKey(AppZhimaScore record);
}