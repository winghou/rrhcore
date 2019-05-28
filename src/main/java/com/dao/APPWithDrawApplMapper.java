package com.dao;

import com.model.APPWithDrawAppl;

import java.util.List;
import java.util.Map;

public interface APPWithDrawApplMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(APPWithDrawAppl record);

    int insertSelective(APPWithDrawAppl record);

    APPWithDrawAppl selectByPrimaryKey(Integer id);
    
    List<APPWithDrawAppl> selectAllByapprId(Integer id);
    
    List<APPWithDrawAppl> selectAllByapprId7Days(Integer id);
    
    List<APPWithDrawAppl> selectApplOrderToday(Integer id);
    
    List<APPWithDrawAppl> selectAllWaitingSignByapprId(Integer apprId);
    
    List<APPWithDrawAppl> selectApplByContractNo(String contractNo);
    
    List<APPWithDrawAppl> selectByApprIdAndStatusAndLoanStatus(Map<String,Object> map);
    
    List<Map<String,Object>> selectByapprIdAndStatus(Map<String,Object> map);
    
    List<Map<String,Object>> selectByByapprIdAndStatusOrderByRepayDate(Map<String,Object> map);
    
    List<Map<String,Object>> selectByapprIdAndLoanStatus(Map<String,Object> map);
    
    List<Map<String,Object>> selectOrderRecords(Integer apprId);
    
    APPWithDrawAppl selectLastOne(Integer apprId);
    
    int updateByPrimaryKeySelective(APPWithDrawAppl record);

    int updateByPrimaryKey(APPWithDrawAppl record);

    /**
     * 情人节投资统计
     **/
    List<Map<String, Object>> selectActivityOrderRecords(Map<String, Object> map);
    
    int updateByPrimaryKeyForCreditxScore(APPWithDrawAppl record);

}