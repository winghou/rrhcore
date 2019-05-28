package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppLoanJob;

public interface AppLoanJobMapper {
    int deleteByPrimaryKey(Integer jobId);

    int insert(AppLoanJob record);

    int insertSelective(AppLoanJob record);

    AppLoanJob selectByPrimaryKey(Integer jobId);
    
    List<Map<String,Object>> selectByjobId(String userId);
    
    List<AppLoanJob> selectAllJob(AppLoanJob record);
    
    int updateByPrimaryKeySelective(AppLoanJob record);

    int updateByPrimaryKey(AppLoanJob record);
}