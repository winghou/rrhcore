package com.dao;

import java.util.List;

import com.model.AppPhoneBookTwo;

public interface AppPhoneBookTwoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppPhoneBookTwo record);

    int insertSelective(AppPhoneBookTwo record);

    AppPhoneBookTwo selectByPrimaryKey(Integer id);
    
    int insertListAppPhoneBook(List<AppPhoneBookTwo> record);
    
    List<AppPhoneBookTwo> selectByapprId(Integer apprId);

    int updateByPrimaryKeySelective(AppPhoneBookTwo record);

    int updateByPrimaryKey(AppPhoneBookTwo record);
}