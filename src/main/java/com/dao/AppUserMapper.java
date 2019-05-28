package com.dao;

import java.util.List;

import com.model.AppUser;

public interface AppUserMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(AppUser record);

    int insertSelective(AppUser record);

    AppUser selectByPrimaryKey(Integer userid);
    
    AppUser selectByMchVersion(String version);
    
    List<AppUser> selectByUserName(String userName);

    int updateByPrimaryKeySelective(AppUser record);

    int updateByPrimaryKey(AppUser record);
    
    AppUser selectByPhone(String phone);
}