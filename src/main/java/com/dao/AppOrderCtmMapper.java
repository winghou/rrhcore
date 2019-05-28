package com.dao;

import java.util.List;

import com.model.AppOrderCtm;

public interface AppOrderCtmMapper {
	List<AppOrderCtm> selectOrderCtmByDrawId(Integer drawId);
	AppOrderCtm selectByPrimaryKey(Integer id);
	void deleteByPrimaryKey(Integer id);
	void insertSelective(AppOrderCtm model);
	void updateByWithDrawIdSelective(AppOrderCtm model);
}
