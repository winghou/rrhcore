package com.dao;

import java.util.List;

import com.model.AppPrize;

public interface AppPrizeMapper {

	int deleteByPrimaryKey(Integer id);

	int insertSelective(AppPrize record);

	AppPrize selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppPrize record);
	
	List<AppPrize> selectAll();
	
	int updatePrizeNum(Integer id);

}
