package com.dao;

import java.util.List;

import com.model.CorpDept;

public interface CorpDeptMapper {
	/**
	 * 通过parentid查询子属部门
	 * 
	 * @param corpDept
	 * @return
	 */
	List<CorpDept> selectCorpDept(CorpDept corpDept);
}