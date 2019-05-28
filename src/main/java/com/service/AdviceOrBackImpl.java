package com.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppAdviceBackMapper;
import com.dao.AppUserMapper;
import com.dao.IfmBaseDictMapper;
import com.frame.Consts;
import com.model.AppAdviceBack;
import com.model.AppUser;
import com.service.intf.AdviceOrbackService;
import com.util.ErrorCode;
import com.util.JsonUtil;
@Service
public class AdviceOrBackImpl implements AdviceOrbackService {
	@Autowired
	private AppAdviceBackMapper appAdviceBackMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private IfmBaseDictMapper ifmBaseDictMapper;

	/* @author yang.wu
	 * 类名称： AdviceOrBackImpl
	 * 创建时间：2017年5月22日 下午2:29:10
	 * @see com.service.intf.AdviceOrbackService#adviceOrbackService(com.alibaba.fastjson.JSONObject)
	 * 类描述：意见反馈
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject adviceOrbackService(JSONObject params) throws Exception{
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);  //用户id
		String advice = JsonUtil.getJStringAndCheck(params, "advice", null, true, detail);  //反馈内容
		String type = JsonUtil.getJStringAndCheck(params, "type", null, true, detail);   //反馈类型
		String contactWay = JsonUtil.getJStringAndCheck(params, "contactWay", null, false, detail);  //联系方式
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		AppAdviceBack appAdviceBack=new AppAdviceBack();
		appAdviceBack.setUserid(mch.getUserid());
		appAdviceBack.setContent(advice);
		appAdviceBack.setType(type);
		appAdviceBack.setContactWay(contactWay);
		appAdviceBackMapper.insertSelective(appAdviceBack);
		detail.put(Consts.RESULT_NOTE, "提交成功");
		return detail;
	}

	/**
	 * @author yang.wu
	 * @Description: TODO
	 * @date 2017年7月5日下午5:28:48
	 * @see com.service.intf.AdviceOrbackService#adviceOrbackTypeService(com.alibaba.fastjson.JSONObject)
	 * 类描述：意见反馈类型
	 */
	@Override
	public JSONObject adviceOrbackTypeService(JSONObject params) throws Exception {
		JSONObject detail = new JSONObject();
		List<Map<String, Object>> list = ifmBaseDictMapper.selectBaseDict("ADVICE_TYPE");
		JSONArray type = new JSONArray();
		for (Map<String, Object> map : list) {
			JSONObject typeList = new JSONObject();
			typeList.put("typeCode", map.get("ITEM_KEY"));
			typeList.put("typeName", map.get("ITEM_VALUE"));
			type.add(typeList);
		}
		detail.put("type", type);
		return detail;
	}

}
