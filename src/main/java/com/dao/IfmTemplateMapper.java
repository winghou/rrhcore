package com.dao;


import java.util.List;

import com.model.IfmTemplate;

public interface IfmTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IfmTemplate record);

    int insertSelective(IfmTemplate record);

    IfmTemplate selectByPrimaryKey(Integer id);
    
    List<IfmTemplate> qryTemplate(Integer tpId);

    int updateByPrimaryKeySelective(IfmTemplate record);

    int updateByPrimaryKey(IfmTemplate record);
}