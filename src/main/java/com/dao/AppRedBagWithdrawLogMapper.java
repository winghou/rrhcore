package com.dao;

import java.util.List;

import com.model.AppRedBagWithdrawLog;

public interface AppRedBagWithdrawLogMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppRedBagWithdrawLog record);

	AppRedBagWithdrawLog selectByPrimaryKey(Integer id);
	
	List<AppRedBagWithdrawLog> selectByApprId(Integer apprId);

	int updateByPrimaryKeySelective(AppRedBagWithdrawLog record);
	
	List<AppRedBagWithdrawLog> qryRedEnvelopeDetail(Integer apprId);
	
	double qryUseTxSum(Integer apprId); 

}
