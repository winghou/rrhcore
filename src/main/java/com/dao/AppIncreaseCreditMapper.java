package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppIncreaseCredit;

public interface AppIncreaseCreditMapper {

	int deleteByPrimaryKey(Integer addressId);

    int insertSelective(AppIncreaseCredit record);

    AppIncreaseCredit selectByPrimaryKey(Integer id);
    
    List<AppIncreaseCredit> selectByApprId(Integer apprId);
    
    AppIncreaseCredit selectByApprIdAndType(Map<String, Object> map);

    int updateByPrimaryKeySelective(AppIncreaseCredit record);

}
