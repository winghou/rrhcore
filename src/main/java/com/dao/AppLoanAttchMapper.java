package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppLoanAttch;

public interface AppLoanAttchMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanAttch record);

    int insertSelective(AppLoanAttch record);

    AppLoanAttch selectByPrimaryKey(Integer id);
    
    List<AppLoanAttch> selectByapprId(Integer apprId);
    
    AppLoanAttch selectByPicType(Map<String, Object> pamams);
    
    List<AppLoanAttch> selectByFileNameAndApprId(Map<String, Object> pamams);
    
    List<AppLoanAttch> selectPicIsAll(Integer apprId);
    
    int updateByPrimaryKeySelective(AppLoanAttch record);

    int updateByPrimaryKey(AppLoanAttch record);
}