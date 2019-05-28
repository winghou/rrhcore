package com.dao;

import java.util.Map;

import com.model.AppRedBagWithdraw;

public interface AppRedBagWithdrawMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppRedBagWithdraw record);

	AppRedBagWithdraw selectByPrimaryKey(Integer id);
	
	AppRedBagWithdraw selectByApprId(Integer apprId);

	int updateByPrimaryKeySelective(AppRedBagWithdraw record);
	
	Map<String,Object> qryRedEnvelope(Integer apprId); 
	
}
