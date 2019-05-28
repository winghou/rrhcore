package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppMessage;

public interface AppMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppMessage record);

    int insertSelective(AppMessage record);

    AppMessage selectByPrimaryKey(Integer id);
    
    List<AppMessage> selectAllMes();
    
    List<AppMessage> selectByapprIdAndType(Map<String,Object> params);
    
    AppMessage selectByapprIdAndMessId(Map<String,Object> params);
    //订单消息
    List<Map<String,Object>> selectByapprIdAndTypeIsRead(Map<String,Object> params);
    
    List<Map<String,Object>> selectByapprIdAndTypeIsRead2(Map<String,Object> params);
    //公告消息
    List<Map<String,Object>> selectByapprIdAndTypeIsRead3(Map<String,Object> params);
    //所有个人消息
    List<Map<String,Object>> selectByapprIdAndTypeIsRead4(Map<String,Object> params);
    
    List<AppMessage> selectByType(String type);

    int updateByPrimaryKeySelective(AppMessage record);

    int updateByPrimaryKey(AppMessage record);
    //轮播图
    List<AppMessage> selectByPrimaryKeyAndTime(Map<String,Object> params);
}