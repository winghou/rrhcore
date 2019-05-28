package com.dao;

import java.util.List;

import com.model.AppAdvert;

public interface AppAdvertMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppAdvert record);

    int insertSelective(AppAdvert record);

    AppAdvert selectByPrimaryKey(Integer id);
    
    List<AppAdvert> queryAllAdvr();
    
    List<AppAdvert> queryAdvrByModule(String module);

    int updateByPrimaryKeySelective(AppAdvert record);

    int updateByPrimaryKey(AppAdvert record);
}