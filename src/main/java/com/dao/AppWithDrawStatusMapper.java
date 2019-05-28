package com.dao;

import com.model.AppWithDrawStatus;

public interface AppWithDrawStatusMapper {

	int deleteByPrimaryKey(Integer id);

    int insert(AppWithDrawStatus record);

    int insertSelective(AppWithDrawStatus record);

    AppWithDrawStatus selectByPrimaryKey(Integer id);
    
    AppWithDrawStatus selectByWfLogId(Integer wfLogId);
    
    AppWithDrawStatus selectByWithDrawId(Integer withId);
    
    int updateByPrimaryKeySelective(AppWithDrawStatus record);
    
    int updateByWithIdSelective(AppWithDrawStatus record);
}
