package com.dao;

import com.model.IfmLoanAppl;

public interface IfmLoanApplMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmLoanAppl record);

    int insertSelective(IfmLoanAppl record);

    IfmLoanAppl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IfmLoanAppl record);

    int updateByPrimaryKey(IfmLoanAppl record);
}