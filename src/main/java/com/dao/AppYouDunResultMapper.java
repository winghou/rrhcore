package com.dao;

import java.util.List;

import com.model.AppYouDunResult;

public interface AppYouDunResultMapper {

	int deleteByPrimaryKey(Integer id);

    int insertSelective(AppYouDunResult record);

    AppYouDunResult selectByPrimaryKey(Integer id);
    
    AppYouDunResult selectByOrderId(String partnerOrderId);

    List<AppYouDunResult> selectByApprId(Integer apprId);
    
    int updateByPrimaryKeySelective(AppYouDunResult record);

}
