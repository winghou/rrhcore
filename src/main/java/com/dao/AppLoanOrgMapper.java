package com.dao;

import java.util.List;

import com.model.AppLoanOrg;

public interface AppLoanOrgMapper {
    int deleteByPrimaryKey(Integer loanOrgId);

    int insert(AppLoanOrg record);

    int insertSelective(AppLoanOrg record);

    AppLoanOrg selectByPrimaryKey(Integer loanOrgId);
    
    List<AppLoanOrg> selectBystatus();

    int updateByPrimaryKeySelective(AppLoanOrg record);

    int updateByPrimaryKey(AppLoanOrg record);
}