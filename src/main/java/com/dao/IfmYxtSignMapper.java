package com.dao;

import org.apache.ibatis.annotations.Param;

import com.model.IfmYxtSign;

public interface IfmYxtSignMapper {

    int insertSelective(IfmYxtSign record);

    IfmYxtSign selectByPrimaryKey(Integer id);
    
    int updateByPrimaryKeySelective(IfmYxtSign record);
    
    // 根据订单号和类型查询代扣签约
	IfmYxtSign selectByOrderIdAndType(@Param("apprId") String apprId, @Param("signType") String signType);

}
