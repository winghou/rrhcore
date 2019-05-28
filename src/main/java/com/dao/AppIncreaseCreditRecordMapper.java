package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppIncreaseCreditRecord;

public interface AppIncreaseCreditRecordMapper {

	int deleteByPrimaryKey(Integer addressId);

    int insertSelective(AppIncreaseCreditRecord record);

    AppIncreaseCreditRecord selectByPrimaryKey(Integer id);
    
    List<AppIncreaseCreditRecord> selectByApprId(Integer apprId);
    
    AppIncreaseCreditRecord selectByApprIdAndType(Map<String, Object> map);

    int updateByPrimaryKeySelective(AppIncreaseCreditRecord record);

}
