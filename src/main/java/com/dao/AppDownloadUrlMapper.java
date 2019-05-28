package com.dao;

import java.util.List;

import com.model.AppDownloadUrl;

public interface AppDownloadUrlMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppDownloadUrl record);

    int insertSelective(AppDownloadUrl record);

    AppDownloadUrl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppDownloadUrl record);

    int updateByPrimaryKeyWithBLOBs(AppDownloadUrl record);

    int updateByPrimaryKey(AppDownloadUrl record);

	List<AppDownloadUrl> selectByDownloadType(String string);
}