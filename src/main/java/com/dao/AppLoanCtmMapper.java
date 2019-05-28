package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppLoanCtm;

public interface AppLoanCtmMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanCtm record);

    int insertSelective(AppLoanCtm record);

    AppLoanCtm selectByPrimaryKey(Integer id);
    
    AppLoanCtm selectByapprId(Integer apprId);
    
    AppLoanCtm selectByIdentityCard(String identityCard);
    
    List<AppLoanCtm> selectOthersByIdentityNo(Map<String, Object> map);

    int updateByPrimaryKeySelective(AppLoanCtm record);

    int updateByPrimaryKey(AppLoanCtm record);
}