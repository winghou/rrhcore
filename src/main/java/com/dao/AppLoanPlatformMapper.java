package com.dao;

import com.model.AppLoanCtmCnt;
import com.model.AppLoanPlatform;

import java.util.List;

public interface AppLoanPlatformMapper {


	/**
	 * 查询所有贷款平台
	 *
	 * @return List
	 */
	public List<AppLoanPlatform> selectPlatformList();


	public AppLoanPlatform selectByPrimaryKey(int id);
}
