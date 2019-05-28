package com.dao;

import java.util.List;

import com.model.AppSchoolName;

public interface AppSchoolNameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppSchoolName record);

    int insertSelective(AppSchoolName record);

    AppSchoolName selectByPrimaryKey(Integer id);
    
    List<AppSchoolName> selectSchool(String name);

    int updateByPrimaryKeySelective(AppSchoolName record);

    int updateByPrimaryKey(AppSchoolName record);
}