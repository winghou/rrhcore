package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppWxBind;

public interface AppWxBindMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppWxBind record);

    int insertSelective(AppWxBind record);

    AppWxBind selectByPrimaryKey(Integer id);
    
    AppWxBind selectByopenIdAndApprId(Map<String, String> map);
    
    AppWxBind selectByopenId(String openId);
    
    List<AppWxBind> selectByUuid(String uuid);
    
    List<AppWxBind> selectByPhone(String phone);

    int updateByPrimaryKeySelective(AppWxBind record);

    int updateByPrimaryKey(AppWxBind record);
    
    AppWxBind queryAppWxBindbyapprid(Integer apprid);
    
    void updateAppWxBindbyappridSx(String openid);
    
    List<AppWxBind> selectOpenId(String openId);
}