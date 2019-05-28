package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppAgreementPayBank;

public interface AppAgreementPayBankMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppAgreementPayBank record);

    int insertSelective(AppAgreementPayBank record);

    AppAgreementPayBank selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppAgreementPayBank record);

    int updateByPrimaryKey(AppAgreementPayBank record);
    /*查询所有*/
    List<AppAgreementPayBank> selectByAgreementPay();
    
    AppAgreementPayBank selectByCodeAndName(Map<String, Object> map);
    
    AppAgreementPayBank selectByCode(String code);
    
    AppAgreementPayBank selectByName(String name);
}