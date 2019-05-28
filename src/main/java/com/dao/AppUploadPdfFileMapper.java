package com.dao;

import com.model.AppUploadPdfFile;

public interface AppUploadPdfFileMapper {
	int deleteByPrimaryKey(Integer id);

    int insert(AppUploadPdfFile record);

    int insertSelective(AppUploadPdfFile record);

    AppUploadPdfFile selectByPrimaryKey(Integer id);
    
    AppUploadPdfFile selectByApplyNo(String param);
    
    AppUploadPdfFile selectByWithDrawId(String param);
    
    int updateByPrimaryKeySelective(AppUploadPdfFile record);
    
    int updateByWithIdSelective(AppUploadPdfFile record);
}
