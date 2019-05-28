package com.dao;

import java.util.Map;

import com.model.AppPhoneValicode;

public interface AppPhoneValicodeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppPhoneValicode record);

    int insertSelective(AppPhoneValicode record);

    AppPhoneValicode selectByPrimaryKey(Integer id);
    
    AppPhoneValicode selectByPhone(String param);
    
    AppPhoneValicode selectByPhoneAndStatus(Map<String, Object> pamams);

    int updateByPrimaryKeySelective(AppPhoneValicode record);

    int updateByPrimaryKey(AppPhoneValicode record);

	AppPhoneValicode selectByPhoneInStatus(Map<String, Object> map);

	AppPhoneValicode selectByPhoneAndStatusCode(Map<String, Object> map);
}