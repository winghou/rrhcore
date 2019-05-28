package com.dao;

import com.model.AppCoupon;

public interface AppCouponMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(AppCoupon record);

	int insertSelective(AppCoupon record);

	AppCoupon selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppCoupon record);

	int updateByPrimaryKey(AppCoupon record);
}
