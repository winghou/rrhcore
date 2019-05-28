package com.dao;

import java.util.List;

import com.model.IfmUser;

public interface IfmUserMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(IfmUser record);

    int insertSelective(IfmUser record);

    IfmUser selectByPrimaryKey(Integer userid);
    
    IfmUser selectByMchVersion(String version);
    
    List<IfmUser> selectByUserName(String userName);

    int updateByPrimaryKeySelective(IfmUser record);

    int updateByPrimaryKey(IfmUser record);
    
    IfmUser selectByPhone(String phone);
}