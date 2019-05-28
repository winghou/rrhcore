package com.dao;

import java.util.List;

import com.model.AppPhoneBook;

public interface AppPhoneBookMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppPhoneBook record);

    int insertSelective(AppPhoneBook record);
    
    int insertListAppPhoneBook(List<AppPhoneBook> record);

    AppPhoneBook selectByPrimaryKey(Integer id);
    
    List<AppPhoneBook> selectByApprId(Integer apprId);
    
    int deleteByApprId(Integer apprId);
    
    int updateByPrimaryKeySelective(AppPhoneBook record);

    int updateByPrimaryKey(AppPhoneBook record);
}