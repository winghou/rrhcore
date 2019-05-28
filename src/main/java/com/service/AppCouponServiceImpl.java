package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppCouponMapper;
import com.dao.AppLoanApplMapper;
import com.dao.AppUserCouponMapper;
import com.dao.AppUserMapper;
import com.frame.Consts;
import com.model.AppCoupon;
import com.model.AppLoanAppl;
import com.model.AppUser;
import com.model.AppUserCoupon;
import com.service.intf.AppCouponService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;

@Service
public class AppCouponServiceImpl implements AppCouponService {
	
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppUserCouponMapper appUserCouponMapper;
	@Autowired
	private AppCouponMapper appCouponMapper;
	
	/**
	 * 查询用户优惠券
	 */
	@Override
	public JSONObject queryCoupon(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		
		userId = mch.getUserid() + "";
		AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
		if(null == appLoanAppl){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户数据异常，请联系客服！");
			return detail;
		}
		JSONArray jsonArray = new JSONArray();
		Map<String, Object> map0 = new HashMap<String, Object>();
		map0.put("apprId", appLoanAppl.getId());
		map0.put("status", "0");
		List<AppUserCoupon> userCoupons = appUserCouponMapper.selectEffectiveByApprIdAndStatus(map0);
		if(null != userCoupons && userCoupons.size() > 0){
			int couponId = 0;
			AppCoupon appCoupon = null;
//			AppUserCoupon appUserCoupon = null;
			JSONObject jsonObject = null;
			for(AppUserCoupon userCoupon : userCoupons){
				couponId = userCoupon.getCouponId();
				appCoupon = appCouponMapper.selectByPrimaryKey(couponId);
//				Map<String, Object> map1 = new HashMap<String, Object>();
//				map1.put("apprId", appLoanAppl.getId());
//				map1.put("couponId", appCoupon.getId());
//				appUserCoupon = appUserCouponMapper.selectByApprIdAndCouponId(map1);
//				if(null != appUserCoupon){
				jsonObject = new JSONObject();
				jsonObject.put("couponId", userCoupon.getId() + "");
				jsonObject.put("use", appCoupon.getUses());
				jsonObject.put("type", appCoupon.getType());
				jsonObject.put("decription", appCoupon.getDescription());
				jsonObject.put("lastDate", DateUtil.format(userCoupon.getEndDate(), "yyyy-MM-dd"));
				jsonArray.add(jsonObject);
//				}
			}
		}
		detail.put("couponLists", jsonArray);
		return detail;
	}

}
