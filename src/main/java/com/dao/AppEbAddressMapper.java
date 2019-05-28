package com.dao;

import com.model.AppEbAddress;

public interface AppEbAddressMapper {
    int deleteByPrimaryKey(Integer addressId);

    int insert(AppEbAddress record);

    int insertSelective(AppEbAddress record);

    AppEbAddress selectByPrimaryKey(Integer addressId);

    int updateByPrimaryKeySelective(AppEbAddress record);

    int updateByPrimaryKey(AppEbAddress record);
}