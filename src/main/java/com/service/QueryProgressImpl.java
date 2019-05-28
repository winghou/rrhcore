package com.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dao.AppLoanApplMapper;
import com.dao.AppLoanAttchMapper;
import com.dao.AppLoanCtmCntMapper;
import com.dao.AppLoanCtmMapper;
import com.dao.AppLoanCtmShipMapper;
import com.dao.AppOprLogMapper;
import com.dao.AppPhoneBookTwoMapper;
import com.dao.AppUserMapper;
import com.dao.AppZhimaScoreMapper;
import com.frame.Consts;
import com.model.AppLoanAppl;
import com.model.AppLoanAttch;
import com.model.AppLoanCtm;
import com.model.AppLoanCtmCnt;
import com.model.AppLoanCtmShip;
import com.model.AppOprLog;
import com.model.AppPhoneBookTwo;
import com.model.AppUser;
import com.service.intf.QueryProgressService;
import com.util.DateUtil;
import com.util.ErrorCode;
import com.util.JsonUtil;
import com.util.StringUtil;

@Service
public class QueryProgressImpl implements QueryProgressService {
	@Autowired
	private AppOprLogMapper appOprLogMapper;
	@Autowired
	private AppLoanApplMapper appLoanApplMapper;
	@Autowired
	private AppLoanCtmMapper appLoanCtmMapper;
	@Autowired
	private AppUserMapper appUserMapper;
	@Autowired
	private AppLoanCtmCntMapper appLoanCtmCntMapper;
	@Autowired
	private AppLoanCtmShipMapper appLoanCtmShipMapper;
	@Autowired
	private AppPhoneBookTwoMapper appPhoneBookTwoMapper;
	@Autowired
	private AppLoanAttchMapper appLoanAttchMapper;
	@Autowired
	private AppZhimaScoreMapper appZhimaScoreMapper;

	@Override
	public JSONObject queryProgress(JSONObject params) {
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
		userId = mch.getUserid()+"";
		List<AppOprLog> oprLogs = appOprLogMapper.selectByUserId(Integer.parseInt(userId));
		if (null != oprLogs && oprLogs.size() > 0) {
			JSONArray details = new JSONArray();
			for (AppOprLog appOprLog : oprLogs) {
				JSONObject jo = new JSONObject();
				jo.put("appendTime", DateUtil.format(appOprLog.getOprTime(), "yyyy-MM-dd HH:mm:ss"));
				jo.put("appendContent", appOprLog.getOprContent());
				details.add(jo);
			}
			detail.put("dataList", details);
		}
		return detail;
	}

	@Override
	public JSONObject queryMyStatus(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		String type = JsonUtil.getJStringAndCheck(params, "type", null, false, detail);
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
			AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
			List<AppLoanCtmCnt> cnts = appLoanCtmCntMapper.queryAllByApprId(appl.getId());
			if ("0".equals(type)) {
				JSONArray dataList = new JSONArray();
				if ("2".equals(appLoanCtm.getIdentity_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "0");
					jo.put("authenDesc", "身份证认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getIdentity_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "0");
					jo.put("authenDesc", "身份证认证");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getIdentity_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "0");
					jo.put("authenDesc", "身份证认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if (null != cnts && cnts.size() > 0) {
					Map<String, Object> map = new HashMap<>();
					map.put("cntType", "0");
					map.put("apprId", appl.getId());
					AppLoanCtmCnt appLoanCtmCnt = appLoanCtmCntMapper.queryByType(map);
					if (null != appLoanCtmCnt && "2".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "1");
						jo.put("authenDesc", "学籍认证");
						jo.put("authenStatus", "2");
						dataList.add(jo);
					} else if (null != appLoanCtmCnt
							&& "1".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "1");
						jo.put("authenDesc", "学籍认证");
						jo.put("authenStatus", "1");
						dataList.add(jo);
					} else if (null == appLoanCtmCnt) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "1");
						jo.put("authenDesc", "学籍认证");
						jo.put("authenStatus", "0");
						dataList.add(jo);
					}
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "1");
					jo.put("authenDesc", "学籍认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if ("2".equals(appLoanCtm.getSchool_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "2");
					jo.put("authenDesc", "学校认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getSchool_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "2");
					jo.put("authenDesc", "学校认证");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getSchool_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "2");
					jo.put("authenDesc", "学校认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if ("2".equals(appLoanCtm.getLxr_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "3");
					jo.put("authenDesc", "联系人认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getLxr_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "3");
					jo.put("authenDesc", "联系人认证");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getLxr_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "3");
					jo.put("authenDesc", "联系人认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if ("2".equals(appLoanCtm.getBank_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "4");
					jo.put("authenDesc", "绑定银行卡");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getBank_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "4");
					jo.put("authenDesc", "绑定银行卡");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getBank_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "4");
					jo.put("authenDesc", "绑定银行卡");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if ("2".equals(appLoanCtm.getFamily_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "5");
					jo.put("authenDesc", "家庭信息认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getFamily_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "5");
					jo.put("authenDesc", "家庭信息认证");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getFamily_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "5");
					jo.put("authenDesc", "家庭信息认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if ("2".equals(appLoanCtm.getCompany_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "6");
					jo.put("authenDesc", "工作信息认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else if ("1".equals(appLoanCtm.getCompany_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "6");
					jo.put("authenDesc", "工作信息认证");
					jo.put("authenStatus", "1");
					dataList.add(jo);
				} else if ("0".equals(appLoanCtm.getCompany_status())) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "6");
					jo.put("authenDesc", "工作信息认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				detail.put("dataList", dataList);
			} else if ("1".equals(type)) {
				JSONArray dataList = new JSONArray();
				// -------芝麻信用分
				Map<String, Object> zhimascore = appZhimaScoreMapper.selectByUserId(Integer.parseInt(userId));
				if (null != zhimascore 
						&& !"".equals(StringUtil.toString(zhimascore.get("zhima_credit_score")))
						&& 30 > Integer.parseInt(StringUtil.toString(zhimascore.get("days")))) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "1");
					jo.put("authenDesc", "芝麻信用认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "1");
					jo.put("authenDesc", "芝麻信用认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				// ------通讯录认证
				List<AppPhoneBookTwo> appPhoneBookTwo = appPhoneBookTwoMapper.selectByapprId(appl.getId());
				if (null != appPhoneBookTwo && appPhoneBookTwo.size() > 0) {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "2");
					jo.put("authenDesc", "通讯录认证");
					jo.put("authenStatus", "2");
					dataList.add(jo);
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "2");
					jo.put("authenDesc", "通讯录认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}

				// ------运营商认证
				if (null != cnts && cnts.size() > 0) {
					Map<String, Object> map = new HashMap<>();
					map.put("cntType", "2");
					map.put("apprId", appl.getId());
					AppLoanCtmCnt appLoanCtmCnt = appLoanCtmCntMapper.queryByType(map);
					if (null != appLoanCtmCnt && "2".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "3");
						jo.put("authenDesc", "运营商认证");
						jo.put("authenStatus", "2");
						dataList.add(jo);
					} else if (null != appLoanCtmCnt&& "1".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "3");
						jo.put("authenDesc", "运营商认证");
						jo.put("authenStatus", "1");
						dataList.add(jo);
					} else if (null == appLoanCtmCnt) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "3");
						jo.put("authenDesc", "运营商认证");
						jo.put("authenStatus", "0");
						dataList.add(jo);
					}
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "3");
					jo.put("authenDesc", "运营商认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if (null != cnts && cnts.size() > 0) {
					AppLoanCtmCnt tb = appLoanCtmCntMapper.selecttbAccountByapprId(appl.getId());
					AppLoanCtmCnt jd = appLoanCtmCntMapper.selectjdAccountByapprId(appl.getId());
					if (null != tb && null != tb.getCntDesc() && "2".equals(tb.getCntDesc()) && null != jd
							&& null != jd.getCntDesc() && "2".equals(jd.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "4");
						jo.put("authenDesc", "电商认证");
						jo.put("authenStatus", "2");
						dataList.add(jo);
					} else if (null != tb && null != jd &&"1".equals(tb.getCntDesc()) && "1".equals(jd.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "4");
						jo.put("authenDesc", "电商认证");
						jo.put("authenStatus", "1");
						dataList.add(jo);
					} else if (null == tb || null == jd) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "4");
						jo.put("authenDesc", "电商认证");
						jo.put("authenStatus", "0");
						dataList.add(jo);
					}
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "4");
					jo.put("authenDesc", "电商认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				// 公积金认证
				if (null != cnts && cnts.size() > 0) {
					Map<String, Object> map = new HashMap<>();
					map.put("cntType", "7");
					map.put("apprId", appl.getId());
					AppLoanCtmCnt appLoanCtmCnt = appLoanCtmCntMapper.queryByType(map);
					if (null != appLoanCtmCnt && "2".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "5");
						jo.put("authenDesc", "公积金认证");
						jo.put("authenStatus", "2");
						dataList.add(jo);
					} else if (null != appLoanCtmCnt
							&& "1".equals(appLoanCtmCnt.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "5");
						jo.put("authenDesc", "公积金认证");
						jo.put("authenStatus", "1");
						dataList.add(jo);
					} else if (null == appLoanCtmCnt) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "5");
						jo.put("authenDesc", "公积金认证");
						jo.put("authenStatus", "0");
						dataList.add(jo);
					}
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "5");
					jo.put("authenDesc", "公积金认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				if (null != cnts && cnts.size() > 0) {
					Map<String, Object> map = new HashMap<>();
					map.put("cntType", "5");
					map.put("apprId", appl.getId());
					Map<String, Object> map1 = new HashMap<>();
					map1.put("cntType", "6");
					map1.put("apprId", appl.getId());
					AppLoanCtmCnt zh = appLoanCtmCntMapper.queryByType(map);
					AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map1);
					if (null != zh && "2".equals(zh.getCntDesc())&&null != yx && "2".equals(yx.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "6");
						jo.put("authenDesc", "信用卡认证");
						jo.put("authenStatus", "2");
						dataList.add(jo);
					} else if (null != zh && "1".equals(zh.getCntDesc())&&null != yx && "1".equals(yx.getCntDesc())) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "6");
						jo.put("authenDesc", "信用卡认证");
						jo.put("authenStatus", "1");
						dataList.add(jo);
					} else if (null == zh||yx==null) {
						JSONObject jo = new JSONObject();
						jo.put("authenType", "6");
						jo.put("authenDesc", "信用卡认证");
						jo.put("authenStatus", "0");
						dataList.add(jo);
					}
				} else {
					JSONObject jo = new JSONObject();
					jo.put("authenType", "6");
					jo.put("authenDesc", "信用卡认证");
					jo.put("authenStatus", "0");
					dataList.add(jo);
				}
				detail.put("dataList", dataList);
			} else {
				List<AppLoanAttch> appLoanAttchs = appLoanAttchMapper.selectByapprId(appl.getId());
				JSONArray dataList = new JSONArray();
				JSONArray personPicList = new JSONArray();
				if (null != appLoanAttchs && appLoanAttchs.size() > 0) {
					for (AppLoanAttch appLoanAttch : appLoanAttchs) {
						JSONObject jo = new JSONObject();
						String str = "http://139.224.31.37:8080";
						//String str = "http://115.28.242.125:8081";
						jo.put("picType", appLoanAttch.getFileName());
						jo.put("picUrl", str + appLoanAttch.getFileUri());
						jo.put("smallPicUrl", str + appLoanAttch.getSmallPicUrl());
						jo.put("picDesc", appLoanAttch.getPicDesc() + "");
						if ("3".equals(appLoanAttch.getFileName())) {
							personPicList.add(jo);
						} else {
							dataList.add(jo);
						}

					}
				}
				detail.put("dataList", dataList);
				detail.put("personPicList", personPicList);
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户不存在");
		}
		return detail;
	}

	@Override
	public JSONObject queryPercent(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
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
			if("0".equals(appl.getStatus())||"4".equals(appl.getStatus())){
				detail.put("isSumbit", "0");
			}else{
				detail.put("isSumbit", "1");
			}
			// 学生打分
			if ("0".equals(appUser.getOrgId())) {
				int count = 0;
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
				List<AppLoanCtmCnt> cnts = appLoanCtmCntMapper.queryAllByApprId(appl.getId());
				List<AppPhoneBookTwo> appPhoneBookTwo = appPhoneBookTwoMapper.selectByapprId(appl.getId());
				if (!"".equals(appLoanCtm.getCustomName()) && appLoanCtm.getCustomName() != null) {
					count = count + 8;
				}
				if (!"".equals(appLoanCtm.getSchoolName()) && appLoanCtm.getSchoolName() != null) {
					count = count + 8;
				}
				List<AppLoanCtmShip> appLoanCtmShip = appLoanCtmShipMapper.selectByApprId(appl.getId());
				if (null != appLoanCtmShip && appLoanCtmShip.size() > 0) {
					count = count + 8;
				}
				if (!"".equals(appLoanCtm.getBankCard()) && appLoanCtm.getBankCard() != null) {
					count = count + 8;
				}
				if(null!=appPhoneBookTwo&&appPhoneBookTwo.size()>0){
					count = count + 8;
				}
				if (null != cnts && cnts.size() > 0) {
					for (AppLoanCtmCnt appLoanCtmCnt : cnts) {
						// 学籍
						if ("0".equals(appLoanCtmCnt.getCntType())) {
							count = count + 8;
						}
						// 电商
						if ("3".equals(appLoanCtmCnt.getCntType()) || "4".equals(appLoanCtmCnt.getCntType())) {
							count = count + 4;
						}
						// 运营商
						if ("2".equals(appLoanCtmCnt.getCntType())) {
							count = count + 8;
						}
					}
				}
				Map<String, Object> zhimascore = appZhimaScoreMapper.selectByUserId(Integer.parseInt(userId));
				if (null != zhimascore && !"".equals(StringUtil.toString(zhimascore.get("id")))) {
					count = count + 9;
				}
				List<AppLoanAttch> appLoanAttchs = appLoanAttchMapper.selectByapprId(appl.getId());
				if (null != appLoanAttchs && appLoanAttchs.size() > 0) {
					for (AppLoanAttch appLoanAttch : appLoanAttchs) {
						if ("0".equals(appLoanAttch.getFileName())) {
							count = count + 9;
						}
						if ("1".equals(appLoanAttch.getFileName())) {
							count = count + 9;
						}
						if ("2".equals(appLoanAttch.getFileName())) {
							count = count + 9;
						}
					}
				}
				detail.put("count", StringUtil.toString(count));
			} else {
				int count = 0;
				AppLoanCtm appLoanCtm = appLoanCtmMapper.selectByapprId(appl.getId());
				List<AppLoanCtmCnt> cnts = appLoanCtmCntMapper.queryAllByApprId(appl.getId());
				List<AppPhoneBookTwo> appPhoneBookTwo = appPhoneBookTwoMapper.selectByapprId(appl.getId());
				if (!"".equals(appLoanCtm.getCustomName()) && appLoanCtm.getCustomName() != null) {
					count = count + 8;
				}
				if (!"".equals(appLoanCtm.getIsMarry()) && appLoanCtm.getIsMarry() != null) {
					count = count + 7;
				}
				List<AppLoanCtmShip> appLoanCtmShip = appLoanCtmShipMapper.selectByApprId(appl.getId());
				if (null != appLoanCtmShip && appLoanCtmShip.size() > 0) {
					count = count + 7;
				}
				if (!"".equals(appLoanCtm.getBankCard()) && appLoanCtm.getBankCard() != null) {
					count = count + 7;
				}
				if (null != cnts && cnts.size() > 0) {
					for (AppLoanCtmCnt appLoanCtmCnt : cnts) {
						// 信用卡
						if ("6".equals(appLoanCtmCnt.getCntType())) {
							count = count + 7;
						}
						if ("5".equals(appLoanCtmCnt.getCntType())) {
							count = count + 7;
						}
						// 电商
						if ("3".equals(appLoanCtmCnt.getCntType()) || "4".equals(appLoanCtmCnt.getCntType())) {
							count = count + 4;
						}
						// 运营商
						if ("2".equals(appLoanCtmCnt.getCntType())) {
							count = count + 7;
						}
						// 公积金
						if ("5".equals(appLoanCtmCnt.getCntType())) {
							count = count + 7;
						}
					}
					Map<String, Object> zhimascore = appZhimaScoreMapper.selectByUserId(Integer.parseInt(userId));
					if (null != zhimascore && !"".equals(StringUtil.toString(zhimascore.get("id")))) {
						count = count + 8;
					}
				}
				if(null!=appPhoneBookTwo&&appPhoneBookTwo.size()>0){
					count = count + 6;
				}
				List<AppLoanAttch> appLoanAttchs = appLoanAttchMapper.selectByapprId(appl.getId());
				if (null != appLoanAttchs && appLoanAttchs.size() > 0) {
					for (AppLoanAttch appLoanAttch : appLoanAttchs) {
						if ("0".equals(appLoanAttch.getFileName())) {
							count = count + 7;
						}
						if ("1".equals(appLoanAttch.getFileName())) {
							count = count + 7;
						}
						if ("2".equals(appLoanAttch.getFileName())) {
							count = count + 7;
						}
					}
				}
				detail.put("count", StringUtil.toString(count));
			}
		} else {
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户不存在");
		}

		return detail;
	}

	@Override
	public JSONObject queryTbAndJdStutus(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLoanAppl appl = appLoanApplMapper.selectByitemCode(appUser.getUserName());
		AppLoanCtmCnt tb = appLoanCtmCntMapper.selecttbAccountByapprId(appl.getId());
		AppLoanCtmCnt jd = appLoanCtmCntMapper.selectjdAccountByapprId(appl.getId());
		JSONArray dataList = new JSONArray();
		if (null != tb && "2".equals(tb.getCntDesc())) {
			JSONObject tbList = new JSONObject();
			tbList.put("authenType", "0");
			tbList.put("authenDesc", "淘宝已认证");
			tbList.put("authenStatus", "1");
			dataList.add(tbList);
		} else if (null != tb &&"1".equals(tb.getCntDesc())){
			JSONObject tbList = new JSONObject();
			tbList.put("authenType", "0");
			tbList.put("authenDesc", "淘宝已填写");
			tbList.put("authenStatus", "1");
			dataList.add(tbList);
		} else if (null == tb) {
			JSONObject tbList = new JSONObject();
			tbList.put("authenType", "0");
			tbList.put("authenDesc", "淘宝未填写");
			tbList.put("authenStatus", "0");
			dataList.add(tbList);
		}

		if (null != jd && "2".equals(jd.getCntDesc())) {
			JSONObject jdList = new JSONObject();
			jdList.put("authenType", "1");
			jdList.put("authenDesc", "京东已认证");
			jdList.put("authenStatus", "1");
			dataList.add(jdList);
		} else if (null != jd&&"1".equals(jd.getCntDesc())) {
			JSONObject jdList = new JSONObject();
			jdList.put("authenType", "1");
			jdList.put("authenDesc", "京东已填写");
			jdList.put("authenStatus", "1");
			dataList.add(jdList);
		} else if (null == jd) {
			JSONObject jdList = new JSONObject();
			jdList.put("authenType", "1");
			jdList.put("authenDesc", "京东未填写");
			jdList.put("authenStatus", "0");
			dataList.add(jdList);

		}
		detail.put("dataList", dataList);
		return detail;
	}

	@Override
	public JSONObject queryXykAndYx(JSONObject params) {
		JSONObject detail = new JSONObject();
		String userId = JsonUtil.getJStringAndCheck(params, "userId", null, false, detail);
		AppUser mch = appUserMapper.selectByMchVersion(userId);
		if(null == mch){
			detail.put(Consts.RESULT, ErrorCode.FAILED);
			detail.put(Consts.RESULT_NOTE, "用户未登录");
			return detail;
		}
		userId = mch.getUserid()+"";
		AppUser appUser = appUserMapper.selectByPrimaryKey(Integer.parseInt(userId));
		AppLoanAppl appl = appLoanApplMapper.selectByitemCode(appUser.getUserName());
		Map<String, Object> map = new HashMap<>();
		map.put("cntType", "6");
		map.put("apprId", appl.getId());
		Map<String, Object> map1 = new HashMap<>();
		map1.put("cntType", "5");
		map1.put("apprId", appl.getId());
		AppLoanCtmCnt zh = appLoanCtmCntMapper.queryByType(map);
		AppLoanCtmCnt yx = appLoanCtmCntMapper.queryByType(map1);
		JSONArray dataList = new JSONArray();
		if (null != zh && "2".equals(zh.getCntDesc())) {
			JSONObject zhList = new JSONObject();
			zhList.put("authenType", "0");
			zhList.put("authenDesc", "信用卡账号已认证");
			zhList.put("authenStatus", "1");
			dataList.add(zhList);
		} else if (null != zh &&"1".equals(zh.getCntDesc())){
			JSONObject zhList = new JSONObject();
			zhList.put("authenType", "0");
			zhList.put("authenDesc", "信用卡账号已填写");
			zhList.put("authenStatus", "1");
			dataList.add(zhList);
		} else if (null == zh) {
			JSONObject zhList = new JSONObject();
			zhList.put("authenType", "0");
			zhList.put("authenDesc", "信用卡账号未填写");
			zhList.put("authenStatus", "0");
			dataList.add(zhList);
		}

		if (null != yx && "2".equals(yx.getCntDesc())) {
			JSONObject yxList = new JSONObject();
			yxList.put("authenType", "1");
			yxList.put("authenDesc", "信用卡邮箱已认证");
			yxList.put("authenStatus", "1");
			dataList.add(yxList);
		} else if (null != yx&&"1".equals(yx.getCntDesc())) {
			JSONObject yxList = new JSONObject();
			yxList.put("authenType", "1");
			yxList.put("authenDesc", "信用卡邮箱已填写");
			yxList.put("authenStatus", "1");
			dataList.add(yxList);
		} else if (null == yx) {
			JSONObject yxList = new JSONObject();
			yxList.put("authenType", "1");
			yxList.put("authenDesc", "信用卡邮箱未填写");
			yxList.put("authenStatus", "0");
			dataList.add(yxList);

		}
		detail.put("dataList", dataList);
		return detail;
	}

}
