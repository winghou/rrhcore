package com.dao;

import com.model.AppLoanBank;

public interface AppLoanBankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppLoanBank record);

    int insertSelective(AppLoanBank record);

    AppLoanBank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppLoanBank record);

    int updateByPrimaryKey(AppLoanBank record);
}