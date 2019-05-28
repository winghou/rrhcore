package com.dao;

import java.util.List;
import java.util.Map;

import com.model.AppUserCoupon;

public interface AppUserCouponMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(AppUserCoupon record);

	int insertSelective(AppUserCoupon record);

	AppUserCoupon selectByPrimaryKey(Integer id);
	
	List<AppUserCoupon> selectByApprId(Integer apprId);
	
	List<AppUserCoupon> selectByApprIdAndStatus(Map<String, Object> pamams);
	
	//查询有效的优惠券
	List<AppUserCoupon> selectEffectiveByApprIdAndStatus(Map<String, Object> pamams);
	
	AppUserCoupon selectByApprIdAndCouponId(Map<String, Object> pamams);

	int updateByPrimaryKeySelective(AppUserCoupon record);

	int updateByPrimaryKey(AppUserCoupon record);
}
