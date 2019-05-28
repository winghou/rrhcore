package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppUser;
import com.model.IfmBaseDict;
import com.service.intf.DrainagePromotionService;
import com.util.ErrorCode;
import com.util.JsonUtil;
@Service
public class DrainagePromotionServiceImpl implements DrainagePromotionService {

	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;
	/* @author yang.wu
	 * 类名称： DrainagePromotionServiceImpl
	 * 创建时间：2017年8月10日 上午11:59:25
	 * @see com.service.intf.DrainagePromotionService#getDrainagePromotionUrl(com.alibaba.fastjson.JSONObject)
	 * 类描述：获取用户引流url
	 */
	@Override
	public JSONObject getDrainagePromotionUrl(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		IfmBaseDict ifmBaseDict = new IfmBaseDict();
		ifmBaseDict.setDataType("DRAINAGE_PROMOTION_URL");
		ifmBaseDict.setItemKey("1");
		ifmBaseDict = ifmBaseDictMapper.selectBykey(ifmBaseDict);
		detail.put("drainagePromotionUrl", ifmBaseDict.getItemValue());
		return detail;
	}

}
