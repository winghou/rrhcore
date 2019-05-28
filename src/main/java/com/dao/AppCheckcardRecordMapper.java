package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppCheckcardRecord;

public interface AppCheckcardRecordMapper {

	AppCheckcardRecord selectByPrimaryKey(Integer id);
	
	void insertSelective(AppCheckcardRecord appCheckcardRecord);
		
	AppCheckcardRecord selectByApprId(Integer appr_id);
	
	//根据outOrderNo查询
	public AppCheckcardRecord selectIfmBankCardCheckByUuid(String uuid);
		
	//查询用户当日验卡次数
	public List<AppCheckcardRecord> selectAppCheckcardRecordThisDay(String identityNo);
	
	int updateByPrimaryKeySelective(AppCheckcardRecord record);

	AppCheckcardRecord selectByBankIdCard(Map<String, Object> map);
}
