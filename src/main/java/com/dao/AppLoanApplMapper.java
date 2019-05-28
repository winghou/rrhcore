package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppLoanAppl;

public interface AppLoanApplMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanAppl record);

    int insertSelective(AppLoanAppl record);

    AppLoanAppl selectByPrimaryKey(Integer id);
    
    AppLoanAppl selectByitemCode(String itemCode);
    
    AppLoanAppl selectByUserId(Integer userId);
    
    List<AppLoanAppl> selectByInviter(String inviterInviteCode);
    
    //查询邀请的并已经放款的用户
    List<Map<String, Object>> selectLoanCountByInviter(String inviterInviteCode);
    
    //查询邀请的并已经认证成功的用户
    List<Map<String, Object>> selectAuthonCountByInviter(String inviterInviteCode);
    
    AppLoanAppl selectByInviterInviteCode(String inviterInviteCode);

    int updateByPrimaryKeySelective(AppLoanAppl record);

    int updateByPrimaryKey(AppLoanAppl record);
    
    // 查询用户有欺诈标记
    List<Map<String, Object>> selectOverPhoneByApprIdAndMiss(Map<String, Object> map);
    
    List<AppLoanAppl> selectByStartIdAndEndId(Map<String, Object> map);
    
    int updateUserInviteCode(List<AppLoanAppl> loanAppls);
    
}