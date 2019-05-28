package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppIncreaseAmtInfo;

public interface AppIncreaseAmtInfoMapper {

	List<AppIncreaseAmtInfo> selectByApprId(Integer apprId);
	
	public AppIncreaseAmtInfo selectByApprIdAndType(Map<String,Object> param);
	
	public void insertSelective(AppIncreaseAmtInfo appIncreaseAmtInfo);
	
	public void updateByApprIdAndType(AppIncreaseAmtInfo appIncreaseAmtInfo);

	List<AppIncreaseAmtInfo> qqAuthorizeByApprId(Integer apprId);

	List<AppIncreaseAmtInfo> weChatAuthorizeByApprId(Integer apprId);
}
