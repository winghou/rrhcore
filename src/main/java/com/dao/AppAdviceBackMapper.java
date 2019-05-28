package com.dao;

import com.model.AppAdviceBack;

public interface AppAdviceBackMapper {
    int insert(AppAdviceBack record);

    int insertSelective(AppAdviceBack record);
}