package com.dao;

import java.util.List;

import com.model.APPContractTemplete;

public interface APPContractTempleteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(APPContractTemplete record);

    int insertSelective(APPContractTemplete record);

    APPContractTemplete selectByPrimaryKey(Integer id);
    
    List<APPContractTemplete> selectTem();
    
    List<APPContractTemplete> selectTmpUrl(String tmpid);

    int updateByPrimaryKeySelective(APPContractTemplete record);

    int updateByPrimaryKey(APPContractTemplete record);
}