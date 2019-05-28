package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppMessageMapper;
import com.dao.AppUserMapper;
import com.dao.PersonalDataMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppMessage;
import com.model.AppUser;
import com.service.intf.PersonDataQueryService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class PersonDataQueryImpl implements PersonDataQueryService {
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private PersonalDataMapper personalDataMapper;
	@Autowired
	private AppMessageMapper appMessageMapper;

	/**
	 * 查询资料进度
	 */
	@Override
	public JSONObject querySchedule(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			// 学生借款功能暂时关闭
			if ("0".equals(StringUtil.nvl(mch.getOrgId()))) {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "暂不提供该功能");
				return detail;
			}
			AppLoanAppl appl = appLoanApplMapper.selectByitemCode(mch.getUserName());
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
			if (!"8".equals(appLoanCtm.getSchedule_status())) {
				detail.put("scheduleStatus", appLoanCtm.getSchedule_status());
				detail.put("sjStatus", "");
			} else if ("8".equals(appLoanCtm.getSchedule_status())) {
				if (null != appLoanCtm.getBind_card_time() && !"".equals(appLoanCtm.getBind_card_time())) { // 银行卡已认证
					// 是否是风控驳回的资料
					if ("1".equals(appl.getBaseInfoStatus())) {
						detail.put("scheduleStatus", "10");
						detail.put("sjStatus", "");
					} else if ("1".equals(appl.getContactInfoStatus())) {
						detail.put("scheduleStatus", "11");
						detail.put("sjStatus", "");
					} else if ("1".equals(appl.getZhimaStatus())) {
						detail.put("scheduleStatus", "12");
						detail.put("sjStatus", "");
					} else if ("1".equals(appl.getOperatorStatus())) {
						detail.put("scheduleStatus", "13");
						detail.put("sjStatus", "");
					} else {
						// 资料进度
						detail.put("scheduleStatus", appLoanCtm.getSchedule_status());
						// 社交账号是否填写
						Map<String, Object> map = new HashMap<>();
						map.put("cntType", "1");
						map.put("apprId", appl.getId());
						AppLoanCtmCnt sj = appLoanCtmCntMapper.queryByType(map);
						if (null == sj || ("".equals(StringUtil.nvl(sj.getCntCommt()))
								&& "".equals(StringUtil.nvl(sj.getCntPass())))) {
							// 社交账号未填写
							detail.put("sjStatus", "0");
						} else if (!"".equals(StringUtil.nvl(sj.getCntCommt()))
								&& "".equals(StringUtil.nvl(sj.getCntPass()))) {
							// 只填写QQ号
							detail.put("sjStatus", "1");
						} else if ("".equals(StringUtil.nvl(sj.getCntCommt()))
								&& !"".equals(StringUtil.nvl(sj.getCntPass()))) {
							// 只填写微信号
							detail.put("sjStatus", "2");
						} else {
							// 社交账号填写完成
							detail.put("sjStatus", "3");
						}
					}
				} else { // 其他资料认证完成，但银行卡未认证
					detail.put("scheduleStatus", "9");
					detail.put("sjStatus", "");
				}
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录，请先登录");
		}
		return detail;
	}

	@Override
	public JSONObject selectRewardAmt(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		String messageId = JsonUtil.getJStringAndCheck(params, "messageId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		if (null != appUser) {
			AppLoanAppl appl = appLoanApplMapper.selectByitemCode(appUser.getUserName());
			AppLoanCtm loanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
			String schduleStatus = loanCtm.getSchedule_status();
			if ("8".equals(schduleStatus)) {
				AppMessage appMessage = appMessageMapper.selectByPrimaryKey(Integer.parseInt(messageId));
				if (null != appMessage) {
					List<Map<String, Object>> maps = personalDataMapper.selectRewardAmt(Integer.parseInt(userId));
					if (maps != null && !maps.isEmpty() && maps.size() > 0) {
						// 邀请的个数贷款的人数
						int c1 = 0;
						// 邀请的成功放款的个数
						int c2 = 0;
						String isSame = "";
						String loanSame = "";
						int amt = 0;
						JSONArray dataList = new JSONArray();
						for (Map<String, Object> map : maps) {
							if (!loanSame.equals(StringUtil.toString(map.get("id")))) {
								// 统计已放款人数
								if ("1".equals(StringUtil.toString(map.get("ctm_status")))
										|| "2".equals(StringUtil.toString(map.get("ctm_status")))
										|| "3".equals(StringUtil.toString(map.get("ctm_status")))) {
									JSONObject jo = new JSONObject();
									c2++;
									jo.put("name", StringUtil.toString(map.get("custom_name")));
									jo.put("registTime",
											DateUtil.format(
													DateUtil.parseDate(StringUtil.toString(map.get("create_date"))),
													"yyyy-MM-dd"));
									jo.put("loanStatus", "已放款");
									dataList.add(jo);
									loanSame = StringUtil.toString(map.get("id"));
								}
							}
							if (!isSame.equals(StringUtil.toString(map.get("id")))) {
								// 统计绑定用户人数
								JSONObject jo = new JSONObject();
								c1++;
								jo.put("name", StringUtil.toString(map.get("custom_name")));
								jo.put("registTime", DateUtil.format(
										DateUtil.parseDate(StringUtil.toString(map.get("create_date"))), "yyyy-MM-dd"));
								jo.put("loanStatus", "已绑定");
								dataList.add(jo);
								isSame = StringUtil.toString(map.get("id"));
							}
						}
						if (c1 >= 5) {
							amt += 10;
						}
						amt += c2 * 30;
						detail.put("dataList", dataList);
						detail.put("amt", amt);
					} else {
						detail.put(Consts.RESULT, ErrorCode.FAILED);
						detail.put(Consts.RESULT_NOTE, "无奖励");
					}
				} else {
					detail.put(Consts.RESULT, ErrorCode.FAILED);
					detail.put(Consts.RESULT_NOTE, "活动不存在");
				}
			} else {
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "用户资料未完全认证");
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
		}
		return detail;
	}
	
	/**
	 * 查询个人信息
	 */
	@Override
	public JSONObject selectPersonalInfo(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, true, detail);
		if (detail.containsKey(Consts.RESULT)) {
			return detail;
		}
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if (null != mch) {
			userId = mch.getUserid() + "";
			AppLoanAppl appLoanAppl = appLoanApplMapper.selectByUserId(Integer.parseInt(userId));
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appLoanAppl.getId());
			String identityCard = appLoanCtm.getIdentityCard();
			String schduleStatus = appLoanCtm.getSchedule_status();
			if (0 >= Integer.parseInt(schduleStatus)) {
				detail.put("name", "未填写");
				detail.put("phone", appLoanAppl.getItemCode());
				detail.put("identityNo", "未认证");
				detail.put(Consts.RESULT, ErrorCode.FAILED);
				detail.put(Consts.RESULT_NOTE, "您的信息还未认证，请前往认证");
			} else {
				detail.put("name", appLoanCtm.getCustomName());
				detail.put("phone", appLoanAppl.getItemCode());
				if (null != identityCard && !"".equals(identityCard)) {
					if (18 == identityCard.length()) {
						identityCard = identityCard.substring(0, 3) + "***********" + identityCard.substring(14, 18);
					} else {
						identityCard = identityCard.substring(0, 3) + "********" + identityCard.substring(11, 15);
					}
				} else {
					identityCard = "未认证";
				}
				detail.put("identityNo", identityCard);
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
		}
		return detail;
	}
	
}
