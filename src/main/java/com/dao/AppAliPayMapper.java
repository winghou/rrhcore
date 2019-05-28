package com.dao;

import com.model.AppAliPay;

public interface AppAliPayMapper {
    int deleteByPrimaryKey(Integer payId);

    int insert(AppAliPay record);

    int insertSelective(AppAliPay record);

    AppAliPay selectByPrimaryKey(Integer payId);

    int updateByPrimaryKeySelective(AppAliPay record);

    int updateByPrimaryKey(AppAliPay record);
}