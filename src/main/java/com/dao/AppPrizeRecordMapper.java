package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppPrizeRecord;

public interface AppPrizeRecordMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppPrizeRecord record);

	AppPrizeRecord selectByPrimaryKey(Integer id);

	List<AppPrizeRecord> selectByapprId(Integer apprId);
	
	// 查询中奖记录（不包括谢谢参与）
	List<AppPrizeRecord> selectByapprIdWithoutThx(Integer apprId);
	
	List<Map<String, Object>> selectByapprIdGroupByPrizeAndDate(Integer apprId);
	
}
