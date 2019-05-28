package com.dao;

import java.util.List;

import com.model.IfmBankCardCheck;

public interface IfmBankCardCheckMapper {

	//根据outOrderNo查询
	public IfmBankCardCheck selectIfmBankCardCheckByOutOrderNo(String outOrderNo);
	
	//查询用户当日验卡次数
	public List<IfmBankCardCheck> selectIfmBankCardByCertNoThisDay(String identityNo);
}
